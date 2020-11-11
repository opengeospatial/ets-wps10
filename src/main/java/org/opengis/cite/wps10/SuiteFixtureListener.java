package org.opengis.cite.wps10;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;

import org.opengis.cite.wps10.SuiteAttribute;
import org.opengis.cite.wps10.TestRunArg;
import org.opengis.cite.wps10.util.ValidationUtils;
import org.opengis.cite.wps10.util.ClientUtils;
import org.opengis.cite.wps10.util.TestSuiteLogger;
import org.opengis.cite.wps10.util.URIUtils;
import org.opengis.cite.wps10.util.XMLUtils;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;

/**
 * A listener that performs various tasks before and after a test suite is run,
 * usually concerned with maintaining a shared test suite fixture. Since this
 * listener is loaded using the ServiceLoader mechanism, its methods will be
 * called before those of other suite listeners listed in the test suite
 * definition and before any annotated configuration methods.
 *
 * Attributes set on an ISuite instance are not inherited by constituent test
 * group contexts (ITestContext). However, suite attributes are still accessible
 * from lower contexts.
 *
 * @see org.testng.ISuite ISuite interface
 */
public class SuiteFixtureListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        processSuiteParameters(suite);
        //processXmlReference(suite);
        registerClientComponent(suite);
    }

    @Override
    public void onFinish(ISuite suite) {
        if (null != System.getProperty("deleteSubjectOnFinish")) {
            deleteTempFiles(suite);
            System.getProperties().remove("deleteSubjectOnFinish");
        }
    }

    /**
     * Processes test suite arguments and sets suite attributes accordingly. The
     * entity referenced by the {@link TestRunArg#IUT iut} argument is retrieved
     * and written to a File that is set as the value of the suite attribute
     * {@link SuiteAttribute#TEST_SUBJ_FILE testSubjectFile}.
     * 
     * @param suite
     *            An ISuite object representing a TestNG test suite.
     */
    void processSuiteParameters(ISuite suite) {
        Map<String, String> params = suite.getXmlSuite().getParameters();
        TestSuiteLogger.log(Level.CONFIG, "Suite parameters\n" + params.toString());
        String iutParam = params.get(TestRunArg.IUT.toString());
        Set<URI> schemaURIs = new HashSet<URI>();
        if ((null == iutParam) || iutParam.isEmpty()) {
            throw new IllegalArgumentException("Required test run parameter not found: " + TestRunArg.IUT.toString());
        }
        URI iutRef = URI.create(iutParam.trim());
        String iutRefString = params.get(TestRunArg.IUT.toString());      
		
        File entityFile = null;
        try {
            entityFile = URIUtils.dereferenceURI(iutRef);
            if (XMLUtils.isXMLSchema(entityFile)) {
    			params.put(TestRunArg.XSD.toString(), iutRefString);
    		} else {
    			params.put(TestRunArg.XML.toString(), iutRefString);
    		}
            schemaURIs.addAll(ValidationUtils.extractSchemaReferences(new StreamSource(entityFile), iutRefString));
        } catch (Exception iox) {
            //throw new RuntimeException("Failed to dereference resource located at " + iutRef, iox);        	
        }
        
		suite.setAttribute(SuiteAttribute.XML.getName(), entityFile);	
		suite.setAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName(), entityFile);
        TestSuiteLogger.log(Level.FINE, String.format("Wrote test subject to file: %s (%d bytes)",
                entityFile.getAbsolutePath(), entityFile.length()));
        
        Document iutDoc = null;
        try {
            iutDoc = URIUtils.parseURI(entityFile.toURI());
        } catch (Exception x) {
            throw new RuntimeException("Failed to parse resource retrieved from " + iutRef, x);
        }
        
        	
        suite.setAttribute(SuiteAttribute.TEST_SUBJECT.getName(), iutDoc);
        suite.setAttribute(SuiteAttribute.TEST_SUBJECT_URI.getName(), URI.create(iutRefString));
        if (TestSuiteLogger.isLoggable(Level.FINE)) {
            StringBuilder logMsg = new StringBuilder("Parsed resource retrieved from ");
            logMsg.append(iutRef).append("\n");
            logMsg.append(XMLUtils.writeNodeToString(iutDoc));
            TestSuiteLogger.log(Level.FINE, logMsg.toString());
        }
        
        //Set RawDataOutput test file
        String executeRequestRawDataOutputParam = params.get(TestRunArg.EXECUTE_REQUEST_FILE_RAW_DATA_OUTPUT.toString());
        URI executeRequestRawDataOutputRef = URI.create(executeRequestRawDataOutputParam.trim());
        File executeRequestRawDataOutputFile = null;
        try {
        	executeRequestRawDataOutputFile = URIUtils.dereferenceURI(executeRequestRawDataOutputRef);
        }catch (Exception ex) {
            throw new RuntimeException("Failed to dereference resource located at " + executeRequestRawDataOutputRef, ex);        	
        }
        Document executeRequestRawDataOutputDoc = null;
        try {
            executeRequestRawDataOutputDoc = URIUtils.parseURI(executeRequestRawDataOutputFile.toURI());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse resource retrieved from " + executeRequestRawDataOutputRef, ex);
        }
        suite.setAttribute(SuiteAttribute.EXECUTE_REQUEST_FILE_RAW_DATA_OUTPUT.getName(), executeRequestRawDataOutputDoc);

    }

    /**
     * A client component is added to the suite fixture as the value of the
     * {@link SuiteAttribute#CLIENT} attribute; it may be subsequently accessed
     * via the {@link org.testng.ITestContext#getSuite()} method.
     *
     * @param suite
     *            The test suite instance.
     */
    void registerClientComponent(ISuite suite) {
        Client client = ClientUtils.buildClient();
        if (null != client) {
            suite.setAttribute(SuiteAttribute.CLIENT.getName(), client);
        }
    }

    /**
     * Deletes temporary files created during the test run if TestSuiteLogger is
     * enabled at the INFO level or higher (they are left intact at the CONFIG
     * level or lower).
     *
     * @param suite
     *            The test suite.
     */
    void deleteTempFiles(ISuite suite) {
        if (TestSuiteLogger.isLoggable(Level.CONFIG)) {
            return;
        }
        File testSubjFile = (File) suite.getAttribute(SuiteAttribute.TEST_SUBJ_FILE.getName());
        if (testSubjFile.exists()) {
            testSubjFile.delete();
        }
    }

    void processXmlReference(ISuite suite) {
		Map<String, String> params = suite.getXmlSuite().getParameters();
		TestSuiteLogger.log(Level.CONFIG, String.format("Suite parameters:\n %s", params));
		Set<URI> schemaURIs = new HashSet<URI>();
		String xsdURI = params.get(TestRunArg.XSD.toString());
		if (null != xsdURI && !xsdURI.isEmpty()) {
			// was submitted as iut argument value via POST
			schemaURIs.add(URI.create(xsdURI));
			suite.setAttribute(SuiteAttribute.SCHEMA_LOC_SET.getName(), schemaURIs);
			return;
		}
		String xmlURI = params.get(TestRunArg.XML.toString());
		if (null == xmlURI || xmlURI.isEmpty()) {
			throw new IllegalArgumentException("Missing XML resource (document or application schema).");
		}
		File xmlFile = null;
		try {
			URI uriXml = URI.create(xmlURI);
			xmlFile = URIUtils.resolveURIAsFile(uriXml);
			if (null == xmlFile || !xmlFile.exists()) {
				throw new IllegalArgumentException("Failed to dereference URI: " + xmlURI);
			}
			if (XMLUtils.isXMLSchema(xmlFile)) {
				params.put(TestRunArg.XSD.toString(), xmlURI);
				schemaURIs.add(URI.create(xmlURI));
			} else {
				schemaURIs.addAll(ValidationUtils.extractSchemaReferences(new StreamSource(xmlFile), xmlURI));
				suite.setAttribute(SuiteAttribute.XML.getName(), xmlFile);

				Document iutDoc = null;
				try {
					//iutDoc = URIUtils.parseURI(xmlFile.toURI());
					iutDoc = URIUtils.parseURI(uriXml);
				} catch (Exception x) {
					throw new RuntimeException("Failed to parse resource retrieved from " + xmlURI, x);
				}
				suite.setAttribute(SuiteAttribute.TEST_SUBJECT.getName(), iutDoc);
				TestSuiteLogger.log(Level.FINE, "Wrote XML document to " + xmlFile.getAbsolutePath());
				suite.setAttribute(SuiteAttribute.TEST_SUBJECT_URI.getName(), URI.create(xmlURI));
			}
		} catch (IOException iox) {
			throw new RuntimeException("Failed to read resource obtained from " + xmlURI, iox);
		} catch (XMLStreamException xse) {
			throw new RuntimeException("Failed to find schema reference in source: " + xmlFile.getAbsolutePath(), xse);
		}
		suite.setAttribute(SuiteAttribute.SCHEMA_LOC_SET.getName(), schemaURIs);
		TestSuiteLogger.log(Level.FINE, String.format("Schema references: %s", schemaURIs));
	}

//	void processSchematronSchema(ISuite suite) {
//		Map<String, String> params = suite.getXmlSuite().getParameters();
//		String schRef = params.get(TestRunArg.SCH.toString());
//		if ((schRef != null) && !schRef.isEmpty()) {
//			URI schURI = URI.create(params.get(TestRunArg.SCH.toString()));
//			suite.setAttribute(SuiteAttribute.SCHEMATRON.getName(), schURI);
//		}
//	}

	
}
