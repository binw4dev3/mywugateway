package com.wu.excel;

import java.util.HashMap;

public class ExcelException extends RuntimeException {

	private static final long serialVersionUID = 8722032228952576711L;

	public static final String NOT_AN_EXCEL_FILE_ERROR = "E0001";
	public static final String NOT_AN_EXCEL_FILE_ERROR_MSG= "Not an Excel file";
	
	public static final String EMPTY_PATH_ERROR = "E0002";
	private static final String EMPTY_PATH_ERROR_MSG = "The file path is empty";
	
	public static final String EXPORT_EXCEL_ERROR = "E0003";
	
	public static final String CLOSE_EXCEL_ERROR = "E0004";
	
	public static HashMap<String, String> errorMap = new HashMap<String, String>();
	
	static {
		errorMap.put(NOT_AN_EXCEL_FILE_ERROR, NOT_AN_EXCEL_FILE_ERROR_MSG);
		errorMap.put(EMPTY_PATH_ERROR, EMPTY_PATH_ERROR_MSG);
	}
	
	public ExcelException(String errorCode) {
		
		this.errorCode = errorCode;
		this.errorMessage = errorMap.get(errorCode);
	}
	
	public ExcelException(String errorCode, String errorMessage) {
		
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	private String errorCode = null;
	
	private String errorMessage = null;
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String toString() {
		
		return errorCode + " " + errorMessage;
	}
}
