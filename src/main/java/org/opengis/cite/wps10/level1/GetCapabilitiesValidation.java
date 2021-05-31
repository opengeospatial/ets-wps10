package org.opengis.cite.wps10.level1;

import net.sf.saxon.s9api.*;
import org.opengis.cite.wps10.DataFixture;
import org.opengis.cite.wps10.Namespaces;
import org.opengis.cite.wps10.util.ValidationUtils;
import org.opengis.cite.wps10.util.XMLUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import java.util.*;

///latest-wps/WebProcessingService
public class GetCapabilitiesValidation extends DataFixture {
//	String serviceURL 	= this.testSubjectUri.toString();
//	String service_url1 = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
//	String service_url2 = "http://93.187.166.52:8081/geoserver/ows";
	
	/**
	 * A.4.2.1 Accept HTTP GET transferred KVP GetCapabilities operation request
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@Test(groups = "A.4.2. GetCapabilities operation test module", description = "Verify that a server accepts at least HTTP GET transferred requests for the GetCapabilities operation")
	public void HTTPGETTransferredKVPGetCapabilitiesValidation() throws IOException, URISyntaxException {
		//Create list of parameters
		String serviceURL 	= testSubjectUri.toString();
		List<String> params = new ArrayList<String>();
		params.add(0,"?service=wps&request=GetCapabilities");
		params.add(1,"?service=wps&request=GetCapabilities&AcceptVersions=1.0.0");
		params.add(2,"?service=wps&request=GetCapabilities&aCcepTVersioNS=1.0.0");
		
		//TODO: wps search whole project to be upper case, Check the http status code
		
		//params.add(2,"?service=wps&request=GetCapabilities&AcceptVersions=1.0.0&language=en-CA");
		
		//HTTP GET transferred KVP GetCapabilities operation request and receive the response code in a HashSet
		HashSet<Integer> res = new HashSet<Integer>();
		for (String param : params){
			HttpURLConnection connection = GetConnection(serviceURL, param);
			
			connection.setRequestMethod("GET");
			
			Integer responseCode = connection.getResponseCode();
			
			res.add(responseCode);
			
			//Check http status code
			Assert.assertTrue(responseCode < 300, "The server does not provides the same response when the same parameter names use different cases and combinations of cases");
			
		}
		
		//If HashSet size() is 1 ==> true; else ==> false
		boolean result = (res.size() != 1 ? false : true);
		Assert.assertTrue(result, "The server does not provides the same response when the same parameter names use different cases and combinations of cases");
	}
	
	/**
	 * A.4.2.2 Accept HTTP POST transferred XML GetCapabilities operation request
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	@Test(groups = "A.4.2. GetCapabilities operation test module", description = "Verify that a server accepts at HTTP POST transferred requests for the GetCapabilities operation if advertised in the GetCapabilities Response")
	public void HTTPPOSTTransferredXMLGetCapabilitiesValidation() throws IOException, URISyntaxException, ParserConfigurationException, SAXException {
		////Check correct POST operation request
		String serviceURL 	= testSubjectUri.toString();
		String msgCorrect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<wps:GetCapabilities service=\"WPS\" "
				+ "xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsGetCapabilities_request.xsd\">"
				+ "<wps:AcceptVersions>"
				+ "<ows:Version xmlns:ows=\"http://www.opengis.net/ows/1.1\">1.0.0</ows:Version>"
				+ "</wps:AcceptVersions>"
				+ "</wps:GetCapabilities>";
		String responseCorrect = postMessage(msgCorrect, serviceURL);
		
		//Read xml string using Xpath2
		InputSource source = new InputSource(new StringReader(responseCorrect));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(source);
		
		String candidateNode = CheckXPath2("//wps:Capabilities", document);
		boolean correctResultRequest = (candidateNode.contains("XdmEmptySequence") ? false : true);
		
		////Check wrong POST operation request
		String msgWrong = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<wps:getcapabilities service=\"WPS\" "
				+ "xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsGetCapabilities_request.xsd\">"
				+ "<wps:AcceptVersions>"
				+ "<ows:Version xmlns:ows=\"http://www.opengis.net/ows/1.1\">1.0.0</ows:Version>"
				+ "</wps:AcceptVersions>"
				+ "</wps:GetCapabilities>";
		String responseWrong = postMessage(msgWrong, serviceURL);
		
		//Read xml string using Xpath2
		InputSource sourceWR = new InputSource(new StringReader(responseWrong));
		DocumentBuilderFactory dbfWR = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder dbWR = dbfWR.newDocumentBuilder();
		Document documentWR = dbWR.parse(sourceWR);
		
		String candidateNodeWR = CheckXPath2("//ows:ExceptionReport", documentWR);
		boolean wrongResultRequest = (candidateNodeWR.contains("XdmEmptySequence") ? false : true);
		
		boolean finalResult = (correctResultRequest == true && wrongResultRequest == true ? true : false);
		Assert.assertTrue(finalResult, "The server does not accepts at HTTP POST transferred requests for the GetCapabilities operation");
	}
	
	/**
	 * A.4.2.3 GetCapabilities operation response
	 * @throws IOException 
	 * @throws SAXException 
	 */
	@Test(groups = "A.4.2. GetCapabilities operation test module", description = "Verify that a server satisfies all requirements on the GetCapabilities operation response")
	public void GetCapabilitiesResponseValidation() throws IOException, SAXException {
		String serviceURL 	= testSubjectUri.toString();
		//Create list of parameters
		List<String> params = new ArrayList<String>();
		params.add(0,"?service=wps&request=GetCapabilities");
		params.add(1,"?service=wps&request=GetCapabilities&AcceptVersions=1.0.0");
		
		//HTTP GET transferred KVP GetCapabilities operation request and receive the response xml file
		HashSet<Boolean> res = new HashSet<Boolean>();
		for (String param : params){
			String response = sendGetRequest(serviceURL, param);
			String xsdPath = "xsd/opengis/wps/1.0/wpsGetCapabilities_response.xsd";
			boolean resultValidation = validateXMLString(response, xsdPath);
			res.add(resultValidation);		
		}

		//If HashSet size() is 1 ==> true; else ==> false.
		boolean result = (res.size() != 1 ? false : true);
		Assert.assertTrue(result, "The server does not satisfies all requirements on the GetCapabilities operation response");
	}
	

	/**
	 * A.4.2.4 Version negotiation
	 * @throws IOException
	 */
	@Test(enabled = true, groups = "A.4.2. GetCapabilities operation test module",description = "Verify that a server satisfies the requirements for version negotiation" )
    public void VersionNegotiationGetCapabilitiesValidation() throws IOException,SaxonApiException,SAXException,ParserConfigurationException{
		String serviceURL 	= testSubjectUri.toString();
		// send GetCapabilities and check support versions
		String response = sendGetRequest(serviceURL, "?service=wps&request=GetCapabilities&language=en-US");
		
		// Read xml string using Xpath2
		InputSource sourceWR = new InputSource(new StringReader(response));
		DocumentBuilderFactory dbfWR = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder dbWR = dbfWR.newDocumentBuilder();
		Document documentWR = dbWR.parse(sourceWR);		
		
		XdmValue xdmValue = XMLUtils.evaluateXPath2(new DOMSource(documentWR), "//ows:ServiceTypeVersion", getStandardBindings());
		
		int higherSupportVersion = 0;
		int lowerSupportVersion = 0;
		
		if(xdmValue.size() == 0) {
			Assert.assertTrue(false, "The server does not satisfies the requirements for version negotiation.");
		}
		else if(xdmValue.size() > 0){
			higherSupportVersion = Integer.parseInt(xdmValue.itemAt(0).getStringValue().replace(".",""));
			lowerSupportVersion = Integer.parseInt(xdmValue.itemAt(0).getStringValue().replace(".",""));
		}
		
		for (XdmItem xdmItem : xdmValue) {
			System.out.println(xdmItem.getStringValue());
			int version = Integer.parseInt(xdmItem.getStringValue().replace(".",""));
			if(version > higherSupportVersion) {higherSupportVersion=version;}
			if(version < lowerSupportVersion) {lowerSupportVersion=version;}
			System.out.println(version);
		}
		//System.out.println(xdmValue.toString());
		
		//check current response version is the highest version or not
		int currentResponseVersion = 0;
		XdmValue xdmValue2 = XMLUtils.evaluateXPath2(new DOMSource(documentWR), "/wps:Capabilities/@version", getStandardBindings());
		if(xdmValue2.size() != 1) {
			Assert.assertTrue(false, "The server does not satisfies the requirements for version negotiation.");
		}
		else {
			System.out.println("A"+ xdmValue2.itemAt(0).getStringValue() + "A");
			currentResponseVersion = Integer.parseInt(xdmValue2.itemAt(0).getStringValue().replace(".", ""));
		}
		
		boolean result = currentResponseVersion == higherSupportVersion ? true : false;
		Assert.assertTrue(result, "The server does not satisfies the requirements for version negotiation.");
		
		
		//check higher and lower than support version returns exceptionCode="VersionNegotiationFailed"
		List<String> paramList = new ArrayList<String>();
		paramList.add(0,"?service=wps&request=GetCapabilities&language=en-US&AcceptVersions=3.0.0"); //higher than support version
		paramList.add(1,"?service=wps&request=GetCapabilities&language=en-US&AcceptVersions=0.0.1"); //lower than support version
		
		for (String param : paramList) {
			//get response xml string
			String exceptionResponse = sendGetRequest(serviceURL, param);
			
			String exceptionNode = CheckXPath2("//ows:Exception[@exceptionCode='VersionNegotiationFailed']",exceptionResponse);
			boolean exceptionResult = (exceptionNode.contains("XdmEmptySequence") ? false : true);
			Assert.assertTrue(exceptionResult, "The server does not satisfies the requirements for version negotiation.");
		}
		
		String supportVersionParam = "?service=wps&request=GetCapabilities&language=en-US&AcceptVersions=1.0.0"; //support version
		//get response xml string
		String supportVersionResponse = sendGetRequest(serviceURL, supportVersionParam);
		
		String node = CheckXPath2("/wps:Capabilities[@version='1.0.0']",supportVersionResponse);
		boolean supportVersionResult = (node.contains("XdmEmptySequence") ? false : true);
		Assert.assertTrue(supportVersionResult, "The server does not satisfies the requirements for version negotiation.");
		
    }
	
	/**
	 * A.4.2.5 Handling updateSequence parameter
	 * @throws IOException
	 * @throws SAXException 
	 */
	@Test(enabled = true, groups = "A.4.2. GetCapabilities operation test module", description = "Verify that a server satisfies the requirements for generating and using the updateSequence parameter, if the server implements the AcceptFormats request parameter.")
	public void HandlingUpdateSequenceValidation() throws IOException, SAXException{
		String serviceURL 	= testSubjectUri.toString();
		//send both right and wrong values of AcceptFormats parameter
		List<String> paramList = new ArrayList<String>();
		paramList.add(0,"?service=wps&request=GetCapabilities&AcceptVersions=1.0.0&AcceptFormats=text/xml");
		paramList.add(1,"?service=wps&request=GetCapabilities&AcceptVersions=1.0.0&AcceptFormats=application/x-bxml");
		
		for (String param : paramList) {
			//get response xml string
			String response = sendGetRequest(serviceURL, param);
			//valid WPS GetCapabilities Response schema path
			String xsdPath = "xsd/opengis/wps/1.0/wpsGetCapabilities_response.xsd";
			
			System.out.println("A.4.2.5: Send 'http get' request to: " + serviceURL + param);
			
			//if xml is valid => true, not valid => false and quit test
			boolean isValid = false;
			isValid = validateXMLString(response, xsdPath);
			Assert.assertTrue(isValid, "The server does not satisfies the requirements for generating and using the updateSequence parameter.");
		}
		
	}
	
	/**
	 * A.4.2.6 Language selection
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws SaxonApiException 
	 */
    @Test(groups = "A.4.2. GetCapabilities operation test module",description = "Verify that a server satisfies the requirements for using the Language parameter." )
    public void LanguageSelectionGetCapabilitiesValidation() throws IOException, ParserConfigurationException, SAXException, SaxonApiException{
    	String serviceURL 	= testSubjectUri.toString();
    	// send GetCapabilities and check support versions
    	String response = sendGetRequest(serviceURL, "?service=wps&request=GetCapabilities&AcceptVersions=1.0.0");
    	
    	// Read xml string using Xpath2
    	InputSource sourceWR = new InputSource(new StringReader(response));
    	DocumentBuilderFactory dbfWR = DocumentBuilderFactory.newInstance();
    	javax.xml.parsers.DocumentBuilder dbWR = dbfWR.newDocumentBuilder();
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
		
		for (String language : supportLanguageList) {
			String languageSelectionResponse = sendGetRequest(serviceURL,"?service=wps&request=GetCapabilities&language=" + language);
			
			// Read xml string using Xpath2
	    	InputSource source = new InputSource(new StringReader(languageSelectionResponse));
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	javax.xml.parsers.DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
	    	Document document = documentBuilder.parse(source);
	    	
	    	//Verify language value of tag wps:Capabilities
			XdmValue languageXdmValue = XMLUtils.evaluateXPath2(new DOMSource(document), "/wps:Capabilities/@xml:lang", getStandardBindings());
			System.out.println(languageXdmValue.toString());
			boolean result = languageXdmValue.toString().contains(language);
			Assert.assertTrue(result, "The server does not satisfies the requirements for using the Language parameter.");
		}
		
    }
    
    //draft: test service_url1 only
	public int sendHTTPGetRequestGetCapabilitiesOperationByVersion(String version) throws IOException {
		String serviceURL 	= testSubjectUri.toString();
		String param = "?service=wps&request=GetCapabilities&language=en-US&AcceptVersions=" + version;
		HttpURLConnection connection = getConnection(serviceURL, param);

		connection.setRequestMethod("GET");
		System.out.println("A.4.2.4: Send 'http get' request to: " + serviceURL + param);

		Integer responseCode = connection.getResponseCode();
		System.out.println("A.4.2.4: Response Code: " + responseCode);

		return responseCode;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
	public HttpURLConnection getConnection(String serviceURL, String param) throws IOException {
		URL urlObj = new URL(serviceURL + param);
		return (HttpURLConnection) urlObj.openConnection();
	}
	
	public HttpURLConnection GetConnection(String serviceURL, String param) throws IOException {
		URL urlObj = new URL(serviceURL + param);
		return (HttpURLConnection) urlObj.openConnection();
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
	
	public String sendGetRequest(String serviceURL, String param) throws IOException {
		String dURL = URLDecoder.decode(serviceURL);
        StringBuilder response = new StringBuilder();
        HttpURLConnection conn = GetConnection(dURL, param);
 
        // Read all the text returned by the server
     	BufferedReader in;
     	int responseCode = conn.getResponseCode();
     	if(responseCode > 299)
     	    in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
     	else
     	    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
     		
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
	
	public String CheckXPath2(String xpath, String response) throws ParserConfigurationException, SAXException, IOException {
		
		InputSource sourceWR = new InputSource(new StringReader(response));
		DocumentBuilderFactory dbfWR = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder dbWR = dbfWR.newDocumentBuilder();
		Document documentWR = dbWR.parse(sourceWR);
		
		XdmValue xdmValue = null;
		try {
			xdmValue = XMLUtils.evaluateXPath2(new DOMSource(documentWR), xpath, getStandardBindings());
		} catch (SaxonApiException e) {
			e.printStackTrace();
		};
		return xdmValue.toString();
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
        DocumentBuilder builder = proc.newDocumentBuilder();
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
}

