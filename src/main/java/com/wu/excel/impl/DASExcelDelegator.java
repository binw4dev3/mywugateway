package com.wu.excel.impl;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.model.das.AbstractDASRecord;
import com.wu.gw.model.das.DASQueryResult;

public class DASExcelDelegator implements IExcelObjectDelegator {

	private DASQueryResult dasQueryData = null;
	
	private int dataRowNum = 1;

	public DASExcelDelegator(DASQueryResult dasQueryData) {
		
		this.dasQueryData = dasQueryData;
	}

	@Override
	public HSSFSheet parseXlsSheet(Workbook workbook) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseDataFromRowXls(Row row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void buildHeader(Sheet sheet) {

		Row head = sheet.createRow(0);

		ArrayList<String> headerList = dasQueryData.getDataHeader();

		if(headerList != null) {
		
			for (int i = 0, n = headerList.size(); i < n; i++) {
	
				Cell cell = head.createCell(i);
	
				cell.setCellValue(headerList.get(i));
			}
		}
	}

	@Override
	public void buildDataRows(Sheet sheet) {

		ArrayList<AbstractDASRecord> dasRecordList = dasQueryData.getRecordSet();

		ArrayList<String> headerList = dasQueryData.getDataHeader();
		
		for (AbstractDASRecord dasRecord : dasRecordList) {
			
			Row dataRow = sheet.createRow(dataRowNum);
			
			dataRowNum++;
			
			HashMap<String, String> columnData = dasRecord.getColumnData();
			
			for (int i = 0, n = headerList.size(); i < n; i++) {

				Cell cell = dataRow.createCell(i);

				cell.setCellValue(columnData.get(headerList.get(i)));
			}
		}
	}

	public DASQueryResult getDasQueryData() {
		return dasQueryData;
	}

	public void setDasQueryData(DASQueryResult dasQueryData) {
		this.dasQueryData = dasQueryData;
	}

	@Override
	public void buildDataRowsAppend(Sheet sheet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dataUpdate(Workbook workBook, Object updateData) {
		// TODO Auto-generated method stub
		return true;
	}
}
