package com.wu.gw.util;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;

public class FileUtils {

	public static HashMap<String, File> scanFiles(String rootPath, String folder) {
		String fileDir = rootPath + folder;
		fileDir = URLDecoder.decode(fileDir);
		File srcDir = new File(fileDir);

		HashMap<String, File> fileMap = null;
		
		if (!srcDir.isDirectory()) {

			System.out.println("Invalid xml file directory.");

		} else {

			File[] files = srcDir.listFiles();

			fileMap = new HashMap<String, File>();
			
			for (File file : files) {
				
				if (UtilFunctions.getFileExtName(file).equals("xml")) {

					fileMap.put(file.getName(), file);
				}
			}
		}
		
		return fileMap;
	}
}
