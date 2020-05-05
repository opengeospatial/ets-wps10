package org.opengis.cite.wps10.level1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.opengis.cite.wps10.Namespaces;
import org.opengis.cite.wps10.CommonFixture;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

import java.util.*;
import javafx.util.Pair; 

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

///latest-wps/WebProcessingService
public class AllOperationServerValidation extends CommonFixture {
	String service_url1 = "http://geoprocessing.demo.52north.org/latest-wps/WebProcessingService";
	String service_url2 = "http://93.187.166.52:8081/geoserver/ows";
	
	/**
	 * A.4.1.1 GetCapabilities HTTP protocol usage
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@Test(enabled = true, groups = "A.4.1. All operations implemented test module", description = "Verify that the rules and conventions governing the use of HTTP are observed") 
	public void GetCapabilitiesHttpProtocolUsageValidation() throws IOException,URISyntaxException { 
		String param = "?service=wps&version=1.0.0&request=GetCapabilities";
		HttpURLConnection connection = GetConnection(service_url1, param);
	 
		connection.setRequestMethod("GET");
	  
		Integer responseCode = connection.getResponseCode();
	  
		boolean result = (responseCode == HttpURLConnection.HTTP_OK );
		Assert.assertTrue(result, "The server does not respond to HTTP request"); 
	 }

	/**
	 * A.4.1.2 GetCapabilities HTTP response status code
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@Test(enabled = true, groups = "A.4.1. All operations implemented test module", description = "Verify that a service request which generates an exception produces response that contains 1) a service exception report, and 2) a status code indicating an error.")
	public void GetCapabilitiesHttpResponseStatusCodeValidation() throws IOException, URISyntaxException {
		String param = "?service=wps&version=1.0.0&request=GetCapabilities";
		HttpURLConnection connection = GetConnection(service_url1, param);
		
		connection.setRequestMethod("POST");
		
		Integer responseCode = connection.getResponseCode();
		
		// Check if response code is 4xx or 5xx
		int firstDigit = Integer.parseInt(Integer.toString(responseCode).substring(0, 1));
		boolean resCodeResult = (firstDigit == 4 || firstDigit == 5 ? true: false);
		
		// Check if body message contain service exception report
		boolean resBodyMesResult = true;
		if(responseCode > 299) {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = inputReader.readLine())!= null){
				response.append(inputLine);
			}
			inputReader.close();
			resBodyMesResult = (response != null ? true : false);
		}

		boolean result = (resCodeResult == true && resBodyMesResult == true ? true : false);
		Assert.assertTrue(result, "the response code from server is not either 4xx (Client error) or 5xx (Server error)");
	}
	
	/**
	 * A.4.1.1 DescribeProcess HTTP protocol usage
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@Test(enabled = true, groups = "A.4.1. All operations implemented test module", description = "Verify that the rules and conventions governing the use of HTTP are observed") 
	public void DescribeProcessHttpProtocolUsageValidation() throws IOException,URISyntaxException { 
		String param = "?service=wps&request=DescribeProcess&Version=1.0.0&identifier=ALL";
		HttpURLConnection connection = GetConnection(service_url1, param);
	 
		connection.setRequestMethod("GET");
	  
		Integer responseCode = connection.getResponseCode();
	  
		boolean result = (responseCode == HttpURLConnection.HTTP_OK );
		Assert.assertTrue(result, "The server does not respond to HTTP request"); 
	 }
	
	/**
	 * A.4.1.2 DescribeProcess HTTP response status code
	 * 
	 * 1. Send HTTP GET Request without defining indentifier
	 * 2. Check if the response code is 4XX or 5XX
	 * 3. Check if the response body xml contains node \\ows:ExceptionReport
	 * 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	@Test(groups = "A.4.1. All operations implemented test module", description = "Verify that a service request which generates an exception produces response that contains 1) a service exception report, and 2) a status code indicating an error.")
	public void DescribeProcessHttpResponseStatusCodeValidation() throws IOException, URISyntaxException, ParserConfigurationException, SAXException {
		String param = "?service=wps&request=DescribeProcess&Version=1.0.0";
		HttpURLConnection connection = GetConnection(service_url1, param);
		
		connection.setRequestMethod("POST");
		
		Integer responseCode = connection.getResponseCode();
		
		// Check if response code is 4xx or 5xx
		int firstDigit = Integer.parseInt(Integer.toString(responseCode).substring(0, 1));
		boolean resCodeResult = (firstDigit == 4 || firstDigit == 5 ? true: false);
		
		// Check if body message contain service exception report
		String responseWrong = "";
		boolean resBodyMesResult = true;
		if(responseCode > 299) {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = inputReader.readLine())!= null){
				response.append(inputLine);
			}
			inputReader.close();
			//resBodyMesResult = (response != null ? true : false);
			
			responseWrong = response.toString();
		}
		
		boolean result = (resCodeResult == true ? true : false);
		Assert.assertTrue(result, "the response code from server is not either 4xx (Client error) or 5xx (Server error)");
		
		//Read xml string using Xpath2
		InputSource sourceWR = new InputSource(new StringReader(responseWrong));
		DocumentBuilderFactory dbfWR = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder dbWR = dbfWR.newDocumentBuilder();
		Document documentWR = dbWR.parse(sourceWR);
		String candidateNodeWR = CheckXPath2("//ows:ExceptionReport", documentWR);
		
		boolean wrongResultRequest = (candidateNodeWR.contains("XdmEmptySequence") ? false : true);
		
		boolean finalResult = (wrongResultRequest == true ? true : false);
		Assert.assertTrue(finalResult, "The response body from server does not contain a service exception report.");
	}
	
	/**
	 * A.4.1.1 Execute HTTP protocol usage
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@Test(enabled = true, groups = "A.4.1. All operations implemented test module", description = "Verify that the rules and conventions governing the use of HTTP are observed") 
	public void ExecuteHttpProtocolUsageValidation() throws IOException,URISyntaxException { 
		String param = "?service=WPS&version=1.0.0&request=Execute&identifier=org.n52.wps.server.r.demo.uniform.table&DataInputs=min=0;max=10;n=5;";
		HttpURLConnection connection = GetConnection(service_url1, param);
	 
		connection.setRequestMethod("GET");
	  
		Integer responseCode = connection.getResponseCode();
	  
		boolean result = (responseCode == HttpURLConnection.HTTP_OK );
		Assert.assertTrue(result, "The server does not respond to HTTP request"); 
	 }
	
	/**
	 * A.4.1.2 Execute HTTP response status code
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * Description: 
	 * 1. Check server offline or not 
	 * 2. Check XML content is valid or not follow 10.2.3
	 * 3. Check response code from POST request is 200 or not
	 */
	@Test(enabled=true, groups = "A.4.1. All operations implemented test module", description = "A.4.4.2. Accept Execute HTTP POST transferred Execute operation requests") 
	public void ExecuteHttpResponseStatusCodeValidation() throws IOException, URISyntaxException { 
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
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
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
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String str;
		while ((str = in.readLine()) != null) {
			content.append(str);
		}
		in.close();
		return content.toString();
	}
	
	public String sendGetRequest(String serviceURL, String param) throws IOException {
        StringBuilder response = new StringBuilder();
        HttpURLConnection conn = GetConnection(serviceURL, param);
 
        // Read all the text returned by the server
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
    
}

