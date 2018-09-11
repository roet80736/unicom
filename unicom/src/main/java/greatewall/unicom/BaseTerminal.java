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

public abstract class BaseTerminal implements ApiClientConstant {

	protected SOAPConnectionFactory connectionFactory;
	protected MessageFactory messageFactory;
	protected URL url;
	protected String licenseKey;
	protected XWSSProcessorFactory processorFactory;
	protected String username;
	protected String password;

	public abstract SOAPMessage createTerminalRequest(Map<String, String> params) throws SOAPException;

	public abstract Map<String, Object> writeTerminalResponse(SOAPMessage response) throws SOAPException;

	public BaseTerminal(String url, String username, String password, String licenseKey)
			throws SOAPException, MalformedURLException, XWSSecurityException {
		connectionFactory = SOAPConnectionFactory.newInstance();
		messageFactory = MessageFactory.newInstance();
		processorFactory = XWSSProcessorFactory.newInstance();
		this.url = new URL(url);
		this.licenseKey = licenseKey;
		this.username = username;
		this.password = password;
	}

	/**
	 * This method creates a Terminal Request and sends back the SOAPMessage.
	 * ICCID value is passed into this method
	 *
	 * @return SOAPMessage
	 * @throws SOAPException
	 */
	protected void initRequestParams(SOAPEnvelope envelope, SOAPBodyElement requestNameElement, Map<String, String> map)
			throws SOAPException {
		String messageId = map.get(UnicomConstants.MESSAGE_ID);
		Name messageIdName = envelope.createName(UnicomConstants.MESSAGE_ID, PREFIX, NAMESPACE_URI);
		SOAPElement messageIdElement = requestNameElement.addChildElement(messageIdName);
		messageIdElement.setValue(messageId);
		map.remove(UnicomConstants.MESSAGE_ID);
		Name version = envelope.createName("version", PREFIX, NAMESPACE_URI);
		SOAPElement versionElement = requestNameElement.addChildElement(version);
		versionElement.setValue(UnicomConstants.VERSION_NO);
		Name license = envelope.createName("licenseKey", PREFIX, NAMESPACE_URI);
		SOAPElement licenseElement = requestNameElement.addChildElement(license);
		licenseElement.setValue(licenseKey);
		Name name;
		SOAPElement element;
		for (String key : map.keySet()) {
			name = envelope.createName(key, PREFIX, NAMESPACE_URI);
			element = requestNameElement.addChildElement(name);
			element.setValue(map.get(key));
		}
	}

	public Map<String, Object> callWebService(Map<String, String> params)
			throws SOAPException, IOException, XWSSecurityException, Exception {
		SOAPMessage request = createTerminalRequest(params);
		request = secureMessage(request, this.username, this.password);
		System.out.println("Request: ");
		request.writeTo(System.out);
		System.out.println("");
		SOAPConnection connection = connectionFactory.createConnection();
		SOAPMessage response = connection.call(request, url);
		System.out.println("Response: ");
		response.writeTo(System.out);
		System.out.println("");

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		if (!response.getSOAPBody().hasFault()) {
			result = writeTerminalResponse(response);
		} else {
			SOAPFault fault = response.getSOAPBody().getFault();
			result.put("state", "fault");
			String faultString = fault.getFaultString();
			result.put("stateCode", faultString);
			if ("200200".equals(faultString)) {
				result.put("stateMsg", "No device usage found (未找到设备用量)");
			}
		}
		return result;
	}

	/**
	 * This method is used to add the security. This uses xwss:UsernameToken
	 * configuration and expects Username and Password to be passes.
	 * SecurityPolicy.xml file should be in classpath.
	 *
	 * @param message
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws XWSSecurityException
	 */
	protected SOAPMessage secureMessage(SOAPMessage message, final String username, final String password)
			throws IOException, XWSSecurityException {
		CallbackHandler callbackHandler = new CallbackHandler() {
			public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
				for (int i = 0; i < callbacks.length; i++) {
					if (callbacks[i] instanceof UsernameCallback) {
						UsernameCallback callback = (UsernameCallback) callbacks[i];
						callback.setUsername(username);
					} else if (callbacks[i] instanceof PasswordCallback) {
						PasswordCallback callback = (PasswordCallback) callbacks[i];
						callback.setPassword(password);
					} else {
						throw new UnsupportedCallbackException(callbacks[i]);
					}
				}
			}
		};
		InputStream policyStream = null;
		XWSSProcessor processor = null;
		try {
			policyStream = getClass().getResourceAsStream("securityPolicy.xml");
			processor = this.processorFactory.createProcessorForSecurityConfiguration(policyStream, callbackHandler);
		} finally {
			if (policyStream != null) {
				policyStream.close();
			}
		}
		ProcessingContext context = processor.createProcessingContext(message);
		return processor.secureOutboundMessage(context);
	}

}
