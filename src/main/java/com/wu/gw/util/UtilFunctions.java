package com.wu.gw.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.google.common.collect.Iterables;

public class UtilFunctions {

	private static Properties utilProps;

	public static Logger loggerApp = LoggerFactory.getLogger("myapp");
	public static Logger logger = LoggerFactory.getLogger("mywu");
	public static Logger d2bLogger = LoggerFactory.getLogger("myd2b");
	public static Logger mtcnLogger = LoggerFactory.getLogger("myMTCN");
	public static Logger exceptLogger= LoggerFactory.getLogger("myException");
	public static Logger d2bLoggerPrinter = LoggerFactory.getLogger("myd2bPrinter");
	
	public static ArrayList<Thread> threadList = new ArrayList<Thread>();
	
	public static Scanner scan = new Scanner(System.in);

	public static Thread createNewThread(Runnable obj) {

		Thread thread = new Thread(obj);
		threadList.add(thread);
		return thread;
	}

	public static String getProperty(String propName) {

		if (utilProps == null) {

			utilProps = new Properties();

			try {

				File file = ResourceUtils.getFile("classpath:properties/GatewayServiceConfig.properties");
				InputStream in = new FileInputStream(file);
				utilProps.load(in);

			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}

		return utilProps.getProperty(propName);
	}

	public static String randomName() {
		Random rd = new Random();
		return nameList.get(rd.nextInt(nameList.size()));
	}

	public static String randomCorpName() {
		Random rd = new Random();
		return companyNameList.get(rd.nextInt(companyNameList.size()));
	}

	public static void loggingException(Logger log, Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		e.printStackTrace();
		e.printStackTrace(printWriter);
		log.info(printWriter.toString());

		printWriter.close();
	}
	
	public static void loggingException(Exception e) {
		loggingException(exceptLogger, e);
	}

	public static String randomIntStr(int min, int max) {
		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		String result = String.valueOf(s);

		return result;
	}
	
	public static int randomInt(int min, int max) {
		Random random = new Random();
		return random.nextInt(max) % (max - min + 1) + min;
	}

	public static X509KeyManager getKeyManager(String algorithm, KeyStore keystore, char[] password) {
		try {
			KeyManagerFactory factory = KeyManagerFactory.getInstance(algorithm);
			factory.init(keystore, password);

			List<KeyManager> kmList = Arrays.asList(factory.getKeyManagers());
			Iterable<X509KeyManager> kmIter = Iterables.filter(kmList, X509KeyManager.class);

			return Iterables.get(kmIter, 0);

		} catch (UnrecoverableKeyException e) {
			loggingException(e);
		} catch (KeyStoreException e) {
			loggingException(e);
		} catch (NoSuchAlgorithmException e) {
			loggingException(e);
		}

		return null;
	}

	public static X509TrustManager getTrustManager(String algorithm, KeyStore keystore) {
		try {
			TrustManagerFactory factory = TrustManagerFactory.getInstance(algorithm);
			factory.init(keystore);

			List<TrustManager> tmList = Arrays.asList(factory.getTrustManagers());
			Iterable<X509TrustManager> tmIter = Iterables.filter(tmList, X509TrustManager.class);

			return Iterables.get(tmIter, 0);

		} catch (KeyStoreException e) {
			loggingException(e);
		} catch (NoSuchAlgorithmException e) {
			loggingException(e);
		}

		return null;
	}

	public static HashMap<String, File> scanFiles(File srcDir) {
		HashMap<String, File> fileMap = null;

		if (srcDir == null || !srcDir.isDirectory()) {

			logger.info("Invalid xml file directory: " + srcDir.getPath());

		} else {

			File[] files = srcDir.listFiles();

			fileMap = new HashMap<String, File>();

			for (File file : files) {

				//if (getFileExtName(file).equals("xml")) {

					fileMap.put(getFileNameNoExt(file), file);
				//}
			}
		}

		return fileMap;
	}

	public static String getFileNameNoExt(File file) {

		String filename = file.getName();

		if ((filename != null) && (filename.length() > 0)) {

			int dot = filename.lastIndexOf('.');

			if ((dot > -1) && (dot < (filename.length() - 1))) {

				return filename.substring(0, dot);
			}
		}

		return filename;
	}

	public static String getFileExtName(File file) {

		if (file == null) {

			return null;
		}

		return getFileExtName(file.getPath());
	}

	public static String getFileExtName(String filename) {

		if ((filename != null) && (filename.length() > 0)) {

			int dot = filename.lastIndexOf('.');

			if ((dot > -1) && (dot < (filename.length() - 1))) {

				return filename.substring(dot + 1);
			}
		}

		return filename;
	}

	public static String readFileToString(File srcFile) throws IOException {

		FileReader fr = new FileReader(srcFile);

		StringBuffer strBuf = new StringBuffer();

		BufferedReader br = new BufferedReader(fr);

		String line = "";
		while ((line = br.readLine()) != null) {
			strBuf.append(line.trim());
		}

		br.close();
		fr.close();

		return strBuf.toString();
	}

	/**
	 * Designed for the testing of Transaction Log Analysis, based on Java 8 new
	 * feature
	 * 
	 * @param srcFile
	 * @param consumer
	 * @throws IOException
	 */
	public static void readFileByLine(String filePath, Consumer<String> consumer) {
		FileReader fr = null;
		
		try {

			File srcFile = new File(filePath);

			fr = new FileReader(srcFile);

			BufferedReader br = new BufferedReader(fr);

			String line = "";
			while ((line = br.readLine()) != null) {
				consumer.accept(line.trim());
			}

			fr.close();

		} catch (IOException e) {

			e.printStackTrace();
			
		} finally {
			
			if(fr != null) {
				try {
					fr.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Designed for the testing of Transaction Log Analysis, based on Java 8 new
	 * feature
	 * 
	 * @param srcFile
	 * @param consumer
	 * @throws IOException
	 */
	public static void readFileByLine(File srcFile, Consumer<String> consumer) {
		FileReader fr = null;
		
		try {
			fr = new FileReader(srcFile);

			BufferedReader br = new BufferedReader(fr);

			String line = "";
			while ((line = br.readLine()) != null) {
				consumer.accept(line.trim());
			}

			fr.close();

		} catch (IOException e) {

			e.printStackTrace();
			
		} finally {
			
			if(fr != null) {
				try {
					fr.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void readFileByLine(File srcFile, List<String> list) {
		FileReader fr = null;

		try {
			fr = new FileReader(srcFile);

			BufferedReader br = new BufferedReader(fr);

			String line = "";
			while ((line = br.readLine()) != null) {
				list.add(line.trim());
			}

			fr.close();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if(fr != null) {
				try {
					fr.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static File copyFile(String from, String to) throws IOException {
		Path fromPath = Paths.get(from);
		Path toPath = Paths.get(to);
		return Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING).toFile();
	}
	
	public static File moveFile(String from, String to) throws IOException {
		Path fromPath = Paths.get(from);
		Path toPath = Paths.get(to);
		return Files.move(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING).toFile();
	}

	public static String requestGatewayService(String URL, String requestMsg) {
		String replyMsg = null;
		HttpsURLConnection conn = null;

		try {
            mtcnLogger.info("Request - {}", requestMsg);

			URL url = new URL(URL);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Length", Integer.toString(requestMsg.length()));
			conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			conn.setRequestProperty("SOAPAction", "");

			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

			conn.setSSLSocketFactory(factory);
			conn.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			conn.addRequestProperty("User-Agent", "Apache-HttpClient/4.5.2 (Java/17.0.5)");

			OutputStream os = conn.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
			osw.write(requestMsg);
			osw.flush();
			osw.close();

			BufferedReader l_reader = null;

			if (conn.getResponseCode() == 200) {
				l_reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			} else {
				l_reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sTotalString = new StringBuilder();
			String sCurrentLine = "";

			while ((sCurrentLine = l_reader.readLine()) != null) {
				sTotalString.append(sCurrentLine);
			}

			String errorReply = retrieveXMLValue(sTotalString.toString(), "error");

			if (errorReply != null && !errorReply.equals("")) {
				replyMsg = "error - " + errorReply;
			} else {
				mtcnLogger.info("Success Reply - " + sTotalString);
				replyMsg = sTotalString.toString();
			}
		} catch(SocketTimeoutException e) {
			replyMsg = "error - Gateway request timeout: " + e.getMessage();
		} catch (Exception e) {
			loggingException(e);
		} finally {
			conn.disconnect();
		}

		return replyMsg;
	}
	
	public static String retrieveXMLSubElement(String xmlStr, String element) {

		if (xmlStr == null || xmlStr.equals("") || element == null || element.equals("")) {
			return null;
		}

		String lElement = "<" + element + ">";
		String rElement = "</" + element + ">";

		int lPos = xmlStr.indexOf(lElement);
		int rPos = xmlStr.indexOf(rElement);
		if (lPos < 0 || lPos >= rPos || xmlStr.length() < (rPos + rElement.length())) {
			return null;
		}

		return xmlStr.substring(lPos + lElement.length(), rPos);
	}
	
	public static List<String> retrieveAllSubElements(String xmlStr, String element) {

		if (xmlStr == null || xmlStr.equals("") || element == null || element.equals("")) {
			return null;
		}

		String lElement = "<" + element + ">";
		String rElement = "</" + element + ">";

		int lPos = xmlStr.indexOf(lElement);
		int rPos = xmlStr.indexOf(rElement);
		
		List<String> results = new ArrayList<>();
		
		while(lPos >= 0 && lPos < rPos && xmlStr.length() >= (rPos + rElement.length())) {

			results.add(xmlStr.substring(lPos + lElement.length(), rPos));
			
			xmlStr = xmlStr.substring(rPos + rElement.length());
			lPos = xmlStr.indexOf(lElement);
			rPos = xmlStr.indexOf(rElement);
			
		}
		
		return results;
	}


	public static String retrieveXMLValue(String xmlStr, String element) {

		if (xmlStr == null || xmlStr.equals("") || element == null || element.equals("")) {

			return "";
		}

		String lElement = "<" + element + ">";
		String rElement = "</" + element + ">";

		int lPos = xmlStr.indexOf(lElement);
		int rPos = xmlStr.indexOf(rElement);
		if (lPos < 0 || lPos >= rPos || xmlStr.length() < (rPos + rElement.length())) {

			return "";
		}

		return retrieveXMLValue(xmlStr.substring(lPos, rPos + rElement.length()));
	}

	public static String retrieveXMLValue(String xmlStr) {

		if (xmlStr == null || xmlStr.equals("")) {

			return "";
		}

		int sLPos = xmlStr.indexOf("<");
		int eLPos = xmlStr.indexOf(">", sLPos);
		if (sLPos < 0 || sLPos >= eLPos) {

			return "";
		}

		int sRPos = xmlStr.indexOf("<", eLPos);
		int eRPos = xmlStr.indexOf(">", sRPos);
		if (sRPos <= eLPos || sRPos < 0 || sRPos >= eRPos) {

			return "";
		}

		return xmlStr.substring(eLPos + 1, sRPos);
	}

	public static String convertToWUAmount(String amount) {
		if (amount == null || amount.equals("")) {
			return amount;
		}

		int iDec = amount.indexOf(".");
		if (iDec <= 0) {
			return amount + "00";
		}

		String integerStr = amount.substring(0, iDec);
		String decimalStr = amount.substring(iDec + 1);
		if(decimalStr.length() >= 3) {
			decimalStr = decimalStr.substring(0, 2);
		}
		decimalStr = "0." + decimalStr;
		/*if(Integer.parseInt(integerStr) == 0) {
			int i = (int)(Double.parseDouble(amount) * 100);
			return String.valueOf(i);
		}*/
		int integerValue = Integer.parseInt(integerStr) * 100;
		int decimalValue = (int)(Double.parseDouble(decimalStr) * 100);

		return String.valueOf(integerValue + decimalValue);

		//return amount.substring(0, iDec) + amount.substring(iDec + 1);
	}

	public static String convertToNatureAmount(String amount) {

		if(amount == null || amount.equals("")) return "";
		
		float natureAmt = Float.valueOf(amount) / 100;

		return String.valueOf(natureAmt);
	}
	
	public static String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(date);
	}
	
	public static String getCurrentTimestamp() {
		Instant now =  Instant.now();
		String PATTERN_FORMAT = "yyyyMMdd";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
	            .withZone(ZoneId.systemDefault());
		
		return formatter.format(now);
	}

	private static ArrayList<String> companyNameList = new ArrayList<String>(
			Arrays.asList("WESTERNUNION", "AMAZON", "GOOGLE", "TIKTOK", "ALIPAY", "TENCENT", "WALMART"));

	private static ArrayList<String> nameList = new ArrayList<String>(Arrays.asList("Aaron", "Abel", "Achilles", "Adam",
			"Adan", "Addison", "Ade", "Adley", "Adolph", "Agustin", "Ahren", "Aidan", "Alan", "Alban", "Albert",
			"Alcander", "Alfred", "Allen", "Alphonse", "Alvin", "Anders", "Andrew", "Andy", "Angelo", "Angus", "Anker",
			"Anthony", "Armand", "Asher", "August", "Baldwin", "Bali", "Barclay", "Barnett", "Barney", "Baron",
			"Barrett", "Barth", "Beck", "Ben", "Benjamin", "Benson", "Berkeley", "Bern", "Bert", "Bill", "Billy", "Bob",
			"Bobby", "Bowen", "Bradley", "Brady", "Brand", "Brian", "Brooks", "Bruno", "Bryant", "Buddy", "Burton",
			"Byron", "Caesar", "Cain", "Clavin", "Cari", "Carlos", "Carson", "Carter", "Cavan", "Chale", "Chandler",
			"Charles ", "Chen", "Chevalier", "Chilton", "Christoph", "Clark", "Clement", "Clinton", "Colin", "Conan",
			"Conrad", "Cornell", "Crispin", "Curtis", "Cyrus", "Dalton", "Dan", "Daniel", "Danny", "Dante", "Daren",
			"Darin", "Darren", "Dave", "David", "Davin", "Davis", "Delbert ", "Dennis", "Derek", "Devlin", "Dick",
			"Dionysius", "Dirk", "Dixon", "Dominick ", "Donal", "Douglas", "Duncan", "Dustin", "Eddy", "Edison",
			"Edward", "Edwin", "Elden", "Elgin", "Elkan", "Elliot", "Elmo", "Elvin", "Emanuel", "Emil", "Emmanel",
			"Enos", "Ennis", "Enrico", "Er", "Erek", "Eric", "Erik", "Ernest", "Ervin", "Ethan", "Eugene", "Evan",
			"Fabian", "Faris", "Felix", "Ferdinand", "Fergus", "Ferguson", "Fidel", "Finley", "Finn", "Fisk", "Floyd",
			"Forrest", "Forster", "Foster", "Frank", "Franklin", "Franz", "Frasier", "Fred", "Freddy", "Frederick",
			"Freeman", "Fremont", "Fritz", "Fuller", "Gabe", "Gabriel", "Gage", "Galen", "Galeno", "Galvin", "Gamal",
			"Garfield", "Garrick", "Garrison", "Gavin", "Gaylord", "Geoffrey", "George", "Gerald", "Gibson", "Gideon",
			"Gilbert", "Giles", "Glenn", "Gordon", "Grady", "Graham", "Grant", "Grayson", "Habib", "Hackett", "Hadley",
			"Hale", "Hamdi", "Hamilton", "Hamlet", "Hank", "Hans", "Hardy", "Harlan", "Harman", "Harold", "Harper",
			"Harry", "Hassan", "Heath", "Hector", "Henry", "Herbert", "Herman", "Hilton", "Holden", "Hollis", "Howard",
			"Iain", "Ian", "Ibrahim", "Id", "Iggi", "Iggy", "Ignatius", "Ihsan", "Ike", "Ilias", "Iman", "Ingmar",
			"Ingram", "Irving", "Isa", "Isaac", "Isaiah", "Ishmael", "Ismail", "Israel", "Issay", "Itzak", "Ivan",
			"Ivar", "Ivo", "Jaafar", "Jack", "Jackson", "Jacob", "Jake", "James", "Jamison", "Jarek", "Jarvis", "Jason",
			"Jasper", "Jedrek", "Jeff", "Jefferson", "Jeffrey", "Jeremiah", "Jerry", "Jesus", "Jim", "Jimmy", "Joe",
			"John", "Joseph", "Joshua", "Justin", "Kai", "Kalman", "Karl", "Keith", "Kelemen", "Kellen", "Kelvin",
			"Ken", "Kenelm", "Kennard", "Kenneth", "Kent", "Kenton", "Kenny", "Kenyon", "Kermit", "Kern", "Kester",
			"Kevin", "Khalil", "Kiefer", "Kimball", "Kiros", "Koen", "Kontar", "Lamar", "Lamont", "Lance", "Lancelot",
			"Lang", "Larry", "Laurence", "Leo", "Leon", "Leonard", "Leroy", "Levi", "Lewis", "Lexi", "Lian", "Lisle",
			"Livvy", "Lloyd", "London", "Lou", "Lucas", "Luce", "Lucius", "Luke", "Lyndon", "Maarten", "Madison",
			"Magnus", "Mahmud", "Malcolm", "Marc", "Marcel", "Marcello", "Marco", "Mark", "Marlon", "Marshall",
			"Marvin", "Matthew", "Maurice", "Max", "Maxime", "Merlin", "Mick", "Mike", "Miles", "Milo", "Montgomery",
			"Morris", "Morrison", "Nabil", "Nalu", "Nasir", "Nate", "Nathan", "Nathaniel", "Neal", "Neely", "Neil",
			"Nelson", "Nessan", "Neville", "Nevin", "Nicholas", "Nick", "Niel", "Nigel", "Nika", "Nils", "Noah", "Noel",
			"Nolan", "Norman", "Norton", "Oakes", "Oakley", "Obert", "Octavio", "Odon", "Odysseus", "Olin", "Oliver",
			"Omar", "Oran", "Oren", "Oringo", "Orion", "Orlando", "Orman", "Ormand", "Orsen", "Orson", "Orville",
			"Oscar", "Oskar", "Othello", "Ovid", "Owen", "Ozzie", "Pablo", "Paddy", "Palma", "Palmer", "Paris",
			"Parker", "Parry", "Parson", "Pascal", "Pasi", "Patrick", "Paul", "Paulo", "Penn", "Perry", "Peter",
			"Philip", "Phillip", "Pierre", "Pierson", "Platon", "Polo", "Porter", "Prince", "Qasim", "Qays", "Quentin",
			"Quillan", "Quincy", "Ranen", "Raymon", "Regan", "Reuben", "Rex", "Reynard", "Riane", "Rich", "Richard",
			"Rick", "Rider", "Riordon", "Robert", "Rock", "Roderick", "Rodman", "Roger", "Roland", "Roman", "Romeo",
			"Ronald", "Roy", "Royce", "Ruben", "Rudolph", "Sabri", "Salah", "Salman", "Salvador", "Sam", "Samson",
			"Samuel", "Sanders", "Santos", "Saunders", "Saxon", "Scott", "Seamus", "Seth", "Simba", "Simon", "Slade",
			"Smith", "Solomon", "Stanley", "Stephen", "Steve", "Steven", "Stuart", "Sylvester", "Ted", "Terrence",
			"Thaddeus", "Thanos", "Theodore", "Theron", "Thierry", "Thomas", "Tierney", "Tilden", "Tim", "Timothy",
			"Tino", "Tobey", "Todd", "Tom", "Tomas", "Tommy", "Tony", "Tracis", "Trevor", "Trey", "Truman", "Tymon",
			"Tyson", "Uba", "Ulric", "Ulysses", "Urban", "Uri", "Vail", "Vernon", "Victor", "Vincent", "Vinnie",
			"Walden", "Waldo", "Walid", "Walker", "Wallace", "Walter", "Ward", "Warner", "Wayne", "Webster", "Wesley",
			"Whitby", "Whitfield", "Whitley", "Wilbur", "Wiley", "Wilfred", "Will", "William", "Willie", "Willis",
			"Wilson", "Winslow ", "Winston", "Wolfgang", "Xanthus", "Xavier", "Xerxes", "Yale", "Yancy", "Yank",
			"Yannis", "Yaro", "Yasir", "Yehudi", "York", "Yosef ", "Yule", "Zach", "Zaki", "Zalman", "Zane", "Zion"));
}
