package com.bouncycastle.pgpUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.bouncycastle.openpgp.PGPException;

public class TestPGPEncryptDecrypt4File {

	public static void main(String[] args) {

		String filePath = "C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\PGP\\";
		String pgpPubKeyFilePath = filePath + "PSBCWUPGP2023-pub.asc";
		String pgpSecKeyFilePath = filePath + "PSBCWUPGP2023-sec.asc";
		String passCode = "P$BCpgpWU";
		
		String testFilePath = "C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\[Data Upload]\\";
		String filePrefix = "99306000000_WUTXOPS_0000_20250717_A_0001_0004";
		String originalFileName = filePrefix + "_raw.txt";
		String encryptedFileName = filePrefix + ".txt";
		String decryptedFileName = filePrefix + "_decrypted.txt";
		
		try {
			InputStream pubickeyIn = new BufferedInputStream(new FileInputStream(pgpPubKeyFilePath));
			InputStream privatekeyIn = new BufferedInputStream(new FileInputStream(pgpSecKeyFilePath));

			PgpEncryptionUtil pgpEncryptionUtil = new PgpEncryptionUtil();
			PgpDecryptionUtil pgpDecryptionUtil = new PgpDecryptionUtil(privatekeyIn, passCode);

			File testFile = new File(testFilePath + originalFileName);
			File encryptedFile = new File(testFilePath + encryptedFileName);
			File decryptedFile = new File(testFilePath + decryptedFileName);
			
			OutputStream encryptedOS = new FileOutputStream(testFilePath + encryptedFileName);
			
			pgpEncryptionUtil.encrypt(encryptedOS, Files.newInputStream(testFile.toPath()), testFile.length(),
				pubickeyIn);
			
	        pgpDecryptionUtil.decrypt(Files.newInputStream(encryptedFile.toPath()), Files.newOutputStream(decryptedFile.toPath()));
			
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (PGPException pe) {
			pe.printStackTrace();
		}
	}
}
