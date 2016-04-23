package nl.clockwork.ebms.admin.plugin.cpa.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import nl.clockwork.ebms.common.util.DOMUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Utils
{
	public static XPath createXPath()
	{
		XPath result = XPathFactory.newInstance().newXPath();
		result.setNamespaceContext(new NamespaceContext()
		{
	    public String getNamespaceURI( String prefix)
	    {
	      if (prefix.equals("cpa"))
	        return "http://www.oasis-open.org/committees/ebxml-cppa/schema/cpp-cpa-2_0.xsd";
	      else if(prefix.equals("xmldsig"))
	      	return "http://www.w3.org/2000/09/xmldsig#";
	      return "";//XPathConstants.NULL_NS_URI;
	    }

			@Override
			public String getPrefix(String namespaceURI)
			{
	      if (namespaceURI.equals("http://www.oasis-open.org/committees/ebxml-cppa/schema/cpp-cpa-2_0.xsd"))
	        return "cpa";
	      else if (namespaceURI.equals("http://www.w3.org/2000/09/xmldsig#"))
	        return "xmldsig";
	      return null;
			}

			@Override
			public Iterator<String> getPrefixes(String namespaceURI)
			{
				ArrayList<String> result = new ArrayList<String>();
	      if (namespaceURI.equals("http://www.oasis-open.org/committees/ebxml-cppa/schema/cpp-cpa-2_0.xsd"))
	        result.add("cpa");
	      else if (namespaceURI.equals("http://www.w3.org/2000/09/xmldsig#"))
	      	result.add("xmldsig");
				return result.iterator();
			}
	  });
		return result;
	}

	public static String toXSDDate(Date date)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00X");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return df.format(date);
	}

	public static void generateKeyInfo(Node node, InputStream is) throws CertificateException, KeyException, MarshalException
	{
    KeyInfoFactory kif = XMLSignatureFactory.getInstance("DOM").getKeyInfoFactory();
    Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(is);
    KeyInfo keyInfo = kif.newKeyInfo(Arrays.asList(kif.newKeyValue(certificate.getPublicKey()),kif.newX509Data(Collections.singletonList(certificate))));
    DOMStructure dom = new DOMStructure(node);
    keyInfo.marshal(dom,null);
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
