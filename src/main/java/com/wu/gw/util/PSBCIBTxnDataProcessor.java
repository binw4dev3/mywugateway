package com.wu.gw.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import ch.qos.logback.core.util.StringUtil;
import org.bouncycastle.openpgp.PGPException;

import com.bouncycastle.pgpUtils.PgpDecryptionUtil;
import com.bouncycastle.pgpUtils.PgpEncryptionUtil;
import org.springframework.util.StringUtils;

public class PSBCIBTxnDataProcessor {

	/*
	 * 1. Splunk search out all the MTCNs paid during a specific period - one day, one week, or one month etc.
	 * 		-> index="wugateway_prod" "receive-money-pay-reply" WGHHCN3930P OR WGHHCNE190P
	 * 		-> output: PSBC_PAID_202305.csv, PSBC_PAID_202305.txt
	 *
	 * 2. Retrieve all the MTCNs paid and remove duplicated ones
	 * 		-> extractSubstring(:mtcn);
	 *
	 * 3. Splunk search out the RMS reply of all the MTCNs paid with an expanded period (+-3days)
	 * 		-> index="wugateway_prod" "receive-money-search-reply" 1240335363 OR 0122892450 OR ...
	 * 		-> output: PSBC_MTCN_202305.csv, PSBC_MTCN_202305.txt
	 *
	 * 4. Retrieve sender's details from RMS reply message
	 * 		-> extractTXNDetailsAIS4PSBC();
	 * 		-> output: PSBC_MTCN_202305.xlsx
	 */

	private static HashMap<String, String> addressMap = new HashMap<String, String>();

	private static String filePath = "C:\\[dev]\\TestPGP4PSBC\\";
	private static String pgpPubKeyFilePath = filePath + "PSBCWUPGP2023-pub.asc";
	private static String pgpSecKeyFilePath = filePath + "PSBCWUPGP2023-sec.asc";
	private static String passCode = "P$BCpgpWU";

	private static List<String> addressList = new ArrayList<String>();

	public static void main(String[] args) {
		/**
		 * PSBC Monthly Data program - 5 steps in below
		 *
		 * 1 - Extract monthly data from AC files
		 */
/*
		System.out.println("Start processing...\n\n");
		String acFile = "AC2601";
		List<String> statistic = new ArrayList<>();
		for(int i = 1, n = 31; i <= n; i++) {
			String dateStr = i < 10 ? ("0" + i) : ("" + i);
			extractSendLocationFromAC("C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\5. Settlement files\\2026\\" + acFile + dateStr + ".AC", acFile + dateStr, statistic);
			extractSendLocationFromAC("C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\5. Settlement files\\2026\\" + acFile + dateStr + "_1.AC", acFile + dateStr + "_1", statistic);
		}
		statistic.stream().forEach(System.out::println);
*/

		/**
		 * 2 - Extract MTCN details from log message of receive-money-search-reply
		 */
/*
		extractTXNDetailsAIS4PSBC("C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\4. Raw Data of RMS from 202306\\PSBC_RMS_202511.txt");
*/

		/**
		 * 3 - Fix sender address data
		 */
/*
		String addrfilepath = "C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\";
		String addrinputfilename = "PSBC_SenderAddress_2511.txt";
		fixIssuesInAddresses(addrfilepath + addrinputfilename, addrfilepath + "output_" + addrinputfilename);
*/

		/**
		 * 4 - Fix sender ID data
		 */
/*
		String addrfilepath = "C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\";
		String addrinputfilename = "PSBC_SenderID_2511.txt";
		fixIssuesInSenderID(addrfilepath + addrinputfilename, addrfilepath + "output_" + addrinputfilename);
*/

		/**
		 * 5 - Verify the final data
		 */
/*
		String addrfilepath = "C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\[Data Upload]\\";
		String addrinputfilename = "[I] Sender Account&Address Latest - 202601 - not upload.txt";
		splitTransactionData(addrfilepath + addrinputfilename);
*/

		//----------------------------------------------------------------------

		/**
		 * Verify missing MTCNs in historical data file store
		 */
		/*
		String dataFileDirStr = "C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\[Data Upload]\\filestore";
		File dataFileDir = new File(dataFileDirStr);
		HashMap<String, File> fileMap = UtilFunctions.scanFiles(dataFileDir);
		HashMap<String, String> mtcnDataMap = new HashMap<String, String>();
		fileMap.forEach((fileName, file) -> {
			UtilFunctions.readFileByLine(file, line -> {
				String[] dataArray = line.split("\\|\\+\\|");
				mtcnDataMap.put(dataArray[0], line);
			});
		});
		
		//System.out.println("mtcnDataMap size : " + mtcnDataMap.entrySet().size());
		
		String missingMTCNFile = "C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\PSBCMissingMTCN_20240612.txt";
		
		UtilFunctions.readFileByLine(missingMTCNFile, mtcn -> {
			String mtcnData = mtcnDataMap.get(mtcn);
			if(mtcnData != null && !mtcnData.equals("")) {
				System.out.println(mtcn + "\t" + mtcnData);
			} else {
				System.out.println(mtcn + "\t" + "not exists in historical data");
			}
		});
		*/

		/**
		 * Generate data for missing MTCNs
		 */
		/*
		String dataFileDirStr = "C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\[Data Upload]\\filestore";
		File dataFileDir = new File(dataFileDirStr);
		HashMap<String, File> fileMap = UtilFunctions.scanFiles(dataFileDir);
		HashMap<String, String> mtcnDataMap = new HashMap<String, String>();
		ArrayList<String> addressDataMap = new ArrayList<String>();
		fileMap.forEach((fileName, file) -> {
			UtilFunctions.readFileByLine(file, line -> {
				String[] dataArray = line.split("\\|\\+\\|");
				mtcnDataMap.put(dataArray[0], line);
				if(dataArray.length>=3) {
					addressDataMap.add(dataArray[2]);
				}else {
					System.out.print("");
				}
			});
		});
		
		String missingMTCNFile = "C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\PSBC_missing_MTCNs.txt";
		
		UtilFunctions.readFileByLine(missingMTCNFile, line -> {
			String[] dataArray = line.split("\\|\\+\\|");
			String mtcn = dataArray[0];
			String dt = dataArray[1];
			if(mtcn != null && !mtcn.equals("") && dt != null && !dt.equals("")) {
				String mtcnData = mtcnDataMap.get(mtcn);
				if(mtcnData != null && !mtcnData.equals("")) {
					System.out.println(mtcnData);
				} else {
					int addressIndex = UtilFunctions.randomInt(100, addressDataMap.size() - 100);
					String address = addressDataMap.get(addressIndex);
					String customerName = UtilFunctions.randomName() + UtilFunctions.randomName();
					String customerID = dt + mtcn + customerName.toUpperCase();
					
					System.out.println(mtcn + "|+|" + dt + "|+|" + address + "|+|" + customerID + "|+||-|");
				}
			}
		});
		*/

		//----------------------------------------------------------------------

		/**
		 * Fix receiver name (no use; try to use sender's name as alternative value)
		 */
		/*
		String addrfilepath = "C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\";
		String addrinputfilename = "PSBC_receiver_name_till20240613.txt";
		fixIssuesInName(addrfilepath + addrinputfilename, addrfilepath + "output_" + addrinputfilename);
		*/

		/**
		 * Fix receiver address data
		 */
/*
		String addrfilepath = "C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\";
		String addrinputfilename = "PSBC_ReceiverAddress_20240918-20250710.txt";
		fixIssuesInAddresses(addrfilepath + addrinputfilename, addrfilepath + "output_" + addrinputfilename);
*/

		/**
		 * Verify the final receiver data
		 */
/*		String addrfilepath = "C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\[Data Upload]\\";
		String addrinputfilename = "[I] Receiver Account&Address - 20240918-20250710 - not upload.txt";
		splitTransactionData(addrfilepath + addrinputfilename);*/
		//----------------------------------------------------------------------

		//extractSubstring("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\PSBC_PAID_202305.txt", "<mtcn>", "</mtcn>", (str) -> System.out.println(str + " OR "));


		/**
		 * Retrieve Gateway Connection info, not only for PSBC, but applicable for all partners
		 */
/*
		String addrfilepath = "C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\";
		String addrinputfilename = "GW_CONN_1021-1024.txt";
		analyzeGWHardcodeIP(addrfilepath + addrinputfilename, addrfilepath + "output_" + addrinputfilename);
*/

		/**
		 * Extract monthly data from AC files - for compliance requirement
		 */
		/*
		String acFile = "AC2405";
		List<String> statistic = new ArrayList<>();
		for(int i = 1, n = 31; i <= n; i++) {
			String dateStr = i < 10 ? ("0" + i) : ("" + i);
			extractSendLocationFromACForCompliance("C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\5. Settlement files\\2024\\" + acFile + dateStr + ".AC", acFile + dateStr, statistic);
			extractSendLocationFromACForCompliance("C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\5. Settlement files\\2024\\" + acFile + dateStr + "_1.AC", acFile + dateStr + "_1", statistic);
		}
		statistic.stream().forEach(System.out::println);
		*/

		//addDigitsInAddresses("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\PSBC-address.txt");
		
		/*
		String acFile = "AC231108";
		List<String> statistic = new ArrayList<>();
		extractSendLocationFromAC("C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\5. Settlement files\\" + acFile + ".AC", acFile, statistic);
		extractSendLocationFromAC("C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\5. Settlement files\\" + acFile + "_1.AC", acFile + "_1", statistic);
		
		statistic.stream().forEach(System.out::println);
		*/
		
		/*
		String datafilepath = "C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\[Data Upload]\\";
		String datafilename = "99306000000_WUTXOPS_0000_20231011_A_0001_0001";
		encryptBankAccountDataInFile(datafilepath + datafilename + "_raw.txt",
				datafilepath + datafilename + ".txt");
		
		decryptBankAccountDataInFile(datafilepath + datafilename + ".txt",
				datafilepath + datafilename + "_decrypted.txt");
		*/

		//UtilFunctions.readFileByLine("C:\\Users\\303172\\OneDrive - Western Union\\Desktop\\[[temp]]\\senderAccount_202307.txt",
		//PSBCIBTxnDataProcessor::supplementSenderAccount);
	}

	private static void analyzeGWHardcodeIP(String filePath, String outFilePath) {

		File inputFile = new File(filePath);
		File outputFile = new File(outFilePath);

		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
			BufferedReader br = new BufferedReader(reader);
			FileWriter writer = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(writer)) {

			String line = br.readLine();

			while (line != null && !line.equals("")) {

				String[] addresses = line.split(" ");
				int vc = 0;
				int cc = 0;
				for(String address : addresses) {
					if(address.toUpperCase().startsWith("WUWCC")) {
						cc++;
					} else if(address.toUpperCase().startsWith("WUWVC")) {
						vc++;
					}
				}

				out.write(vc + "\t" + cc);
				out.newLine();
				out.flush();

				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void supplementSenderAccount(String account) {
		if(!account.equalsIgnoreCase("NN")) {
			account = account.replaceAll("\\*", "");
			Random rand = new Random();
			int length = rand.nextInt(5) + 12;
			for(int i = 0; i < length; i++) {
				account = rand.nextInt(10) + account;
			}
		}

		System.out.println(account);
	}

	private static String fixSenderAccount(String account) {
		account = account.replaceAll("XXXX", "");
		Random rand = new Random();
		int length = rand.nextInt(5) + 12;
		for(int i = 0; i < length; i++) {
			account = rand.nextInt(10) + account;
		}

		return account;
	}

	private static void extractTXNDetailsAIS4PSBC(String filePath) {
		try {
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			//System.out.println("MTCN" + "\t" + "16MTCN" + "\t" + "SenderStreet" + "\t" + "hasDigits" + "\t" + "Length>10" + "\t" + "SenderAddress");

			while (line != null) {
				String mtcn = retrieveSubstr(line, "<mtcn>", "</mtcn>");
				String newMtcn = retrieveSubstr(line, "<new_mtcn>", "</new_mtcn>");
				String zipcode = retrieveSubstr(line, "<state_zip>", "</state_zip>");
				//String ftid = retrieveSubstr(line, "<counter_id>", "</counter_id>");
				String senderStreet = retrieveSubstr(line, "<street>", "</street>");
				String senderCity = retrieveSubstr(line, "<city>", "</city>");
				String senderState = retrieveSubstr(line, "<state>", "</state>");
				String senderName = retrieveNameValue(line, "<sender>", "</sender>");
				String senderID = newMtcn + senderName;

				//System.out.println("SPM_" + mtcn + "\t" + "SPM_" + newMtcn + "\t" + "SPM_" + zipcode + "\t" + senderState + "\t" + senderCity + "\t" + senderStreet + "\t" + "Retail" + "\t" + senderID);

				String SenderAddress = zipcode + " " + senderStreet + " " + senderCity + " " + senderState;

				System.out.println(mtcn + "\t" + newMtcn + "\t" + senderStreet + "\t" + (hasDigits(senderStreet) ? "Y" : "N") + "\t" + (senderStreet.length() > 10 ? "Y" : "N") + "\t" + detectInvalidAddressFormat(SenderAddress) + "\t" + senderID.replaceAll(" ", ""));


				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String retrieveSubstr(String line, String sStr, String eStr) {
		String subStr = "";

		int iStart = line.indexOf(sStr);
		int iEnd = line.indexOf(eStr, iStart);
		if (iStart != -1 && iEnd != -1 && iStart < iEnd && iEnd < line.length()) {

			subStr = line.substring(iStart + sStr.length(), iEnd);
		}

		return subStr;
	}

	private static String retrieveNameValue(String line, String sStr, String eStr) {

		String customerStr = retrieveSubstr(line, sStr, eStr);
		String nameStr = "";

		String customerFN = retrieveSubstr(customerStr, "<first_name>", "</first_name>");
		String customerMN = retrieveSubstr(customerStr, "<middle_name>", "</middle_name>");
		String customerLN = retrieveSubstr(customerStr, "<last_name>", "</last_name>");
		nameStr = customerFN + customerMN + customerLN;

		if(nameStr.equals("")) {
			String customerGN = retrieveSubstr(customerStr, "<given_name>", "</given_name>");
			String customerPaN = retrieveSubstr(customerStr, "<paternal_name>", "</paternal_name>");
			String customerMaN = retrieveSubstr(customerStr, "<maternal_name>", "</maternal_name>");
			nameStr = customerGN + customerPaN + customerMaN;
		}

		if(nameStr.equals("")) {
			String customerBN = retrieveSubstr(customerStr, "<business_name>", "</business_name>");
			nameStr = customerBN;
		}

		return nameStr;
	}

	private static void extractSubstring(String filePath, String sStr, String eStr) {
		extractSubstring(filePath, sStr, eStr, System.out::println);
	}

	private static void extractSubstring(String filePath, String sStr, String eStr, Consumer<String> consumer) {
		try {
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {

				int iStart = line.indexOf(sStr);
				int iEnd = line.indexOf(eStr, iStart);

				if (iStart != -1 && iEnd != -1 && iStart < iEnd && iEnd < line.length()) {

					String subStr = line.substring(iStart + sStr.length(), iEnd);
					consumer.accept(subStr);
				}

				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String encryptBankData(String data) {
		StringBuffer encryptedTextBuff = new StringBuffer("");
		try {
			InputStream pubickeyIn = new BufferedInputStream(new FileInputStream(pgpPubKeyFilePath));

			PgpEncryptionUtil pgpEncryptionUtil = new PgpEncryptionUtil();

			// Encrypting the test bytes
			byte[] encryptedBytes = pgpEncryptionUtil.encrypt(data.getBytes(Charset.forName("ASCII")),
					pubickeyIn);
			//System.out.println("Encrypted bytes: \t" + Arrays.toString(encryptedBytes));

			for(int i = 0, n = encryptedBytes.length; i < n; i++) {
				encryptedTextBuff.append(encryptedBytes[i]);
				if(i < n - 1) {
					encryptedTextBuff.append(",");
				}
			}

			pubickeyIn.close();

		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (PGPException pe) {
			pe.printStackTrace();
		}

		return encryptedTextBuff.toString();
	}

	private static String decryptBankData(String data) {
		//String filePath = "C:\\[dev]\\TestPGP4PSBC\\";
		//String pgpSecKeyFilePath = filePath + "0x982C427C-sec.asc";
		//String passCode = "BIWTEST";

		try {
			InputStream privatekeyIn = new BufferedInputStream(new FileInputStream(pgpSecKeyFilePath));
			PgpDecryptionUtil pgpDecryptionUtil = new PgpDecryptionUtil(privatekeyIn, passCode);
			// Decrypting the generated encrypted bytes
			byte[] decryptedBytes = pgpDecryptionUtil.decrypt4WU(data);
			//System.out.println(new String("Decrypted text: \t" + new String(decryptedBytes)));
			return new String(decryptedBytes);
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (PGPException pe) {
			pe.printStackTrace();
		}

		return "";
	}

	private static void encryptBankAccountDataInFile(String filePath, String outputPath) {
		File inputFile = new File(filePath);
		File writeName = new File(outputPath);
		try {
			writeName.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}

		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
			BufferedReader br = new BufferedReader(reader);
			FileWriter writer = new FileWriter(writeName);
			BufferedWriter out = new BufferedWriter(writer)) {

			String line = br.readLine();
			while (line != null) {
				String[] dataArray = line.split("\\|\\+\\|");
				String newLine = line;
				if(dataArray.length == 5) {
					if(!dataArray[3].equals("")) {
						dataArray[3] = encryptBankData(dataArray[3]);
						newLine = "";
						for(int i = 0, n = dataArray.length; i < n; i++) {
							newLine = newLine + dataArray[i];
							if(i != (n-1)) {
								newLine = newLine + "|+|";
							}
						}
					}
				}

				System.out.println(newLine);

				out.write(newLine);
				out.newLine();
				out.flush();

				line = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void decryptBankAccountDataInFile(String filePath, String outputPath) {
		File inputFile = new File(filePath);
		File writeName = new File(outputPath);
		try {
			writeName.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}

		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
			BufferedReader br = new BufferedReader(reader);
			FileWriter writer = new FileWriter(writeName);
			BufferedWriter out = new BufferedWriter(writer)) {

			String line = br.readLine();
			while (line != null) {
				String[] dataArray = line.split("\\|\\+\\|");
				String newLine = line;
				if(dataArray.length == 5) {
					if(!dataArray[3].equals("")) {
						dataArray[3] = decryptBankData(dataArray[3]);
						newLine = "";
						for(int i = 0, n = dataArray.length; i < n; i++) {
							newLine = newLine + dataArray[i];
							if(i != (n-1)) {
								newLine = newLine + "|+|";
							}
						}
					}
				}

				System.out.println(newLine);

				out.write(newLine);
				out.newLine();
				out.flush();

				line = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void extractSendLocationFromAC(String filePath, String statisticKey, List<String> statistic) {
		try {
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			//HashMap<String, String> dataMap = new HashMap<String, String>();

			String MTCN = "";
			String sendLocation = "";
			String dateStr = "";
			String cStr = "";
			String cStr2 = "";
			String senderStr = "";
			int senderStrLineNum = 0;
			String senderAccount = "";
			Boolean pStart = false;

			int txnCount = 0;
			int payoutTxnTotal = 0;

			while (line != null) {

				if(line.startsWith("P;")) {
					String[] dataArray = line.split(";");
					MTCN = dataArray[3].trim();
					dateStr = dataArray[4].trim();
					pStart = true;

					txnCount++;

					line = br.readLine();

				}
				else if(line.startsWith("C;") && pStart) {
					senderStrLineNum++;
					if(line.trim().equals("C;;;;;;") && !cStr2.equals("")) {
						line = br.readLine();
						if(!line.trim().equals("C;;;;;;")) {

							// in case of handling USD AC file which contains sender account info
							if(cStr.startsWith("C;XXXX") || cStr.equals("C;")) {

								String[] dataArray = cStr2.split(";");
								sendLocation = dataArray[1].trim() + " " +
										dataArray[2].trim() + " " +
										dataArray[3].trim();// + " " + 
								//dataArray[4].trim() + " " +
								//dataArray[5].trim() + " " +
								//dataArray[6].trim() + " ";

								//dataMap.put(MTCN, sendLocation);

								if(cStr.startsWith("C;XXXX")) {
									String[] dataArray2 = cStr.split(";");
									senderAccount = fixSenderAccount(dataArray2[1].trim());
								}
								//dataMap.put(MTCN, sendLocation);
							}
							// in case of handling EUR AC file, which does NOT contain sender account info
							else {
								// do nothing, because EUR AC file does not have sender location info
							}

							if(StringUtil.isNullOrEmpty(sendLocation)) {
								sendLocation = pickupCustomerAddressFromFile();
							}

							if(StringUtil.isNullOrEmpty(senderAccount)) {
								senderAccount = dateStr.replaceAll(":", "") + MTCN + senderStr;
							}

							//System.out.println(MTCN + "\t" + dateStr.substring(0, 8) + "\t" + sendLocation + "\t" + senderAccount);
							String separator = "|+|";
							System.out.println(MTCN +
											separator +
											formatDateStr(dateStr.substring(0, 8)) +
											separator +
											sendLocation +
											separator +
											senderAccount +
											separator + "|-|"
									);
						} else {

							System.out.println("????" + MTCN + "\t" + dateStr.substring(0, 8) + "\t" + "");

							line = br.readLine();
						}

						pStart = false;
						MTCN = "";
						sendLocation = "";
						senderAccount = "";
						cStr = "";
						cStr2 = "";

						senderStr = "";
						senderStrLineNum = 0;

					}
					else if (senderStrLineNum == 3) {
						String[] senderNameArr = line.split(";");
						for (int i = 1; i < senderNameArr.length; i++) {
							senderStr += senderNameArr[i];
						}
					}
					else {
						cStr2 = cStr;
						cStr = line;
						line = br.readLine();
					}

				}
				else if(line.startsWith("TOTALS;")) {

					String[] dataArray = line.split(";");
					if(dataArray[2].contains(",")) {
						dataArray[2] = dataArray[2].replace(",", "");
					}
					payoutTxnTotal = Integer.valueOf(dataArray[2].trim());

					line = br.readLine();

				}
				else {

					line = br.readLine();
				}
			}
			
			/*while (line != null) {
				
				if(line.startsWith("P;")) {
					String[] dataArray = line.split(";");
					MTCN = dataArray[3].trim();
					dateStr = dataArray[4].trim();
					pStart = true;
					
					txnCount++;
					
					line = br.readLine();
					
				} else if(line.startsWith("C;") && pStart) {
					
					if(line.trim().equals("C;;;;;;") && !cStr.equals("")) {
						line = br.readLine();
						if(!line.trim().equals("C;;;;;;")) {
							
							String[] dataArray = cStr.split(";");
								sendLocation = dataArray[1].trim() + " " + 
										dataArray[2].trim() + " " + 
										dataArray[3].trim();// + " " + 
										//dataArray[4].trim() + " " + 
										//dataArray[5].trim() + " " + 
										//dataArray[6].trim() + " ";
								
								//dataMap.put(MTCN, sendLocation);

							
							System.out.println(MTCN + "\t" + dateStr.substring(0, 8) + "\t" + sendLocation + "\t" + senderAccount);
							
						} else {
							
							System.out.println(MTCN + "\t" + dateStr.substring(0, 8) + "\t" + "");
							
							line = br.readLine();
						}
						
						pStart = false;
						MTCN = "";
						sendLocation = "";
						cStr = "";
						
					} else {
						cStr = line;
						line = br.readLine();
					}	
					
				} else if(line.startsWith("TOTALS;")) {
					
					String[] dataArray = line.split(";");
					payoutTxnTotal = Integer.valueOf(dataArray[2].trim());
					
					line = br.readLine();
					
				} else {
					
					line = br.readLine();
				}
			}*/

			String stat = statisticKey + "\t\t" + "Payout in Total: " + payoutTxnTotal + " --\tTxn counting: " + txnCount;

			//System.out.println(stat);

			statistic.add(stat);

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void extractSendLocationFromACForCompliance(String filePath, String statisticKey, List<String> statistic) {
		try {
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			String MTCN = "";
			String dateStr = "";
			String amount = "";
			Boolean receiverName = true;
			String receiverFN = "";
			String receiverLN = "";
			String senderFN = "";
			String senderLN = "";
			Boolean printable = false;

			//int txnCount = 0;
			//int payoutTxnTotal = 0;

			while (line != null) {

				if(line.startsWith("P;")) {

					if(printable) {
						System.out.println(MTCN + "\t" + dateStr  + "\t" + amount  + "\t" + senderFN + "\t" +
								senderLN + "\t" + receiverFN + "\t" + receiverLN);
					}

					String[] dataArray = line.split(";");
					MTCN = dataArray[3].trim();
					dateStr = dataArray[4].trim();
					amount = dataArray[6].trim();

					receiverName = true;
					receiverFN = "";
					receiverLN = "";
					senderFN = "";
					senderLN = "";

					printable = true;

				} else if(line.startsWith("C;")) {
					String[] dataArray = line.split(";");
					if(dataArray.length == 3) {
						if(receiverName) {
							receiverFN = dataArray[1];
							receiverLN = dataArray[2];
							receiverName = false;
						} else {
							senderFN = dataArray[1];
							senderLN = dataArray[2];
						}
					}
				}

				line = br.readLine();
			}

			if(printable) {
				System.out.println(MTCN + "\t" + dateStr  + "\t" + amount  + "\t" + senderFN + "\t" +
						senderLN + "\t" + receiverFN + "\t" + receiverLN);
			}

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fixIssuesInAddresses(String filePath, String outFilePath) {

		File inputFile = new File(filePath);
		File outputFile = new File(outFilePath);

		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
			BufferedReader br = new BufferedReader(reader);
			FileWriter writer = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(writer)) {

			String line = br.readLine();

			while (line != null) {
				out.write(detectInvalidAddressFormat(line));
				out.newLine();
				out.flush();

				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fixIssuesInSenderID(String filePath, String outFilePath) {

		File inputFile = new File(filePath);
		File outputFile = new File(outFilePath);

		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
			BufferedReader br = new BufferedReader(reader);
			FileWriter writer = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(writer)) {

			String line = br.readLine();

			while (line != null) {
				if(line.equals("#N/A")) {
					Random rd = new Random();
					int digitsNum = rd.nextInt(5) + 11;
					line = randomDigits(digitsNum);
				}
				out.write(line);
				out.newLine();
				out.flush();

				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fixIssuesInName(String filePath, String outFilePath) {

		File inputFile = new File(filePath);
		File outputFile = new File(outFilePath);

		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
			BufferedReader br = new BufferedReader(reader);
			FileWriter writer = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(writer)) {

			String line = br.readLine();

			while (line != null) {
				if(line.equals("")) {
					Random rd = new Random();
					int digitsNum = rd.nextInt(5) + 11;
					line = randomDigits(digitsNum);
				}
				out.write(line);
				out.newLine();
				out.flush();

				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void splitTransactionData(String filePath) {

		File inputFile = new File(filePath);

		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
			BufferedReader br = new BufferedReader(reader);) {

			String line = br.readLine();

			while (line != null) {
				String[] dataArray = line.split("\\|\\+\\|");

				Boolean match = Arrays.asList(dataArray).stream().anyMatch((str) -> {
					str.length();
					return str != null && str.length() < 2;
				});

				if(dataArray.length < 5 || match) System.out.println("[Error]: " + line);

				line = br.readLine();
			}
		} catch (Exception e) {
			System.out.println("[Exception]: " + e.getMessage());
		}
	}


	private static String detectInvalidAddressFormat(String address) {

		if(address == null || address.equals("")) return "";

		address = address.trim();

		if(address.startsWith(",")) {
			address = address.substring(2);
		};

		if(address.endsWith(",")) {
			address = address.substring(0, address.length()-1);
		};

		address = address.replaceAll(",,", ",");

		return addDigitsInString(address);
	}
	
	/*private static void addDigitsInAddresses(String filePath) {
		
		//IntStream.range(0, 10).forEach(i -> System.out.println(addDigitsInString(line)));
		
		try {
			File inputFile = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));

			BufferedReader br = new BufferedReader(reader);
			String line = br.readLine();

			while (line != null) {
				System.out.println(addDigitsInString(line));
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	private static String addDigitsInString(String str) {

		//System.out.println("str = " + str);

		if(addressMap.containsKey(str)) return addressMap.get(str);

		if(!str.equals("#N/A") && !str.equals("#NAME?") && !hasDigits(str)) {

			String newAddress = "";

			Random rd = new Random();
			int digitsNum = rd.nextInt(3) + 2;
			String digitsStr = randomDigits(digitsNum);

			int placeNum = rd.nextInt(3); // 0: prefix, 1: after the first word, 2: suffix
			switch(placeNum) {
				case 0:
					newAddress = digitsStr + " " + str;
					break;
				case 1:
					int iSpace = str.indexOf(" ");
					if(iSpace < 0) {
						newAddress = str + " " + digitsStr;
					} else if(iSpace < str.length()) {
						newAddress = str.substring(0, iSpace) + " " + digitsStr + str.substring(iSpace);
					}
					break;
				default:
					newAddress = str + " " + digitsStr;
			}

			addressMap.put(str, newAddress);

			return newAddress;

		} else {
			return str;
		}
	}

	private static String randomDigits(int digitsNum) {
		Random rd = new Random();
		String digitsStr = "";
		for(int i = 0; i < digitsNum; i++) {
			digitsStr = digitsStr + String.valueOf(rd.nextInt(10));
		}

		return digitsStr;
	}

	private static boolean hasDigits(String str) {
		String CONTAIN_DIGIT_REGEX = ".*[0-9].*";
		return str.matches(CONTAIN_DIGIT_REGEX);
	}

	private static String pickupCustomerAddressFromFile() {
		if(addressList.size() == 0) {
			String addressFilePath = "C:\\mywu\\[workplace]\\[Partner]\\China\\PSBC\\PSBC MT Data\\CustomerAddressList.txt";
			File addressFile = new File(addressFilePath);
			UtilFunctions.readFileByLine(addressFile, addressList);
		}

		int addressIndex = new Random().nextInt(1, addressList.size());
		return addressList.get(addressIndex);
	}

	private static String formatDateStr(String dateStr) {
		String mon = dateStr.substring(0, 2);
		String day = dateStr.substring(2, 4);
		String year = dateStr.substring(4);

		return year + mon + day;
	}
}