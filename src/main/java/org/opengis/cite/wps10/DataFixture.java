package org.opengis.cite.wps10;

import net.sf.saxon.s9api.*;
import org.apache.xerces.xs.XSModel;
import org.opengis.cite.validation.XmlSchemaCompiler;
import org.opengis.cite.wps10.util.NamespaceBindings;
import org.opengis.cite.wps10.util.ValidationUtils;
import org.opengis.cite.wps10.util.XMLUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * A supporting base class that provides a common fixture for validating data
 * sets. The configuration methods are invoked before any that may be defined in
 * a subclass.
 */
public class DataFixture {

    /**
     * Files containing tested subject.
     */
    protected File dataFile;
    protected Document originalSubject;
    protected Document testSubject;
    protected URI testSubjectUri;
    
    protected String Resource_GML_Path = "/org/opengis/cite/om20/xsd/opengis/gml/3.2.1/gml-3.2.1.xsd";
    protected String Resource_SWE_Path = "/org/opengis/cite/sweCommon/2.0/swe_2.0.1_flatten/swe_2.0.1.xsd";
    /**
     * An XSModel object representing a GML application schema.
     */
    protected XSModel model;
    protected URI executeHttpGetUri;
    protected Document executeRequestFileRawDataOutputSubject;
    protected Document executeRequestFileResponseDocumentOutputSubject;
    protected Document executeRequestFileUpdatingResponseDocumentOutputSubject;
    
	
    public DataFixture() {
    }
    
    @BeforeClass(alwaysRun = true)
    public void obtainTestSubject(ITestContext testContext){
    	Assert.assertTrue(
                testContext.getSuite().getAttributeNames()
                        .contains(SuiteAttribute.XML.getName()),
                "No data to validate.");
        this.dataFile = (File) testContext.getSuite().getAttribute(
                SuiteAttribute.XML.getName());
        this.model = (XSModel) testContext.getSuite().getAttribute(
                SuiteAttribute.XSMODEL.getName());
        
        Object obj = testContext.getSuite().getAttribute(
                SuiteAttribute.TEST_SUBJECT.getName());
        if ((null != obj) && Document.class.isAssignableFrom(obj.getClass())) {
            this.testSubject = Document.class.cast(obj);
            originalSubject = Document.class.cast(obj);
        }
        
        Object uriObj = testContext.getSuite().getAttribute(
                SuiteAttribute.TEST_SUBJECT_URI.getName());
        if ((null != uriObj)){        	
            this.testSubjectUri = URI.class.cast(uriObj);
            System.out.println(this.testSubjectUri.toString());        	
        }
        
        /* START OF PARAMETERS */        
        Object excuteHttpGetUriObj = testContext.getSuite().getAttribute(
                SuiteAttribute.EXECUTE_HTTP_GET_URI.getName());
        if ((null != excuteHttpGetUriObj)){        	
            this.executeHttpGetUri = URI.class.cast(excuteHttpGetUriObj);
            System.out.println(this.executeHttpGetUri.toString());        	
        }
        
        Object executeRequestFileRawDataOutputObj = testContext.getSuite().getAttribute(SuiteAttribute.EXECUTE_REQUEST_RAW_DATA_URI.getName());
        if((null != executeRequestFileRawDataOutputObj) && Document.class.isAssignableFrom(executeRequestFileRawDataOutputObj.getClass())) {
        	this.executeRequestFileRawDataOutputSubject = Document.class.cast(executeRequestFileRawDataOutputObj);
        }
        
        Object executeRequestFileResponseDocumentOutputObj = testContext.getSuite().getAttribute(SuiteAttribute.EXECUTE_REQUEST_RESPONSE_DOCUMENT_URI.getName());
        if((null != executeRequestFileResponseDocumentOutputObj) && Document.class.isAssignableFrom(executeRequestFileResponseDocumentOutputObj.getClass())) {
        	this.executeRequestFileResponseDocumentOutputSubject = Document.class.cast(executeRequestFileResponseDocumentOutputObj);
        }
        
        Object executeRequestFileUpdatingResponseDocumentOutputObj = testContext.getSuite().getAttribute(SuiteAttribute.EXECUTE_REQUEST_UPDATING_RESPONSE_DOCUMENT_URI.getName());
        if((null != executeRequestFileUpdatingResponseDocumentOutputObj) && Document.class.isAssignableFrom(executeRequestFileUpdatingResponseDocumentOutputObj.getClass())) {
        	this.executeRequestFileUpdatingResponseDocumentOutputSubject = Document.class.cast(executeRequestFileUpdatingResponseDocumentOutputObj);
        }
        
        /* CLOSE */
    }
    /**
     * A configuration method ({@code BeforeClass}) that initializes the test
     * fixture as follows:
     * <ol>
     * <li>Obtain the GML data set from the test context. The suite attribute
     * {@link org.opengis.cite.iso19136.SuiteAttribute#GML} should evaluate to a
     * {@code File} object containing the GML data. If no such file reference
     * exists the tests are skipped.</li>
     * <li>Obtain the schema model from the test context. The suite attribute
     * {@link org.opengis.cite.iso19136.SuiteAttribute#XSMODEL model} should
     * evaluate to an {@code XSModel} object representing the GML application
     * schema.</li>
     * </ol>
     * 
     * @param testContext
     *            The test (group) context.
     */
//    @BeforeClass(alwaysRun = true)
//    public void initDataFixture(ITestContext testContext) {
//        Assert.assertTrue(
//                testContext.getSuite().getAttributeNames()
//                        .contains(SuiteAttribute.XML.getName()),
//                "No data to validate.");
//        this.dataFile = (File) testContext.getSuite().getAttribute(
//                SuiteAttribute.XML.getName());
//        this.model = (XSModel) testContext.getSuite().getAttribute(
//                SuiteAttribute.XSMODEL.getName());
//        
//        Object obj = testContext.getSuite().getAttribute(
//                SuiteAttribute.TEST_SUBJECT.getName());
//        if ((null != obj) && Document.class.isAssignableFrom(obj.getClass())) {
//            this.testSubject = Document.class.cast(obj);
//        }
//    }

    /**
     * Sets the data file. This is a convenience method intended to facilitate
     * unit testing.
     * 
     * @param dataFile
     *            A File containing the data to be validated.
     */
    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Sets the schema model (for unit testing purposes).
     * 
     * @param xsModel
     *            An XSModel object representing a GML application schema.
     */
    public void setSchemaModel(XSModel xsModel) {
        this.model = xsModel;
    }

    /**
     * Generates an XPath expression to find all instances of the given elements
     * in the data being validated. The supplied namespace bindings will be
     * supplemented if necessary.
     * 
     * @param elemNames
     *            A list of qualified names corresponding to element
     *            declarations.
     * @param namespaceBindings
     *            A collection of namespace bindings required to evaluate the
     *            XPath expression, where each entry maps a namespace URI (key)
     *            to a prefix (value).
     * @return An XPath (1.0) expression.
     */
    public String generateXPathExpression(List<QName> elemNames,
            Map<String, String> namespaceBindings) {
        StringBuilder xpath = new StringBuilder();
        ListIterator<QName> itr = elemNames.listIterator();
        while (itr.hasNext()) {
            QName qName = itr.next();
            String namespace = qName.getNamespaceURI();
            String prefix = namespaceBindings.get(namespace);
            if (null == prefix) {
                prefix = (namespace.equals(Namespaces.TargetStandard)) ? Namespaces.TargetStandardPrefix : "ns"
                        + itr.previousIndex();
                namespaceBindings.put(namespace, prefix);
            }
            xpath.append("//").append(prefix).append(":");
            xpath.append(qName.getLocalPart());
            if (itr.hasNext())
                xpath.append(" | "); // union operator
        }
        return xpath.toString();
    }
    

	public File GetFileViaResourcePath(String resourcePath) {
		try {
        InputStream in = this.getClass().getResourceAsStream(resourcePath);
        if (in == null) {
            return null;
        }

        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
        tempFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    } catch (IOException e) {
			URL xsdPath = this.getClass().getResource(resourcePath);
			File file = new File(xsdPath.toString().substring(5));
			return file;
    }
	}

	/**
	 * Create Validator for checking XML file against XML Schema file
	 * 
	 * @param xsdPath URL path of the XSD file
	 * @return schema validator
	 * @throws XMLStreamException XMLStreamException
	 * @throws IOException IOException
	 * @throws SAXException SAX Error
	 */
	public Validator CreateValidator(URL xsdPath) throws XMLStreamException, SAXException, IOException {
		Schema schema;
		Set<URI> schemaRefs;
		schemaRefs = ValidationUtils.extractSchemaReferences(new StreamSource(this.dataFile),
				this.testSubjectUri.toString());
		XmlSchemaCompiler xsdCompiler = new XmlSchemaCompiler(xsdPath);
		schema = xsdCompiler.compileXmlSchema(schemaRefs.toArray(new URI[schemaRefs.size()]));
		return schema.newValidator();
	}

	/**
	 * Evaluates an XPath 2.0 expression using the Saxon s9api interfaces
	 * modified version
	 * 
	 * @param xmlFile
	 *            The XML file.
	 * @param expr
	 *            The XPath expression to be evaluated.
	 * @param nsBindings
	 *            A collection of namespace bindings required to evaluate the
	 *            XPath expression, where each entry maps a namespace URI (key)
	 *            to a prefix (value); this may be {@code null} if not needed.
	 * @return An XdmValue object representing a value in the XDM data model;
	 *         this is a sequence of zero or more items, where each item is
	 *         either an atomic value or a node.
	 * @throws SaxonApiException SaxonApiException
	 */
	public static XdmValue evaluateXPath2Modified(File xmlFile, String expr, Map<String, String> nsBindings)
			throws SaxonApiException {
		Processor proc = new Processor(false);
		XPathCompiler compiler = proc.newXPathCompiler();
		if (null != nsBindings) {
			for (String nsURI : nsBindings.keySet()) {
				compiler.declareNamespace(nsBindings.get(nsURI), nsURI);
			}
		}
		XPathSelector xpath = compiler.compile(expr).load();
		DocumentBuilder builder = proc.newDocumentBuilder();
		XdmNode node = builder.build(xmlFile);

		xpath.setContextItem(node);
		return xpath.evaluate();
	}

	/**
	 * Check XPath2.0 modified version
	 * 
	 * @param xpath2
	 *            String denoting an xpath syntax
	 * @param xmlFile
	 *            the File xml
	 * @return XdmValue converted to string
	 */
	public XdmValue CheckXPath2Modified(String xpath2, File xmlFile) {
		XdmValue xdmValue = null;
		try {
			xdmValue = evaluateXPath2Modified(xmlFile, xpath2, NamespaceBindings.getStandardBindings());
		} catch (SaxonApiException e) {
			e.printStackTrace();
		}
		return xdmValue;
	}

	/**
	 * Return <code>"true"</code> if candidate element node satisfy all three of
	 * the following conditions: Condition 1: The name of the candidate node
	 * matches the specified <code>nodeName</code> or matches the name of an
	 * element in a substitution group headed by an element named
	 * <code>nodeName</code>. Condition 2: derives-from(AT, ET) is true, where
	 * AT is the type annotation of the candidate node and ET is the schema type
	 * declared for element <code>nodeName</code> in the in-scope element
	 * declarations. Condition 3: If the element declaration for
	 * <code>nodeName</code> in the in-scope element declarations is not
	 * nillable, then the nilled property of the <code>candidateNode</code> is
	 * false
	 * 
	 * @param candidateNode
	 *            the candidate node for testing
	 * @param nodeName
	 *            name of node for comparing
	 * @param schemaFile
	 *            the location of schema used for testing
	 * @return return string value "true" or "false"
	 * @throws SaxonApiException SaxonApiException
	 */
	public String SchemaElement(String candidateNode, String nodeName, File schemaFile) throws SaxonApiException {
		// ---Test condition 1--- candidateNode matches the nodeName, or matches
		// the name of an element in a substitution group headed by nodeName
		String test1_result = "true";

		///// check whether or not "candidateNode" matches the specified
		///// nodeName, if it matches,
		///// the attribute "abstract" of nodeName must be false or does not
		///// exist.
		if (candidateNode.equals(nodeName)) {
			String xpath_t1_1 = String.format("//xs:schema/xs:element[@name=\"%s\"]/@abstract", nodeName.split(":")[1]);
			String bool_xpath_t1_1 = String.format("boolean(%s)", xpath_t1_1);
			if (CheckXPath2Modified(bool_xpath_t1_1, schemaFile).equals("true")) {
				if (CheckXPath2Modified(xpath_t1_1, schemaFile).toString().split("=")[1].equals("\"true\"")) {
					test1_result = "false";
				}
			}
		} else {
			///// check whether or not "candidateNode" matches the name of an
			///// element
			///// in a substitution group headed by "nodeName".
			String sub_name = "";
			String name = String.format("\"%s\"", candidateNode.split(":")[1]);
			do {
				String xpath_t1_2 = String.format("//xs:schema/xs:element[@name=%s]/@substitutionGroup", name);
				String result_xpath_t1_2 = CheckXPath2Modified(xpath_t1_2, schemaFile).toString();
				if (result_xpath_t1_2.contains("XdmEmptySequence")) {
					test1_result = "false";
					break;
				}
				sub_name = result_xpath_t1_2.split("=")[1];
				if (sub_name.equals(String.format("\"%s\"", nodeName))) {
					break;
				} else {
					if (sub_name.equals("\"gml:AbstractGML\"") || sub_name.equals("\"gml:AbstractObject\"")) {
						test1_result = "false";
						break;
					}
					if (sub_name.equals("\"swe:AbstractSWE\"")) {
						test1_result = "false";
						break;
					}
					name = "\"".concat(sub_name.split(":")[1]);
				}
			} while (true);
		}

		if (test1_result.equals("false")) {
			return "false";
		}

		// ---Test condition 2--- derives-from(AT, ET) is true.
		// According to
		// "https://www.w3.org/TR/xpath20/#prod-xpath-SchemaElementTest",
		// section 2.5.4,
		// derives-from(AT, ET) returns true if ET IS A KNOWN TYPE and ANY OF
		// the following three conditions is true:
		// condition 1 - AT is a schema type found in the in-scope schema
		// definitions, and is the same as ET or is derived by restriction or
		// extension from ET
		// or, condition 2 - AT is a schema type not found in the in-scope
		// schema definitions, and an implementation-dependent mechanism is able
		// to determine that AT is derived by restriction from ET
		// or, condition 3 - There exists some schema type IT such that
		// derives-from(IT, ET) and derives-from(AT, IT) are true.
		String test2_result = "true";

		///// Get the type of candidateNode (AT-Actual Type) and of nodeName
		///// (ET-Expected Type).
		String xpath_t2_1 = String.format("//xs:schema/xs:element[@name=\"%s\"]/@type", candidateNode.split(":")[1]);
		String candidateNode_type = CheckXPath2Modified(xpath_t2_1, schemaFile).toString();
		String xpath_t2_2 = String.format("//xs:schema/xs:element[@name=\"%s\"]/@type", nodeName.split(":")[1]);
		String nodeName_type = CheckXPath2Modified(xpath_t2_2, schemaFile).toString();

		///// Check whether or not ET is a known type
		String xpath_t2_3 = String.format("boolean(//*[@name=%s])", String.format("\"%s", nodeName_type.split(":")[1]));
		if (CheckXPath2Modified(xpath_t2_3, schemaFile).equals("false")) {
			test2_result = "false";
		}

		///// Check whether or not AT is a schema type found in the in-scope
		///// schema definitions
		String xpath_t2_4 = String.format("boolean(//*[@name=%s])",
				String.format("\"%s", candidateNode_type.split(":")[1]));
		if (CheckXPath2Modified(xpath_t2_4, schemaFile).equals("false")) {
			test2_result = "false";
		}

		///// check whether or not AT is the same as ET or is derived by
		///// restriction or extension from ET
		if (!candidateNode_type.equals(nodeName_type)) {
			String type_element = "";
			String name_element = String.format("\"%s", candidateNode_type.split(":")[1]);
			do {
				String xpath_t2_5 = String.format("//*[@name=%s]//@base", name_element);
				String result_xpath_t2_5 = "";
				if (CheckXPath2Modified(xpath_t2_5, schemaFile).size() > 1) {
					result_xpath_t2_5 = CheckXPath2Modified(xpath_t2_5, schemaFile).itemAt(0).getStringValue();
					type_element = String.format("\"%s\"", result_xpath_t2_5);
				} else if (CheckXPath2Modified(xpath_t2_5, schemaFile).size() == 1) {
					result_xpath_t2_5 = CheckXPath2Modified(xpath_t2_5, schemaFile).toString();
					type_element = result_xpath_t2_5.split("=")[1];
				} else {
					test2_result = "false";
					break;
				}
				if (type_element.equals(String.format("%s", nodeName_type.split("=")[1]))) {
					break;
				} else {
					if (type_element.equals("\"gml:AbstractGMLType\"")) {
						test2_result = "false";
						break;
					}
					name_element = "\"".concat(type_element.split(":")[1]);
				}
			} while (true);
		}

		if (test2_result.equals("false")) {
			return "false";
		}

		// ---Test condition 3---
		// If "candidateNode" is not nillable, then the "nodeName" is not
		// nillable,
		// and vice versa
		String test3_result = "false";

		String xpath_t3_1 = String.format("//xs:schema/xs:element[@name=\"%s\"]/@nillable", nodeName.split(":")[1]);
		String xpath_t3_2 = String.format("//xs:schema/xs:element[@name=\"%s\"]/@nillable",
				candidateNode.split(":")[1]);
		String result_xpath_t3_1 = CheckXPath2Modified(xpath_t3_1, schemaFile).toString();
		String result_xpath_t3_2 = CheckXPath2Modified(xpath_t3_2, schemaFile).toString();

		if (result_xpath_t3_1.contains("XdmEmptySequence") && result_xpath_t3_2.contains("XdmEmptySequence")) {
			test3_result = "true";
		} else if (result_xpath_t3_1.equals(result_xpath_t3_2)) {
			test3_result = "true";
		}

		if (test3_result.equals("false")) {
			return "false";
		}

		String final_result = "";
		if (test1_result.equals("true") && test2_result.equals("true") && test3_result.equals("true")) {
			final_result = "true";
		} else {
			final_result = "false";
		}

		return final_result;
	}

	/**
	 * Create validator from xsd file
	 * 
	 * @param xsdPath
	 *            A URL that denotes the location of a XML schema.
	 * @throws SAXException SAXException
	 * @throws URISyntaxException URISyntaxException
	 * @throws IOException IOException
	 * @return schema validator
	 */
	public Validator CreateValidatorFromXSD(URL xsdPath) throws SAXException, IOException, URISyntaxException {
		XmlSchemaCompiler compiler = new XmlSchemaCompiler(xsdPath);
		Schema schema = compiler.compileXmlSchema(xsdPath.toURI());
		return schema.newValidator();
	}

	/**
	 * Check XPath2.0
	 * 
	 * @param xpath
	 *            String denoting an xpath syntax
	 * @return XdmValue converted to string
	 */
	public String CheckXPath2(String xpath) {
		XdmValue xdmValue = null;
		try {
			xdmValue = XMLUtils.evaluateXPath2(new DOMSource(this.testSubject), xpath,
					NamespaceBindings.getStandardBindings());
		} catch (SaxonApiException e) {
			e.printStackTrace();
		};
		return xdmValue.toString();
	}

	/**
	 * Check Observation Type Measurement in schematron document
	 * resultTypeConsistent.sch
	 * 
	 * @param href
	 *            the value of om:type/xlink:href to make the context
	 * @return return list of values containing "true" or "false"
	 */
	public List<String> CheckObservationTypeMeasurement(String href) {
		List<String> results = new ArrayList<String>();
		String context = String.format("//om:OM_Observation[om:type/@xlink:href='%s']", href);
	
		int count_observation = Integer.parseInt((CheckXPath2(String.format("count(%s)", context))));
		for (int i = 1; i <= count_observation; i++) {
			String uom_value = String.format("(%s/om:result/@uom)[%s]", context,i);
			String result_text = String.format("(%s/om:result/text())[%s]", context,i);
			String xpath = String.format(
					"(((%s castable as xs:string) and (string-length(%s) > 0) and (not(matches(%s, \"[: \\n\\r\\t]+\"))))  or ((%s castable as xs:anyURI) and matches(%s , \"([a-zA-Z][a-zA-Z0-9\\-\\+\\.]*:|\\.\\./|\\./|#).*\"))) and (%s castable as xs:double)",
					uom_value, uom_value, uom_value, uom_value, uom_value, result_text);
			results.add(CheckXPath2(xpath));
		}
		return results;
	}

	/**
	 * Check Observation Type Category in schematron document
	 * resultTypeConsistent.sch
	 * 
	 * @param href
	 *            the value of om:type/xlink:href to make the context
	 * @return return list of values containing "true" or "false"
	 */
	public List<String> CheckObservationTypeCategory(String href) {
		List<String> results = new ArrayList<String>();	
		String context = String.format("//om:OM_Observation[om:type/@xlink:href='%s']", href);
		int count_observation = Integer.parseInt((CheckXPath2(String.format("count(%s)", context))));
		
		for (int i = 1; i <= count_observation; i++) {
			boolean test1 = true;
			boolean test2 = false;
			
			if (CheckXPath2(String.format("(%s/om:result/@xlink:href)[%s]", context, i)).contains("XdmEmptySequence")
					|| CheckXPath2(String.format("(%s/om:result/@xlink:title)[%s]", context, i)).contains("XdmEmptySequence")) {
				test1 = false;
			}
			//the result cannot have any child element nor text
			
			if (CheckXPath2(String.format("(%s/om:result/*)[%s]", context, i)).contains("XdmEmptySequence")
					&& CheckXPath2(String.format("(%s/om:result/text())[%s]", context, i)).contains("XdmEmptySequence")) {
				test2 = true;
			}
			if (test1 && test2) {
				results.add("true");
			} else {
				results.add("false");
			}
		}
		return results;
	}

	/**
	 * Check Observation Type Count in schematron document
	 * resultTypeConsistent.sch
	 * 
	 * @param href
	 *            the value of om:type/xlink:href to make the context
	 * @return return list of values containing "true" or "false"
	 */
	public List<String> CheckObservationTypeCount(String href) {
		List<String> results = new ArrayList<String>();
		String context = String.format("//om:OM_Observation[om:type/@xlink:href='%s']", href);
		int count_observation = Integer.parseInt((CheckXPath2(String.format("count(%s)", context))));
		for (int i = 1; i <= count_observation; i++) {
			results.add(CheckXPath2(String.format("(%s/om:result/text())[%s] castable as xs:integer)", context, i)));
		}
		return results;
	}

	/**
	 * Check Observation Type Truth in schematron document
	 * resultTypeConsistent.sch
	 * 
	 * @param href
	 *            the value of om:type/xlink:href to make the context
	 * @return return list of values containing "true" or "false"
	 */
	public List<String> CheckObservationTypeTruth(String href) {
		List<String> results = new ArrayList<String>();
		String context = String.format("//om:OM_Observation[om:type/@xlink:href='%s']", href);
		int count_observation = Integer.parseInt((CheckXPath2(String.format("count(%s)", context))));
		for (int i = 1; i<= count_observation; i++) {
			results.add(CheckXPath2(String.format("(%s/om:result/text())[%s] castable as xs:boolean", context, i)));
		}
		return results;
	}
	
	public List<String> GetResultTypeHref() {
		int count_observation = Integer.parseInt((CheckXPath2("count(//om:OM_Observation/om:type/@xlink:href)")));
		List<String> list_href = new ArrayList<String>();
		for (int i=1; i <= count_observation; i++ ) {
			list_href.add(CheckXPath2(String.format("string((//om:OM_Observation/om:type/@xlink:href)[%s])", i)));
		}
		return list_href;
	}
	
	

}
