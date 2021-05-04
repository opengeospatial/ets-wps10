package org.opengis.cite.wps10.level1;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.opengis.cite.wps10.CommonFixture;
import org.opengis.cite.wps10.DataFixture;
import org.opengis.cite.wps10.ErrorMessage;
import org.opengis.cite.wps10.ErrorMessageKeys;
import org.opengis.cite.wps10.SuiteAttribute;
import org.opengis.cite.wps10.util.NamespaceBindings;
import org.opengis.cite.wps10.util.XMLUtils;
import org.opengis.cite.validation.RelaxNGValidator;
import org.opengis.cite.validation.ValidationErrorHandler;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
//import java.net.URLDecoder;

/**
 * Includes various tests of capability 1.
 */
public class Capability1Tests extends DataFixture {

//	public static void main(String []args) throws IOException, URISyntaxException {
//		String URL 	= "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService?service=WPS%26request=GetCapabilities%26version=1.0.0%26Identifier=org.n52.wps.server.r.test.geo%26DataInputs=filename=fcu_ogc_wps";
//		String dURL = URLDecoder.decode(URL);
//		System.out.println(URL);
//		System.out.println(dURL);
//	}

    /**
     * Checks the behavior of the trim function.
     */
    @Test(description = "Implements ATC 1-2")
    public void trim() {
        String str = "  foo   ";
        Assert.assertTrue("foo".equals(str.trim()));
    }
    
    @Test(description = "Ricky")
    public void NotEmpty() {
    	
        String xpath = "count(//*)>0";
        String rs = this.CheckXPath2(xpath);
        Assert.assertTrue("true".equals(rs));
    }
    
   
	

    /**
     * Verify the test subject is a valid Atom feed.
     *
     * @throws SAXException
     *             If the resource cannot be parsed.
     * @throws IOException
     *             If the resource is not accessible.
     */
//    @Test(description = "Implements ATC 1-3")
//    public void docIsValidAtomFeed() throws SAXException, IOException {
//        URL schemaRef = getClass().getResource(
//                "/org/opengis/cite/wps10/rnc/atom.rnc");
//        RelaxNGValidator rngValidator = new RelaxNGValidator(schemaRef);
//        Source xmlSource = (null != testSubject)
//                ? new DOMSource(testSubject) : null;
//        rngValidator.validate(xmlSource);
//        ValidationErrorHandler err = rngValidator.getErrorHandler();
//        Assert.assertFalse(err.errorsDetected(),
//                ErrorMessage.format(ErrorMessageKeys.NOT_SCHEMA_VALID,
//                err.getErrorCount(), err.toString()));
//    }
}
