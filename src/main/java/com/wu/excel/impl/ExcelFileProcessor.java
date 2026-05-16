package com.wu.excel.impl;

import com.wu.excel.IExcelFileProcessor;

public class ExcelFileProcessor implements IExcelFileProcessor {

	public static String getPostfix(String path) {
		
		if (path == null || EMPTY.equals(path.trim())) {
			
			return EMPTY;
			
		}
		
		if (path.contains(POINT)) {
			
			return path.substring(path.lastIndexOf(POINT) + 1, path.length());
			
		}
		
		return EMPTY;
	}
}
