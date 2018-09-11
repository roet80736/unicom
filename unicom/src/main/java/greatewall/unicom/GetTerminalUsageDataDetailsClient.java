package greatewall.unicom;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import com.jasperwireless.ws.client.sample.ApiClientConstant;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.callback.PasswordCallback;
import com.sun.xml.wss.impl.callback.UsernameCallback;

import greatewall.unicom.common.UnicomConstants;

public class GetTerminalUsageDataDetailsClient extends BaseTerminal {

	public GetTerminalUsageDataDetailsClient(String url, String username, String password, String licenseKey)
			throws SOAPException, MalformedURLException, XWSSecurityException {
		super(url, username, password, licenseKey);
	}

	@Override
	public SOAPMessage createTerminalRequest(Map<String, String> params) throws SOAPException {
		SOAPMessage message = messageFactory.createMessage();
		message.getMimeHeaders().addHeader("SOAPAction", UnicomConstants.BILLING_URL + "GetTerminalUsageDataDetails");
		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
		Name requestName = envelope.createName(UnicomConstants.GET_TERMINAL_USAGE_DATA_DETAILS_REQ, PREFIX,
				NAMESPACE_URI);
		SOAPBodyElement requestNameElement = message.getSOAPBody().addBodyElement(requestName);
		if (params != null && !params.isEmpty()) {
			initRequestParams(envelope, requestNameElement, params);
		}
		return message;
	}

	@Override
	public Map<String, Object> writeTerminalResponse(SOAPMessage response) throws SOAPException {
		SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
		Name responseName = envelope.createName(UnicomConstants.GET_TERMINAL_USAGE_DATA_DETAILS_RESP, PREFIX,
				NAMESPACE_URI);
		SOAPBodyElement responseElement = (SOAPBodyElement) response.getSOAPBody().getChildElements(responseName)
				.next();
		String terminalValue = responseElement.getTextContent();
//		System.out.println("Terminal Response [" + terminalValue + "]");
		return null;
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

		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("messageId", "TCE-100-ABC-34084");
		params.put("iccid", "89860617040064578877");
		params.put("cycleStartDate", "2018-08-01Z");

		GetTerminalUsageDataDetailsClient terminalClient = new GetTerminalUsageDataDetailsClient(url, "dengwei",
				"NRP202_20180320", "673e0884-1fe6-4505-99e9-ca7b0ef00fc5");
		terminalClient.callWebService(params);
	}

}
