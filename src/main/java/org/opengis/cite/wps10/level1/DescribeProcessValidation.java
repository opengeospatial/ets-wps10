package org.opengis.cite.wps10.level1;

import net.sf.saxon.s9api.*;
import org.opengis.cite.wps10.DataFixture;
import org.opengis.cite.wps10.Namespaces;
import org.opengis.cite.wps10.util.ValidationUtils;
import org.opengis.cite.wps10.util.XMLUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DescribeProcessValidation extends DataFixture{
//	String serviceURL 	= this.testSubjectUri.toString();
//	String service_url1 = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
//	String service_url2 = "http://93.187.166.52:8081/geoserver/ows";
	String processId = "";
	
	@BeforeClass
	public void beforeClass() throws IOException, ParserConfigurationException, SAXException, SaxonApiException {
		String serviceURL 	= testSubjectUri.toString();
		
		String response = sendGetRequest(serviceURL, "?service=wps&request=GetCapabilities&language=en-US");
		// Read xml string using Xpath2
		InputSource sourceWR = new InputSource(new StringReader(response));
		DocumentBuilderFactory dbfWR = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder dbWR = dbfWR.newDocumentBuilder();
		Document documentWR = dbWR.parse(sourceWR);	
		XdmValue xdmValue = XMLUtils.evaluateXPath2(new DOMSource(documentWR), "//ows:Identifier", getStandardBindings());
		if(xdmValue.size() != 0) {
			processId = xdmValue.itemAt(0).getStringValue();	
		}
		
		CheckServiceHasProcess();
	}
	
	private void CheckServiceHasProcess() {
		if(processId.isEmpty()) {
			throw new SkipException("Skip the DescribeProcess validation test due to no process offering.");
		}
	}
	
	/**
	 * A.4.3.1 Accept DescribeProcess HTTP GET transferred operation requests
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@Test(groups = "A.4.3. DescribeProcess operation test module", description = "Verify that a server accepts at least HTTP GET transferred requests for the DescribeProcess operation")
	public void HTTPGETTransferredKVPDescribeProcessValidation() throws IOException, URISyntaxException {
		String serviceURL 	= testSubjectUri.toString();
		String param = "?service=wps&version=1.0.0&request=DescribeProcess&Identifier=" + processId;
		HttpURLConnection connection = GetConnection(serviceURL, param);
	 
		connection.setRequestMethod("GET");
	  
		Integer responseCode = connection.getResponseCode();
	  
		boolean result = (responseCode == HttpURLConnection.HTTP_OK );
		Assert.assertTrue(result, "The server does not accepts HTTP GET transferred requests for the DescribeProcess operation");
	}
	
	
	/**
	 * A.4.3.2 Accept DescribeProcess HTTP POST transferred operation requests
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws SaxonApiException 
	 */
	@Test(groups = "A.4.3. DescribeProcess operation test module", description = "Verify that a server accepts at HTTP POST transferred requests for the DescribeProcess operation")
	public void HTTPPOSTTransferredXMLDescribeProcessValidation() throws IOException, URISyntaxException, ParserConfigurationException, SAXException, SaxonApiException {
		////Check correct POST operation request
		String serviceURL 	= testSubjectUri.toString();
		
		String msgCorrect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<wps:DescribeProcess service=\"WPS\" version=\"1.0.0\" "
				+ "xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" "
				+ "xmlns:ows=\"http://www.opengis.net/ows/1.1\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsDescribeProcess_request.xsd\">"
				+ "<ows:Identifier>" + processId + "</ows:Identifier>"
				+ "</wps:DescribeProcess>";
		String responseCorrect = postMessage(msgCorrect, serviceURL);
		
		//Read xml string using Xpath2
		InputSource source = new InputSource(new StringReader(responseCorrect));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(source);
		String candidateNode = CheckXPath2("//wps:ProcessDescriptions", document);
		boolean correctResultRequest = (candidateNode.contains("XdmEmptySequence") ? false : true);
		
		Assert.assertTrue(correctResultRequest, "The server does not accepts at HTTP POST transferred requests for the DescribeProcess operation");
	}
	
	/**
	 * A.4.3.3 DescribeProcess operation response
	 * @throws IOException 
	 * @throws SAXException 
	 */
	@Test(groups = "A.4.3. DescribeProcess operation test module", description = "Verify that a server satisfies all requirements on the DescribeProcess operation response.")
	public void DescribeProcessResponseValidation() throws IOException, SAXException {
		//Set KVP request
		String serviceURL 	= testSubjectUri.toString();
		String param = "?service=wps&version=1.0.0&request=DescribeProcess&Identifier" + processId;
		String response = sendGetRequest(serviceURL, param);
		
		//Check response with xsd file
		String xsdPath = "xsd/opengis/wps/1.0/wpsDescribeProcess_response.xsd";
		boolean resultValidation = validateXMLString(response, xsdPath);
		
		Assert.assertTrue(resultValidation, "The server does not satisfies all requirements on the DescribeProcess operation response");
	}
	
	/**
	 * A.4.3.4 Language selection
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws SaxonApiException 
	 */
    @Test(groups = "A.4.3. DescribeProcess operation test module",description = "Verify that a server satisfies the requirements for using the Language parameter for the DescribeProcess operation." )
    public void LanguageSelectionGetCapabilitiesValidation() throws IOException, ParserConfigurationException, SAXException, SaxonApiException{
    	// send GetCapabilities and check support versions
    	String serviceURL 	= testSubjectUri.toString();
    	String response = sendGetRequest(serviceURL, "?service=wps&request=GetCapabilities&AcceptVersions=1.0.0");
    	
    	// Read xml string using Xpath2
    	InputSource sourceWR = new InputSource(new StringReader(response));
    	DocumentBuilderFactory dbfWR = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dbWR = dbfWR.newDocumentBuilder();
    	Document documentWR = dbWR.parse(sourceWR);
    	
    	XdmValue xdmValue = XMLUtils.evaluateXPath2(new DOMSource(documentWR), "/wps:Capabilities/wps:Languages/wps:Supported/ows:Language", getStandardBindings());
		
		//No supported language
		if(xdmValue.size() == 0) {
			Assert.assertTrue(false, "The server does not satisfies the requirements for using the Language parameter.");
		}
		
		//save all supported languages to list 
		List<String> supportLanguageList = new ArrayList<String>();
		
		//add different support languages to list
		for (XdmItem xdmItem : xdmValue) {
			String language = xdmItem.getStringValue();
			supportLanguageList.add(language);
			System.out.println(language);
		}
		
		//send request using each support language
		for (String language : supportLanguageList) {
			String languageSelectionResponse = sendGetRequest(serviceURL,"?service=wps&request=DescribeProcess&Version=1.0.0&identifier=" + processId + "&language=" + language);
			
			// Read xml string using Xpath2
	    	InputSource source = new InputSource(new StringReader(languageSelectionResponse));
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
	    	Document document = documentBuilder.parse(source);
	    	
	    	//Verify language value of tag wps:ProcessDescriptions
			XdmValue languageXdmValue = XMLUtils.evaluateXPath2(new DOMSource(document), "/wps:ProcessDescriptions/@xml:lang", getStandardBindings());
			System.out.println(languageXdmValue.toString());
			boolean result = languageXdmValue.toString().contains(language);
			Assert.assertTrue(result, "The server does not satisfies the requirements for using the Language parameter.");
		}
		
    }
    
    public HttpURLConnection GetConnection(String serviceURL, String param) throws IOException {
		URL urlObj = new URL(serviceURL + param);
		return (HttpURLConnection) urlObj.openConnection();
	}
	
	public String sendGetRequest(String serviceURL, String param) throws IOException {
		String dURL = URLDecoder.decode(serviceURL);
        StringBuilder response = new StringBuilder();
        HttpURLConnection conn = GetConnection(dURL, param);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "text/xml");
        conn.setDoOutput(true);
		
        // Read all the text returned by the server
        BufferedReader in;
        if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
        	in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        else {
        	in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        
        String str;
        while ((str = in.readLine()) != null) {
            response.append(str);
        }
        in.close();
		return response.toString();
	}
	
	public Map<String, String> getStandardBindings() {
    	Map<String, String> nsBindings = new HashMap<String, String>();    	
        nsBindings.put(Namespaces.OWS, "ows");
        nsBindings.put(Namespaces.XLINK, "xlink");
        nsBindings.put(Namespaces.GML, "gml");
        nsBindings.put(Namespaces.WPS, "wps");
        return nsBindings;
    }
	
	public boolean validateXMLString(String inputXml, String schemaLocation)throws SAXException, IOException {
    	// build the schema
    	//SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    	//File schemaFile = new File(schemaLocation);
    	Schema schema = ValidationUtils.createSchema(schemaLocation);
    	Validator validator = schema.newValidator();
    	// create a source from a string
    	Source source = new StreamSource(new StringReader(inputXml));
    	// check input
    	boolean isValid = true;
    	try {
    		validator.validate(source);
    	} 
    	catch (SAXException e) {
    		isValid = false;
    	}
    		return isValid;
    }
	
	/**
	 * Check XPath2.0
	 * 
	 * @param xpath
	 *            String denoting an xpath syntax
	 * @return XdmValue converted to string
	 */
	public String CheckXPath2(String xpath, Document testSubject) {
		XdmValue xdmValue = null;
		try {
			xdmValue = evaluateXPath2(new DOMSource(testSubject), xpath, getStandardBindings());
		} catch (SaxonApiException e) {
			e.printStackTrace();
		};
		return xdmValue.toString();
	}
	
	public String postMessage(String xmlString, String serviceURL) throws IOException {
		StringBuffer content = new StringBuffer();
		URL url = new URL(serviceURL);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-type", "text/xml");
		conn.setDoOutput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		String msg = xmlString;
		wr.writeBytes(msg);
		// send request
		wr.flush();
		// close
		wr.close();

		// read response
		BufferedReader in;
		int responseCode = conn.getResponseCode();
		if(responseCode > 299)
		    in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		else
		    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String str;
		while ((str = in.readLine()) != null) {
			content.append(str);
		}
		in.close();
		return content.toString();
	}
	
	/**
     * Evaluates an XPath 2.0 expression using the Saxon s9api interfaces.
     * 
     * @param xmlSource
     *            The XML Source.
     * @param expr
     *            The XPath expression to be evaluated.
     * @param nsBindings
     *            A collection of namespace bindings required to evaluate the
     *            XPath expression, where each entry maps a namespace URI (key)
     *            to a prefix (value); this may be {@code null} if not needed.
     * @return An XdmValue object representing a value in the XDM data model;
     *         this is a sequence of zero or more items, where each item is
     *         either an atomic value or a node.
     * @throws SaxonApiException
     *             If an error occurs while evaluating the expression; this
     *             always wraps some other underlying exception.
     */
    public XdmValue evaluateXPath2(Source xmlSource, String expr,
            Map<String, String> nsBindings) throws SaxonApiException {
        Processor proc = new Processor(false);
        XPathCompiler compiler = proc.newXPathCompiler();
       if (null != nsBindings) {
            for (String nsURI : nsBindings.keySet()) {
                compiler.declareNamespace(nsBindings.get(nsURI), nsURI);
            }
        }
        XPathSelector xpath = compiler.compile(expr).load();
        net.sf.saxon.s9api.DocumentBuilder builder = proc.newDocumentBuilder();
        XdmNode node = null;
        if (DOMSource.class.isInstance(xmlSource)) {
            DOMSource domSource = (DOMSource) xmlSource;
            node = builder.wrap(domSource.getNode());
        } else {
            node = builder.build(xmlSource);
        }
        xpath.setContextItem(node);
        return xpath.evaluate();
    }
}
