package com.wu.web.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wu.gw.mybatis.mapper.CountryCurrencyListMapper;
import com.wu.gw.mybatis.model.CountryCurrencyList;
import com.wu.web.model.RespBean;

@Service
public class CountryCurrencyListService {

	@Autowired
	CountryCurrencyListMapper ccListMapper;
	
	public RespBean addCountryCurrencyList(CountryCurrencyList ccList) {
		if(ccList.getListName() == null || 
			ccList.getListName().equals("") || 
			ccList.getSCountry() == null || 
			ccList.getSCountry().equals("") || 
			ccList.getSCurrency() == null || 
			ccList.getSCurrency().equals("") || 
			ccList.getSNaid() == null || 
			ccList.getSNaid().equals("")) {
			return RespBean.error("Missing or invalid data field in the CountryCurrencyList");
		}
		
		CountryCurrencyList ccListDB = ccListMapper.getCountryCurrencyList(ccList.getListName());
		if(ccListDB != null) {
			return RespBean.error("CountryCurrencyList is existing in Database.");
		}
		
		ccList.setUpdateTime(new Date());
		
		if(ccListMapper.addCountryCurrencyList(ccList) > 0) {
			return RespBean.ok("New CountryCurrency List added in DB.", ccList);
		}
		
		return RespBean.error("Failed to add new CountryCurrency List in DB.");
	}
	
	public CountryCurrencyList getCountryCurrencyList(String listName) {
		return ccListMapper.getCountryCurrencyList(listName);
	}
	
	public RespBean updateCountryCurrencyListByListName(String listname) {
		CountryCurrencyList ccList = ccListMapper.getCountryCurrencyList(listname);
		if(ccList != null) {
			ccList.setUpdateTime(new Date());
			if(ccListMapper.updateCountryCurrencyListByListName(ccList) > 0) {
				return RespBean.ok("Country Currency List has been updated : " + listname);
			} else {
				return RespBean.error("Failed to update the Country Currency List : ", listname);
			}
		} else {
			return RespBean.error("Unable to find CountryCurrencyList in Database : ", listname);
		}
	}
}
