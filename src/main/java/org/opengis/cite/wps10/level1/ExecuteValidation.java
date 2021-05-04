package org.opengis.cite.wps10.level1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import org.opengis.cite.wps10.Namespaces;
import org.opengis.cite.wps10.util.ValidationUtils;
//import org.opengis.cite.wps10.CommonFixture;
import org.opengis.cite.wps10.DataFixture;
//import org.opengis.cite.wps10.TestRunArg;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.*;
import javafx.util.Pair;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class ExecuteValidation extends DataFixture {
//	String serviceURL 	= testSubjectUri.toString();
		
	/**
	 * A.4.4. Execute Validation
	 * Problem: How to identify a WPS is standard or not. Where is right WPS Sample?  
	 */
	
//	public static void main(String []args) throws IOException, URISyntaxException {
////		HTTPGETTransferredExecuteValidation();
////		HTTPPOSTTransferredExecuteValidation();
////		RawDataOutputExecuteValidation();
////		ResponseDocumentExecuteValidation();
////		UpdatingResponseDocumentExecuteValidation();
////		LanguageSelectionExecuteValidation();
////		HTTPResponseStatusCode();
//	}
	
	/**
	 * A.4.4.1 HTTP protocol GET usage
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * Description: 
	 * 1. Check server offline or not 
	 * 2. Check KVP parameters is valid or not (Required parameters include: Service, Request, Version and Identifier. Optional parameters include: Language, DataInputs, ResponseDocument, RawOutputData, StoreExecuteResponse, Lineage, Status)
	 * 3. Check KVP syntax follow 10.2.2.1 and 10.2.2.2
	 * 4. Check response code from GET request is 200 or not
	 */
	@Test(enabled=true, groups = "A.4.4. Execute operation test module", description = "A.4.4.1. Accept Execute HTTP GET transferred Execute operation requests")
	public void HTTPGETTransferredExecuteValidation() throws IOException, URISyntaxException { 
		String serviceURL = testSubjectUri.toString();
		String parameters = "service=wps&request=GetCapabilities&version=1.0.0";
		String req 		  = serviceURL + "?" + parameters;
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(req, "GET");
		if(isValid) {
			String HttpGetServiceURL = executeHttpGetUri.toString();
			StringBuilder xmlResponse = sendRequestByGET(HttpGetServiceURL, "");
			String xsdPath = "xsd/opengis/wps/1.0/wpsExecute_response.xsd";
			status 	= isXMLSchemaValid(xsdPath, xmlResponse.toString()) ? true : false;
			msg 	= "The server does not satisfies all requirements on the Execute operation response";
		} 
		else {
			status	= isValid;
			msg 	= "The server does not respond to HTTP GET request";
		}
		Assert.assertTrue(status, msg); 
	}
	
	/**
	 * A.4.4.2 HTTP protocol POST usage
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * Description: 
	 * 1. Check server offline or not 
	 * 2. Check XML content is valid or not follow 10.2.3
	 * 3. Check response code from POST request is 200 or not
	 */
	@Test(enabled=true, groups = "A.4.4. Execute operation test module", description = "A.4.4.2. Accept Execute HTTP POST transferred Execute operation requests") 
	public void HTTPPOSTTransferredExecuteValidation() throws IOException { 
		PostRawDataOutputExecuteValidation();
	}
	
	/**
	 * A.4.4.3 Raw Data Output
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * Description: 
	 * 1. Check server offline or not 
	 * 2. Check XML content is valid or not follow 10.2.3
	 * 3. Check response code from POST request is 200 or not
	 * 4. Check response text from POST request is valid or not (How to check for all types of output?)
	 */
	@Test(enabled=true, groups = "A.4.4. Execute operation test module", description = "A.4.4.3. Execute operation response: raw data output") 
	public void RawDataOutputExecuteValidation() throws IOException { 
		PostRawDataOutputExecuteValidation();
	}
	
	private void PostRawDataOutputExecuteValidation() throws IOException {
		String serviceURL = testSubjectUri.toString();
		String parameters = "service=wps&request=GetCapabilities&version=1.0.0";//&Identifier=" + identifier;
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(serviceURL + "?" + parameters, "GET");
		if(isValid) {
			String xmlString  = getStringOfXmlDocument(executeRequestFileRawDataOutputSubject);
			String xsdReqPath = "xsd/opengis/wps/1.0/wpsExecute_request.xsd";
			boolean isRequestValid = isXMLSchemaValid(xsdReqPath, xmlString.toString()) ? true : false;			
			if(isRequestValid) {
//				Document reqDoc 	= executeRequestFileRawDataOutputSubject;				
//				String reqMimeType	= reqDoc.getElementsByTagName("wps:RawDataOutput").item(0).getAttributes().getNamedItem("mimeType").getNodeValue();
//				String resMimeType 	= getRequestByPOST(serviceURL, xmlString).getKey().getContentType();
//				status	= reqMimeType.equals(resMimeType) ? true : false;
				status	= isRequestValid;
				msg 	= "The server does not satisfies all requirements on the Execute operation response";
			}
			else {
				status	= isRequestValid;
				msg 	= "The server does not respond to invalid request";
			}
		} 
		else {
			status	= isValid;
			msg 	= "The server does not respond to HTTP POST request";
		}
		Assert.assertTrue(status, msg);
	}
	
	/**
	 * A.4.4.4 Response Document
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * 1. Check server offline or not 
	 * 2. Check XML content is valid or not follow 10.2.3
	 * 3. Check response code from POST request is 200 or not
	 * 4. Check response text from POST request is valid or not follow 10.3.2
	 */
	@Test(enabled=true, groups = "A.4.4. Execute operation test module", description = "A.4.4.4. Execute operation response: response document") 
	public void ResponseDocumentExecuteValidation() throws IOException, URISyntaxException { 		
		String serviceURL = testSubjectUri.toString();		
		String parameters = "service=wps&request=GetCapabilities&version=1.0.0";//&Identifier=" + identifier;
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(serviceURL + "?" + parameters, "GET");
		if(isValid) {
			String xmlString  = getStringOfXmlDocument(executeRequestFileResponseDocumentOutputSubject);
			String xsdReqPath = "xsd/opengis/wps/1.0/wpsExecute_request.xsd";
			boolean isRequestValid = isXMLSchemaValid(xsdReqPath, xmlString.toString()) ? true : false;			
			if(isRequestValid) {				
				StringBuilder xmlResponse = sendRequestByPOST(serviceURL, xmlString);
				String xsdPath = "xsd/opengis/wps/1.0/wpsExecute_response.xsd";
				status	= isXMLSchemaValid(xsdPath, xmlResponse.toString()) ? true : false;
				msg 	= "The server does not satisfies all requirements on the Execute operation response";
			}
			else {
				status	= isRequestValid;
				msg 	= "The server does not respond to invalid request";
			}
		} 
		else {
			status	= isValid;
			msg 	= "The server does not respond to HTTP POST request";
		}
		Assert.assertTrue(status, msg); 
	}
	
	/**
	 * A.4.4.5 Updating Response Document
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * 1. Check server offline or not 
	 * 2. Check XML content is valid or not follow 10.2.3
	 * 3. Check response code from POST request is 200 or not
	 * 4. Check response text from POST request is valid or not follow 10.3.2 (Try with LiteralData, BoundingBoxData and ComplexData. How to custom it dynamic?)
	 * 5. Check StoreResponseDocument attribute is true or false (Need to be true)
	 * 6. Check response text from GET request for statusLocation is valid or not follow 10.3.2
	 */
	@Test(enabled=true, groups = "A.4.4. Execute operation test module", description = "A.4.4.5. Execute operation response: updating of response document") 
	public void UpdatingResponseDocumentExecuteValidation() throws IOException, URISyntaxException { 
		String serviceURL = testSubjectUri.toString();
//		String identifier = CheckXPath2("//ows:Identifier", executeRequestFileUpdatingResponseDocumentOutputSubject);
		String parameters = "service=wps&request=GetCapabilities&version=1.0.0";//&Identifier=" + identifier;
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(serviceURL + "?" + parameters, "GET");
		if(isValid) {
			String xmlString  = getStringOfXmlDocument(executeRequestFileUpdatingResponseDocumentOutputSubject);
			String xsdReqPath = "xsd/opengis/wps/1.0/wpsExecute_request.xsd";
			boolean isRequestValid = isXMLSchemaValid(xsdReqPath, xmlString.toString()) ? true : false;			
			if(isRequestValid) {
				StringBuilder xmlResponse = sendRequestByPOST(serviceURL, xmlString);
//				Document reqDoc 	= executeRequestFileUpdatingResponseDocumentOutputSubject;
				String xsdPath = "xsd/opengis/wps/1.0/wpsExecute_response.xsd";
				boolean isResponseValid = isXMLSchemaValid(xsdPath, xmlResponse.toString()) ? true : false;
				if(isResponseValid) {
					Document locationDoc = convertStringToXMLDocument(xmlResponse.toString());
					String location 	 = locationDoc.getFirstChild().getAttributes().getNamedItem("statusLocation").getNodeValue();
					StringBuilder xmlLocationResponse = sendRequestByGET(location, "");
					String xsdLocPath 	 = "xsd/opengis/wps/1.0/wpsExecute_response.xsd";
					status	= isXMLSchemaValid(xsdLocPath, xmlLocationResponse.toString()) ? true : false;	
					msg 	= "The server does not satisfies all requirements on the Execute operation response";
				}
				else {
					status 	= isResponseValid;
					msg 	= "The server does not return valid response";
				}
			}
			else {
				status	= isRequestValid;
				msg 	= "The server does not respond to invalid request";
			}
		} 
		else {
			status	= isValid;
			msg 	= "The server does not respond to HTTP POST request";
		}
		Assert.assertTrue(status, msg); 		
	}
	
	/**
	 * A.4.4.6 Language Selection
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * Description:
	 * 1. Check server offline or not 
	 * 2. Check KVP parameters is valid or not (Required parameters include: Service, Request, Version and Identifier. Optional parameters include: Language, DataInputs, ResponseDocument, RawOutputData, StoreExecuteResponse, Lineage, Status)
	 * 3. Check KVP syntax follow 10.2.2.1 and 10.2.2.2
	 * 4. Check response code from GET request is 200 or not
	 * 5. Check response text from GET request is valid or not follow 10.3.2 (If language is not supported or error, return Exception; If supported, return ResponseDocument)
	 */
	@Test(enabled=true, groups = "A.4.4. Execute operation test module", description = "A.4.4.6. Language selection") 
	public void LanguageSelectionExecuteValidation() throws IOException, URISyntaxException { 
//		String serviceURL = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
//		String serviceURL = testSubjectUri.toString();
		String HttpGetServiceURL = executeHttpGetUri.toString();
		String serviceURL  = URLDecoder.decode(HttpGetServiceURL);
//		String parameters = "service=wps&request=GetCapabilities&version=1.0.0&Identifier=org.n52.wps.server.r.test.geo&DataInputs=filename=fcu_ogc_wps";
		String req 		  = serviceURL; // + "?" + parameters;
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(req, "GET");
		if(isValid) {			
			boolean isLanguageValid   = isHTTPValid(req + "&Language=en-US", "GET");
			if(isLanguageValid) {
				StringBuilder xmlResponse = sendRequestByGET(serviceURL, "");
				String xsdPath = "xsd/opengis/wps/1.0/wpsExecute_response.xsd";
				status 	= isXMLSchemaValid(xsdPath, xmlResponse.toString()) ? true : false;
				msg 	= "The server does not satisfies all requirements on the Execute operation response";
			}
			else {
				status 	= isLanguageValid; 
				msg 	= "The server does not respond to Language parameter";
			}
		} 
		else {
			status	= isValid;
			msg 	= "The server does not respond to HTTP GET request";
		}
		Assert.assertTrue(status, msg); 
	}
	
	/*
	 * private Map<String, String> KVPfromURL(String URL) throws
	 * UnsupportedEncodingException { int i = URL.indexOf("?"); Map<String, String>
	 * paramsMap = new HashMap<>(); if (i > -1) { String searchURL =
	 * URL.substring(URL.indexOf("?") + 1); String params[] = searchURL.split("&");
	 * 
	 * for (String param : params) { String temp[] = param.split("=");
	 * paramsMap.put(temp[0], java.net.URLDecoder.decode(temp[1], "UTF-8")); } }
	 * return paramsMap; }
	 */
	
	private static StringBuilder sendRequestByGET(String requestURL, String parameters) throws IOException {
		String dURL = URLDecoder.decode(requestURL);
		URL obj = new URL(dURL + "?" + parameters);	
		if(parameters == "" || parameters == null) {
			obj = new URL(dURL);
		} 
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { 
			InputStream inputStream = con.getInputStream();
			byte[] res = new byte[2048];
			int i = 0;
			StringBuilder response = new StringBuilder();
			while ((i = inputStream.read(res)) != -1) {
				response.append(new String(res, 0, i));
			}
			inputStream.close();
			return response;
		} else {
			System.out.println("HTTP GET request not worked");
			return null;
		}
	}
	
	private static StringBuilder sendRequestByPOST(String requestURL, String XML) throws IOException {		
		URL obj = new URL(requestURL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("Content-Type", "application/xml");
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(XML.getBytes());
		os.flush();
		os.close();
		
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { 
			InputStream inputStream = con.getInputStream();
			byte[] res = new byte[2048];
			int i = 0;
			StringBuilder response = new StringBuilder();
			while ((i = inputStream.read(res)) != -1) {
				response.append(new String(res, 0, i));
			}
			inputStream.close();
			return response;
		} else {
			System.out.println("HTTP POST request not worked");
			return null;
		}
	}

	/*
	 * private static String getContentTypeByPOST(String requestURL, String XML)
	 * throws IOException { URL obj = new URL(requestURL); HttpURLConnection con =
	 * (HttpURLConnection) obj.openConnection();
	 * con.setRequestProperty("Content-Type", "application/xml");
	 * con.setRequestMethod("POST");
	 * 
	 * con.setDoOutput(true); OutputStream os = con.getOutputStream();
	 * os.write(XML.getBytes()); os.flush(); os.close();
	 * 
	 * int responseCode = con.getResponseCode(); if (responseCode ==
	 * HttpURLConnection.HTTP_OK) { return con.getContentType().toString(); } else {
	 * System.out.println("HTTP POST request not worked"); return null; } }
	 */
	
	/*
	 * private static Pair<HttpURLConnection, StringBuilder> getRequestByPOST(String
	 * requestURL, String XML) throws IOException { URL obj = new URL(requestURL);
	 * HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 * con.setRequestProperty("Content-Type", "application/xml");
	 * con.setRequestMethod("POST");
	 * 
	 * con.setDoOutput(true); OutputStream os = con.getOutputStream();
	 * os.write(XML.getBytes()); os.flush(); os.close();
	 * 
	 * int responseCode = con.getResponseCode(); if (responseCode ==
	 * HttpURLConnection.HTTP_OK) { InputStream inputStream = con.getInputStream();
	 * byte[] res = new byte[2048]; int i = 0; StringBuilder response = new
	 * StringBuilder(); while ((i = inputStream.read(res)) != -1) {
	 * response.append(new String(res, 0, i)); } inputStream.close(); return new
	 * Pair<HttpURLConnection, StringBuilder>(con, response); } else {
	 * System.out.println("HTTP POST request not worked"); return null; } }
	 */
	
	private static boolean isHTTPValid(String urlString, String reqMethod) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(reqMethod);
        int statusCode = con.getResponseCode(); 
        return (statusCode/100 != 2) ? false : true;
    }
	
	private static boolean isXMLSchemaValid(String xsdPath, String xmlString){        
        try {
            //SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = ValidationUtils.createSchema(xsdPath);
            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(new StringReader(xmlString)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
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
	
	private static Document convertStringToXMLDocument(String xmlString) 
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();
             
            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return null;
    }
	
	private static String getStringOfXmlDocument(Document xmlDocument)
    {
    	String xmlString = "";
    	TransformerFactory tf = TransformerFactory.newInstance();
        try {        	
        	Transformer t = tf.newTransformer();
        	StringWriter sw = new StringWriter();
        	t.transform(new DOMSource(xmlDocument), new StreamResult(sw));
        	xmlString = sw.toString(); 
            
        } 
        catch (TransformerException e) 
        {
            e.printStackTrace();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        return xmlString;
    }
}

