package org.opengis.cite.wps10;

import net.sf.saxon.s9api.XdmValue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.cite.wps10.util.XMLUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Verifies the results of executing a test run using the main controller
 * (TestNGController).
 * 
 */
public class VerifyTestNGController {

    private static DocumentBuilder docBuilder;
    private Properties testRunProps;

    @BeforeClass
    public static void initParser() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
        dbf.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);
        docBuilder = dbf.newDocumentBuilder();
    }

    @Before
    public void loadDefaultTestRunProperties()
            throws InvalidPropertiesFormatException, IOException {
        this.testRunProps = new Properties();
        this.testRunProps.loadFromXML(getClass().getResourceAsStream(
                "/test-run-props.xml"));
    }

    @Test
    public void doTestRun() throws Exception {
    	//URL testSubject = getClass().getResource("/atom-feed-2.xml");
    	URL rawSubject = getClass().getResource("/wps_execute_request_complexdata.xml");
    	URL responseSubject = getClass().getResource("/wps_execute_request_response_document.xml");
    	URL updateResponseSubject = getClass().getResource("/wps_execute_request_updating_response_document.xml");
    	
        this.testRunProps.setProperty(TestRunArg.IUT.toString(), "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService");
        this.testRunProps.setProperty(TestRunArg.EXECUTE_HTTP_GET_URI.toString(), "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService?service=WPS%26request=GetCapabilities%26version=1.0.0%26Identifier=org.n52.wps.server.r.test.geo%26DataInputs=filename=fcu_ogc_wps");
        this.testRunProps.setProperty(TestRunArg.EXECUTE_REQUEST_RAW_DATA_URI.toString(), rawSubject.toURI().toString());
        this.testRunProps.setProperty(TestRunArg.EXECUTE_REQUEST_RESPONSE_DOCUMENT_URI.toString(), responseSubject.toURI().toString());
        this.testRunProps.setProperty(TestRunArg.EXECUTE_REQUEST_UPDATING_RESPONSE_DOCUMENT_URI.toString(), updateResponseSubject.toURI().toString());
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
        this.testRunProps.storeToXML(outStream, "Integration test");
        Document testRunArgs = docBuilder.parse(new ByteArrayInputStream(
                outStream.toByteArray()));
        TestNGController controller = new TestNGController();
        Source results = controller.doTestRun(testRunArgs);
        String xpath = "/testng-results/@failed";
        XdmValue failed = XMLUtils.evaluateXPath2(results, xpath, null);
        int numFailed = Integer.parseInt(failed.getUnderlyingValue()
                .getStringValue());
        assertEquals("Unexpected number of fail verdicts.", 0, numFailed);		 
    }
}
