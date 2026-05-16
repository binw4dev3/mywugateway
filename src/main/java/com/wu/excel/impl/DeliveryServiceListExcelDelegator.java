package com.wu.excel.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.wu.excel.IExcelObjectDelegator;
import com.wu.gw.model.APNTemplate;
import com.wu.gw.model.APNTemplateDataEntry;

public class DeliveryServiceListExcelDelegator implements IExcelObjectDelegator {

	private ArrayList<APNTemplate> apnTemplateList = null;

	private int dataRowNum = 1;

	public DeliveryServiceListExcelDelegator(ArrayList<APNTemplate> apnTemplateList) {
		this.apnTemplateList = apnTemplateList;
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

		ArrayList<String> headerList = new ArrayList<String>(
				Arrays.asList("Country/Currency", "Service Code", "Template ID", "Available"));

		if (headerList != null) {

			for (int i = 0, n = headerList.size(); i < n; i++) {

				Cell cell = head.createCell(i);

				cell.setCellValue(headerList.get(i));
			}
		}
	}

	@Override
	public void buildDataRows(Sheet sheet) {
		if (apnTemplateList == null) {
			return;
		}

		Cell cell = null;
		for (int i = 0, n = apnTemplateList.size(); i < n; i++) {
			APNTemplate apnTemplate = apnTemplateList.get(i);

			Row dataRow = sheet.createRow(dataRowNum);
			dataRowNum++;
			
			cell = dataRow.createCell(0);
			cell.setCellValue(apnTemplate.getCorridor());

			cell = dataRow.createCell(1);
			cell.setCellValue(apnTemplate.getServiceCode());
			
			cell = dataRow.createCell(2);
			cell.setCellValue(apnTemplate.getTemplateID());
			
			cell = dataRow.createCell(3);
			cell.setCellValue(apnTemplate.getStatus());
			
			//cell = dataRow.createCell(4);
			//cell.setCellValue(apnTemplate.getServiceDesp());
		}
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
