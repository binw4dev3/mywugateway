package com.bouncycastle.pgpUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.bouncycastle.openpgp.PGPException;

public class TestPGPEncryptDecrypt4Bytes {

	public static void main(String[] args) {

		String filePath = "C:\\Users\\303172\\OneDrive - Western Union\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\PGP\\";
		String pgpPubKeyFilePath = filePath + "PSBCWUPGP2023-pub.asc";
		String pgpSecKeyFilePath = filePath + "PSBCWUPGP2023-sec.asc";
		String passCode = "P$BCpgpWU";
		
		String testString = "****************4762";

		try {
			InputStream pubickeyIn = new BufferedInputStream(new FileInputStream(pgpPubKeyFilePath));
			InputStream privatekeyIn = new BufferedInputStream(new FileInputStream(pgpSecKeyFilePath));

			PgpEncryptionUtil pgpEncryptionUtil = new PgpEncryptionUtil();
			PgpDecryptionUtil pgpDecryptionUtil = new PgpDecryptionUtil(privatekeyIn, passCode);

			// Encrypting the test bytes
			byte[] encryptedBytes = pgpEncryptionUtil.encrypt(testString.getBytes(Charset.forName("ASCII")),
					pubickeyIn);
			System.out.println("Encrypted bytes: \t" + Arrays.toString(encryptedBytes));
			
			StringBuffer encryptedTextBuff = new StringBuffer("");
			for(int i = 0, n = encryptedBytes.length; i < n; i++) {
				encryptedTextBuff.append(encryptedBytes[i]);
				if(i < n - 1) {
					encryptedTextBuff.append(",");
				}
			}
			
			String encryptedText = encryptedTextBuff.toString();
			
			//String encryptedText = Arrays.toString(encryptedBytes);
			System.out.println("Encrypted text: \t" + encryptedText);
			
			/*String[] encryptedTextArray = encryptedText.split(",");
			byte[] encryptedTextBytesArray = new byte[encryptedTextArray.length];
			for(int i = 0, n = encryptedTextArray.length; i < n; i++) {
				String text = encryptedTextArray[i];
				int d = Integer.valueOf(encryptedTextArray[i]);
				byte b = Byte.valueOf(encryptedTextArray[i]);
				
				encryptedTextBytesArray[i] = b;
			}*/

			// Decrypting the generated encrypted bytes
			//byte[] decryptedBytes = pgpDecryptionUtil.decrypt(encryptedBytes);
			byte[] decryptedBytes = pgpDecryptionUtil.decrypt4WU(encryptedText);
			System.out.println(new String("Decrypted text: \t" + new String(decryptedBytes)));
			
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (PGPException pe) {
			pe.printStackTrace();
		}
	}

}
