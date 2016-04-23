package nl.clockwork.ebms.admin.plugin.cpa.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.clockwork.ebms.common.util.DOMUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Utils
{
	public static X509Certificate getCertificateBySubject(KeyStore keyStore, String subject, String password) throws GeneralSecurityException
	{
		Enumeration<String> aliases = keyStore.aliases();
		while (aliases.hasMoreElements())
		{
			String alias = aliases.nextElement();
			X509Certificate certificate = (X509Certificate)keyStore.getCertificate(alias);
			if (certificate.getSubjectDN().getName().equals(subject))
				return certificate;
		}
		return null;
	}

	public static void write(Node node, OutputStream outputStream) throws TransformerException
	{
		Transformer transformer = DOMUtils.getTransformer();
		transformer.transform(new DOMSource(node),new StreamResult(outputStream));
	}

	public static void main(String[] args) throws ParserConfigurationException, MarshalException, TransformerException, GeneralSecurityException, IOException
	{
		Document document = createDocument();
    document.appendChild(document.createElement("root"));

    KeyInfoFactory kif = XMLSignatureFactory.getInstance("DOM").getKeyInfoFactory();

    InputStream fis = new FileInputStream("/home/edwin/ebms/ebms-adapter-mule3/resources/CPAs/keystore.cer");
    Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(fis);
    KeyInfo keyInfo = kif.newKeyInfo(Arrays.asList(kif.newKeyValue(certificate.getPublicKey()),kif.newX509Data(Collections.singletonList(certificate))));

    DOMStructure dom = new DOMStructure(document.getFirstChild());
    keyInfo.marshal(dom,null);
    write(dom.getNode().getFirstChild(),System.out);
	}

	private static Document createDocument() throws ParserConfigurationException
	{
		javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
    return db.newDocument();
	}
}
