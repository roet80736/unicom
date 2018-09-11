package greatewall.unicom;

import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xml.wss.XWSSecurityException;

import greatewall.unicom.common.UnicomConstants;

public class GetTerminalDetailsClient extends BaseTerminal {

	public GetTerminalDetailsClient(String username, String password, String licenseKey)
			throws SOAPException, MalformedURLException, XWSSecurityException {
		this("https://api.10646.cn/ws/service/terminal", username, password, licenseKey);
	}

	public GetTerminalDetailsClient(String url, String username, String password, String licenseKey)
			throws SOAPException, MalformedURLException, XWSSecurityException {
		super(url, username, password, licenseKey);
	}

	@Override
	public SOAPMessage createTerminalRequest(Map<String, String> params) throws SOAPException {
		SOAPMessage message = messageFactory.createMessage();
		message.getMimeHeaders().addHeader("SOAPAction", UnicomConstants.TERMINAL_URL + "GetTerminalDetails");
		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
		Name requestName = envelope.createName(UnicomConstants.GET_TERMINAL_DETAILS_REQ, PREFIX, NAMESPACE_URI);
		SOAPBodyElement requestNameElement = message.getSOAPBody().addBodyElement(requestName);
		String iccid = params.get("iccid");
		params.remove("iccid");
		if (params != null && !params.isEmpty()) {
			initRequestParams(envelope, requestNameElement, params);
		}
		Name iccids = envelope.createName("iccids", PREFIX, NAMESPACE_URI);
		SOAPElement iccidsElement = requestNameElement.addChildElement(iccids);
		Name iccidName = envelope.createName("iccid", PREFIX, NAMESPACE_URI);
		SOAPElement iccidElement = iccidsElement.addChildElement(iccidName);
		iccidElement.setValue(iccid);

		// SOAPMessage message = this.messageFactory.createMessage();
		// message.getMimeHeaders().addHeader("SOAPAction",
		// "http://api.jasperwireless.com/ws/service/terminal/GetTerminalDetails");
		//
		// SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
		// Name terminalRequestName =
		// envelope.createName("GetTerminalDetailsRequest", "jws",
		// "http://api.jasperwireless.com/ws/schema");
		//
		// SOAPBodyElement terminalRequestElement =
		// message.getSOAPBody().addBodyElement(terminalRequestName);
		// Name msgId = envelope.createName("messageId", "jws",
		// "http://api.jasperwireless.com/ws/schema");
		// SOAPElement msgElement =
		// terminalRequestElement.addChildElement(msgId);
		// msgElement.setValue("TCE-100-ABC-340841");
		// Name version = envelope.createName("version", "jws",
		// "http://api.jasperwireless.com/ws/schema");
		// SOAPElement versionElement =
		// terminalRequestElement.addChildElement(version);
		// versionElement.setValue("1.0");
		// Name license = envelope.createName("licenseKey", "jws",
		// "http://api.jasperwireless.com/ws/schema");
		// SOAPElement licenseElement =
		// terminalRequestElement.addChildElement(license);
		// licenseElement.setValue(this.licenseKey);
		// Name iccids = envelope.createName("iccids", "jws",
		// "http://api.jasperwireless.com/ws/schema");
		// SOAPElement iccidsElement =
		// terminalRequestElement.addChildElement(iccids);
		// Name iccidName = envelope.createName("iccid", "jws",
		// "http://api.jasperwireless.com/ws/schema");
		// SOAPElement iccidElement = iccidsElement.addChildElement(iccidName);
		// iccidElement.setValue("89860617040064578877");
		//
		// Name iccidName = envelope.createName("iccid", "jws",
		// "http://api.jasperwireless.com/ws/schema");
		// SOAPElement iccidElement =
		// terminalRequestElement.addChildElement(iccidName);
		// iccidElement.setValue("89860617040064578877");

		return message;
	}

	@Override
	public Map<String, Object> writeTerminalResponse(SOAPMessage response) throws SOAPException {
		SOAPEnvelope envelope = response.getSOAPPart().getEnvelope();
		Name terminalResponseName = envelope.createName(UnicomConstants.GET_TERMINAL_DETAILS_RESP, PREFIX,
				NAMESPACE_URI);
		SOAPBodyElement terminalResponseElement = (SOAPBodyElement) response.getSOAPBody()
				.getChildElements(terminalResponseName).next();
//		String terminalValue = terminalResponseElement.getTextContent();
		Name terminals = envelope.createName("terminals", PREFIX, NAMESPACE_URI);
		Name terminal = envelope.createName("terminal", PREFIX, NAMESPACE_URI);
		SOAPBodyElement terminalsElement = (SOAPBodyElement) terminalResponseElement.getChildElements(terminals).next();
		SOAPBodyElement terminalElement = (SOAPBodyElement) terminalsElement.getChildElements(terminal).next();
		NodeList list = terminalElement.getChildNodes();
		Node node = null;
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		for (int i = 0; i < list.getLength(); i++) {
			node = list.item(i);
			result.put(node.getLocalName(), node.getTextContent());
		}
		result.put("state", "success");
		return result;
	}

	public static void main(String[] args) throws Exception {

		GetTerminalDetailsClient terminalClient = new GetTerminalDetailsClient("dengwei", "NRP202_20180320",
				"673e0884-1fe6-4505-99e9-ca7b0ef00fc5");
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("messageId", "TCE-100-ABC-34084");
		params.put("iccid", "89860617040064578877");
		// params.put("cycleStartDate", "2018-08-01Z");
		Map<String, Object> result = terminalClient.callWebService(params);
		System.out.println(result);
	}

}
