package com.wu.gw.mybatis.mapper;

import com.wu.gw.mybatis.model.CountryCurrencyList;

public interface CountryCurrencyListMapper {

	public int addCountryCurrencyList(CountryCurrencyList ccList);
	
	public CountryCurrencyList getCountryCurrencyList(String listName);
	
	public int updateCountryCurrencyListByListName(CountryCurrencyList ccList);
}
