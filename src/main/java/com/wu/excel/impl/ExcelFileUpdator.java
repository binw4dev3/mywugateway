package com.wu.excel.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wu.excel.ExcelException;
import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.util.UtilFunctions;

public class ExcelFileUpdator extends ExcelFileProcessor {

	public static boolean updateToExcel(String path, IExcelObjectDelegator delegator, Object updateData) throws ExcelException, IOException {
		
		if (path == null || EMPTY.equals(path)) {
			
			throw new ExcelException(ExcelException.EMPTY_PATH_ERROR);
			
		} else {
			
			String postfix = getPostfix(path);
			
			if (!EMPTY.equals(postfix)) {
				
				InputStream is = new FileInputStream(path);
				
				UtilFunctions.logger.info(PROCESSING_FOR_UPDATE + path);
				
				Workbook workbook = null;
				
				if (OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
					
					workbook = new HSSFWorkbook(is);
					
				} else if (OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
					
					workbook = new XSSFWorkbook(is);
				}
				
				delegator.dataUpdate(workbook, updateData);
				
				is.close();
				
				OutputStream os =new FileOutputStream(path);
				workbook.write(os);
				os.close();
				
			} else {
				throw new ExcelException(ExcelException.NOT_AN_EXCEL_FILE_ERROR);
			}
		}
		
		return true;
	}
}
