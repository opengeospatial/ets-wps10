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

import org.opengis.cite.wps10.CommonFixture;
import org.opengis.cite.wps10.DataFixture;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.util.*;
import javafx.util.Pair; 

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class ExecuteValidation extends DataFixture {
	
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
		String serviceURL = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
		String parameters = "service=WPS&request=Execute&version=1.0.0&Identifier=org.n52.wps.server.r.test.geo&DataInputs=filename=fcu_ogc_wps";
		String req 		  = serviceURL + "?" + parameters;
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(req, "GET");
		if(isValid) {
			StringBuilder xmlResponse = sendRequestByGET(serviceURL, parameters);
			String xsdPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_response.xsd";
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
	public void HTTPPOSTTransferredExecuteValidation() throws IOException, URISyntaxException { 
		String serviceURL = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
		String parameters = "service=WPS&request=Execute&version=1.0.0&Identifier=org.n52.wps.server.r.test.geo&DataInputs=filename=fcu_ogc_wps";
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(serviceURL + "?" + parameters, "GET");
		if(isValid) {
			String xmlString  = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
					"<wps:Execute service=\"WPS\" version=\"1.0.0\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0\r\n" + 
					"../wpsExecute_request.xsd\">\r\n" + 
					"	<ows:Identifier>org.n52.wps.server.algorithm.test.LongRunningDummyTestClass</ows:Identifier>\r\n" + 
					"	<wps:DataInputs>\r\n" + 
					"		<wps:Input>\r\n" + 
					"			<ows:Identifier>BBOXInputData</ows:Identifier>\r\n" + 
					"            <wps:Data>\r\n" + 
					"                <wps:BoundingBoxData crs=\"urn:ogc:def:crs:EPSG:6.6:4328\" dimensions=\"2\">\r\n" + 
					"                    <ows:LowerCorner>12.513 41.87</ows:LowerCorner>\r\n" + 
					"                    <ows:UpperCorner>14.996 43.333</ows:UpperCorner>\r\n" + 
					"                </wps:BoundingBoxData>\r\n" + 
					"            </wps:Data>\r\n" + 
					"		</wps:Input>\r\n" + 
					"	</wps:DataInputs>\r\n" + 
					"	<wps:ResponseForm>\r\n" + 
					"		<wps:ResponseDocument storeExecuteResponse=\"true\" lineage=\"true\" status=\"true\">\r\n" + 
					"			<wps:Output asReference=\"true\">\r\n" + 
					"				<ows:Identifier>BBOXOutputData</ows:Identifier>\r\n" + 
					"				<ows:Title>BBOXOutputData</ows:Title>\r\n" + 
					"				<ows:Abstract>BBOXOutputData</ows:Abstract>\r\n" + 
					"			</wps:Output>\r\n" + 
					"		</wps:ResponseDocument>\r\n" + 
					"	</wps:ResponseForm>\r\n" + 
					"</wps:Execute>";
			String xsdReqPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_request.xsd";
			boolean isRequestValid = isXMLSchemaValid(xsdReqPath, xmlString.toString()) ? true : false;			
			if(isRequestValid) {
				StringBuilder xmlResponse = sendRequestByPOST(serviceURL, xmlString);
				String xsdPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_response.xsd";
				status	= isXMLSchemaValid(xsdPath, xmlResponse.toString()) ? true : false;;
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
	public void RawDataOutputExecuteValidation() throws IOException, URISyntaxException { 
		String serviceURL = "https://demo.geo-solutions.it/geoserver/ows";
		String parameters = "service=WPS&request=Execute&version=1.0.0&Identifier=JTS:buffers";
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(serviceURL + "?" + parameters, "GET");
		if(isValid) {
			String xmlString  = getStringOfXmlDocument(this.executeRequestFileRawDataOutputSubject);
			String xsdReqPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_request.xsd";
			boolean isRequestValid = isXMLSchemaValid(xsdReqPath, xmlString.toString()) ? true : false;			
			if(isRequestValid) {
				Document reqDoc 	= convertStringToXMLDocument(xmlString.toString());
				
//				if(reqDoc.getElementsByTagName("wps:RawDataOutput").item(0).getAttributes().getNamedItem("mimeType") == null)
//					System.out.println("Hello");
				
				//String reqMimeType	= reqDoc.getElementsByTagName("wps:RawDataOutput").item(0).getAttributes().getNamedItem("mimeType").getNodeValue();
//				String resMimeType 	= getContentTypeByPOST(serviceURL, xmlString);
				//String resMimeType 	= getRequestByPOST(serviceURL, xmlString).getKey().getContentType();
				//status	= reqMimeType.equals(resMimeType) ? true : false;
				
				StringBuilder xmlLocationResponse = sendRequestByPOST(serviceURL, xmlString);
				String xsdLocPath 	 = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_response.xsd";
				status	= isXMLSchemaValid(xsdLocPath, xmlLocationResponse.toString()) ? true : false;	
				
				msg 	= "The server does not satisfies all requirements on the Execute operation response";

//				StringBuilder xmlResponse = sendRequestByPOST(serviceURL, xmlString);
//				String xsdPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/gml/3.1.1/base/gml.xsd";
//				status	= isXMLSchemaValid(xsdPath, xmlResponse.toString()) ? true : false;;
//				msg 	= "The server does not satisfies all requirements on the Execute operation response";
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
		String serviceURL = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
		String parameters = "service=WPS&request=Execute&version=1.0.0&Identifier=org.n52.wps.server.r.test.geo&DataInputs=filename=fcu_ogc_wps";
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(serviceURL + "?" + parameters, "GET");
		if(isValid) {
			String xmlString  = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
					"<wps:Execute service=\"WPS\" version=\"1.0.0\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0\r\n" + 
					"../wpsExecute_request.xsd\">\r\n" + 
					"	<ows:Identifier>org.n52.wps.server.algorithm.test.LongRunningDummyTestClass</ows:Identifier>\r\n" + 
					"	<wps:DataInputs>\r\n" + 
					"		<wps:Input>\r\n" + 
					"			<ows:Identifier>BBOXInputData</ows:Identifier>\r\n" + 
					"            <wps:Data>\r\n" + 
					"                <wps:BoundingBoxData crs=\"urn:ogc:def:crs:EPSG:6.6:4328\" dimensions=\"2\">\r\n" + 
					"                    <ows:LowerCorner>12.513 41.87</ows:LowerCorner>\r\n" + 
					"                    <ows:UpperCorner>14.996 43.333</ows:UpperCorner>\r\n" + 
					"                </wps:BoundingBoxData>\r\n" + 
					"            </wps:Data>\r\n" + 
					"		</wps:Input>\r\n" + 
					"	</wps:DataInputs>\r\n" + 
					"	<wps:ResponseForm>\r\n" + 
					"		<wps:ResponseDocument storeExecuteResponse=\"true\" lineage=\"true\" status=\"true\">\r\n" + 
					"			<wps:Output asReference=\"true\">\r\n" + 
					"				<ows:Identifier>BBOXOutputData</ows:Identifier>\r\n" + 
					"				<ows:Title>BBOXOutputData</ows:Title>\r\n" + 
					"				<ows:Abstract>BBOXOutputData</ows:Abstract>\r\n" + 
					"			</wps:Output>\r\n" + 
					"		</wps:ResponseDocument>\r\n" + 
					"	</wps:ResponseForm>\r\n" + 
					"</wps:Execute>";
			String xsdReqPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_request.xsd";
			boolean isRequestValid = isXMLSchemaValid(xsdReqPath, xmlString.toString()) ? true : false;			
			if(isRequestValid) {
				StringBuilder xmlResponse = sendRequestByPOST(serviceURL, xmlString);
				String xsdPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_response.xsd";
				status	= isXMLSchemaValid(xsdPath, xmlResponse.toString()) ? true : false;;
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
		String serviceURL = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
		String parameters = "service=WPS&request=Execute&version=1.0.0&Identifier=org.n52.wps.server.r.test.geo&DataInputs=filename=fcu_ogc_wps";
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(serviceURL + "?" + parameters, "GET");
		if(isValid) {
			String xmlString  = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" + 
					"<wps:Execute service=\"WPS\" version=\"1.0.0\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0\r\n" + 
					"../wpsExecute_request.xsd\">\r\n" + 
					"	<ows:Identifier>org.n52.wps.server.algorithm.test.LongRunningDummyTestClass</ows:Identifier>\r\n" + 
					"	<wps:DataInputs>\r\n" + 
					"		<wps:Input>\r\n" + 
					"			<ows:Identifier>BBOXInputData</ows:Identifier>\r\n" + 
					"            <wps:Data>\r\n" + 
					"                <wps:BoundingBoxData crs=\"urn:ogc:def:crs:EPSG:6.6:4328\" dimensions=\"2\">\r\n" + 
					"                    <ows:LowerCorner>12.513 41.87</ows:LowerCorner>\r\n" + 
					"                    <ows:UpperCorner>14.996 43.333</ows:UpperCorner>\r\n" + 
					"                </wps:BoundingBoxData>\r\n" + 
					"            </wps:Data>\r\n" + 
					"		</wps:Input>\r\n" + 
					"	</wps:DataInputs>\r\n" + 
					"	<wps:ResponseForm>\r\n" + 
					"		<wps:ResponseDocument storeExecuteResponse=\"true\" lineage=\"true\" status=\"true\">\r\n" + 
					"			<wps:Output asReference=\"true\">\r\n" + 
					"				<ows:Identifier>BBOXOutputData</ows:Identifier>\r\n" + 
					"				<ows:Title>BBOXOutputData</ows:Title>\r\n" + 
					"				<ows:Abstract>BBOXOutputData</ows:Abstract>\r\n" + 
					"			</wps:Output>\r\n" + 
					"		</wps:ResponseDocument>\r\n" + 
					"	</wps:ResponseForm>\r\n" + 
					"</wps:Execute>";
			String xsdReqPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_request.xsd";
			boolean isRequestValid = isXMLSchemaValid(xsdReqPath, xmlString.toString()) ? true : false;			
			if(isRequestValid) {
				StringBuilder xmlResponse = sendRequestByPOST(serviceURL, xmlString);
				String xsdPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_response.xsd";
				boolean isResponseValid = isXMLSchemaValid(xsdPath, xmlResponse.toString()) ? true : false;
				if(isResponseValid) {
					Document locationDoc = convertStringToXMLDocument(xmlResponse.toString());
					String location 	 = locationDoc.getFirstChild().getAttributes().getNamedItem("statusLocation").getNodeValue();
					StringBuilder xmlLocationResponse = sendRequestByGET(location, "");
					String xsdLocPath 	 = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_response.xsd";
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
		String serviceURL = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
		String parameters = "service=WPS&request=Execute&version=1.0.0&Identifier=org.n52.wps.server.r.test.geo&DataInputs=filename=fcu_ogc_wps";
		String req 		  = serviceURL + "?" + parameters;
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(req, "GET");
		if(isValid) {
			boolean isLanguageValid   = isHTTPValid(req + "&Language=en-US", "GET");
			if(isLanguageValid) {
				StringBuilder xmlResponse = sendRequestByGET(serviceURL, parameters);
				String xsdPath = "src/main/resources/org/opengis/cite/wps10/xsd/opengis/wps/1.0/wpsExecute_response.xsd";
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
	
	/**
	 * A.4.1.2 HTTP Response Status Code
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * 1. Check response code is 4xx, 5xx or not 
	 * 2. Check response text from request is exception or not 
	 * 3. Return exceptionCode value
	 */
	@Test(enabled=true, groups = "A.4.1. All operations implemented test module", description = "A.4.1.2. HTTP response status code") 
	public void HTTPResponseStatusCode() throws IOException, URISyntaxException { 
		String serviceURL = "https://demo.geo-solutions.it/geoserver/ows";
		String parameters = "service=WPS&version=1.0.0&request=DescribeProcess&identifier=JTS:Invalid";
		boolean status	  = false;
		String msg 		  = null;
		boolean isValid   = isHTTPValid(serviceURL + "?" + parameters, "GET");
		if(isValid) {
			StringBuilder xmlResponse = sendRequestByGET(serviceURL, parameters);
			Document exceptionDoc = convertStringToXMLDocument(xmlResponse.toString());
			String exceptionCode  = exceptionDoc.getElementsByTagName("ows:Exception").item(0).getAttributes().getNamedItem("exceptionCode").getNodeValue();
			if(exceptionCode != null) {
				status	= true;	
				msg 	= "The server return an exception code " + exceptionCode;
			} else {
				status 	= false;
				msg 	= "The server does not return an exception code";
			}	
		} 
		else {
			status	= true;
			msg 	= "The server does not respond to HTTP GET request";
		}
		Assert.assertTrue(status, msg); 		
	}
	
	private Map<String, String> KVPfromURL(String URL) throws UnsupportedEncodingException {
	    int i = URL.indexOf("?");
	    Map<String, String> paramsMap = new HashMap<>();
	    if (i > -1) {
	        String searchURL = URL.substring(URL.indexOf("?") + 1);
	        String params[] = searchURL.split("&");

	        for (String param : params) {
	            String temp[] = param.split("=");
	            paramsMap.put(temp[0], java.net.URLDecoder.decode(temp[1], "UTF-8"));
	        }
	    }
	    return paramsMap;
	}
	
	private static StringBuilder sendRequestByGET(String requestURL, String parameters) throws IOException {
		URL obj = new URL(requestURL + "?" + parameters);	
		if(parameters == "" || parameters == null) {
			obj = new URL(requestURL);
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

	private static String getContentTypeByPOST(String requestURL, String XML) throws IOException {		
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
			return con.getContentType().toString();
		} else {
			System.out.println("HTTP POST request not worked");
			return null;
		}
	}
	
	private static Pair<HttpURLConnection, StringBuilder> getRequestByPOST(String requestURL, String XML) throws IOException {		
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
			return new Pair<HttpURLConnection, StringBuilder>(con, response);
		} else {
			System.out.println("HTTP POST request not worked");
			return null;
		}
	}
	
	private static boolean isHTTPValid(String urlString, String reqMethod) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(reqMethod);
        int statusCode = con.getResponseCode(); 
        return (statusCode/100 != 2) ? false : true;
    }
	
	private static boolean isXMLSchemaValid(String xsdPath, String xmlString){        
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlString)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
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
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
             
            // Uncomment if you do not require XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
             
            //A character stream that collects its output in a string buffer, 
            //which can then be used to construct a string.
            StringWriter writer = new StringWriter();
     
            //transform document to string 
            transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));
     
            xmlString = writer.getBuffer().toString();   
            System.out.println(xmlString);                      //Print to console or logs
            
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

