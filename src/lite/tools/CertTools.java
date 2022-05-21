package lite.tools;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertTools {

	public static Date getNotBefore(String filePath) throws Exception {
		filePath = new File(filePath).toPath().toRealPath().toString();	
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		FileInputStream is = new FileInputStream(filePath);
		X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
		Date date = cer.getNotBefore();
		is.close();
		return date;

	}

	public static Date getNotAfter(String filePath) throws Exception {
		filePath = new File(filePath).toPath().toRealPath().toString();	
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		FileInputStream is = new FileInputStream(filePath);
		X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
		Date date = cer.getNotAfter();
		is.close();
		return date;
	}
}
