/**
* Copyright 2005 Jasper Systems, Inc. All rights reserved.
 *
 * This software code is the confidential and proprietary information of
 * Jasper Systems, Inc. ("Confidential Information"). Any unauthorized
 * review, use, copy, disclosure or distribution of such Confidential
 * Information is strictly prohibited.
 */
package greatewall.unicom;

import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.wss.XWSSecurityException;

import greatewall.unicom.common.UnicomConstants;

/**
 * 按月统计用量
 * 
 * @author liushici
 * @version $Id:
 *          //depot/jasper_dev/module/ProvisionApp/web/secure/apidoc/java/com/jasperwireless/ws/client/sample/GetTerminalDetailsClient.java#3
 *          $
 */
public class GetTerminalUsageClient extends BaseTerminal {

	public GetTerminalUsageClient(String username, String password, String licenseKey)
			throws SOAPException, MalformedURLException, XWSSecurityException {
		this("https://api.10646.cn/ws/service/billing", username, password, licenseKey);
	}

	public GetTerminalUsageClient(String url, String username, String password, String licenseKey)
			throws SOAPException, MalformedURLException, XWSSecurityException {
		super(url, username, password, licenseKey);
	}

	@Override
	public SOAPMessage createTerminalRequest(Map<String, String> map) throws SOAPException {
		SOAPMessage message = messageFactory.createMessage();
		message.getMimeHeaders().addHeader("SOAPAction", UnicomConstants.BILLING_URL + "GetTerminalUsage");
		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
		Name requestName = envelope.createName(UnicomConstants.GET_TERMINAL_USAGE_REQ, PREFIX, NAMESPACE_URI);
		SOAPBodyElement requestNameElement = message.getSOAPBody().addBodyElement(requestName);
		if (map != null && !map.isEmpty()) {
			initRequestParams(envelope, requestNameElement, map);
		}
		return message;
	}

	@Override
	public Map<String, Object> writeTerminalResponse(SOAPMessage response) throws SOAPException {
		SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
		Name terminalResponseName = envelope.createName(UnicomConstants.GET_TERMINAL_USAGE_RESP, PREFIX, NAMESPACE_URI);
		SOAPBodyElement terminalResponseElement = (SOAPBodyElement) response.getSOAPBody()
				.getChildElements(terminalResponseName).next();
		// String terminalValue = terminalResponseElement.getTextContent();
		NodeList list = terminalResponseElement.getChildNodes();
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		Node node;
		for (int i = 0; i < list.getLength(); i++) {
			node = list.item(i);
			result.put(node.getLocalName(), node.getTextContent());
		}
		result.put("state", "success");
		return result;
	}

	/**
	 * Main program. Usage : TerminalClient <username> <password>
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Apitest URL. See "Get WSDL Files" in the API documentation for
		// Production URL.
		// String url =
		// "https://apitest.jasperwireless.com/ws/service/terminal";
		String url = "https://api.10646.cn/ws/service/billing";
		/*
		 * if (args.length != 4) { System.out.
		 * println("usage: GetTerminalDetailsClient <license-key> <username> <password> <iccid>"
		 * ); System.exit(-1); } GetTerminalDetailsClient terminalClient = new
		 * GetTerminalDetailsClient(url, args[0]);
		 * terminalClient.callWebService(args[1], args[2], args[3]);
		 */

		// GetTerminalUsageClient2 terminalClient = new 
		// GetTerminalUsageClient2(url,
		// "673e0884-1fe6-4505-99e9-ca7b0ef00fc5");
		// terminalClient.callWebService("dengwei", "NRP202_20180320",
		// "89860617040064578877", "2018-08-01Z");

		GetTerminalUsageClient terminalClient = new GetTerminalUsageClient("dengwei", "NRP202_20180320",
				"673e0884-1fe6-4505-99e9-ca7b0ef00fc5");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("messageId", "TCE-100-ABC-34084");
		params.put("iccid", "89860617040064578877");
		params.put("cycleStartDate", "2018-08-01Z");

		Map<String, Object> result = terminalClient.callWebService(params);
		System.out.println(result);

	}

}
