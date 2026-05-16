package com.wu.gw.mybatis.mapper;

import com.wu.gw.mybatis.model.CountryCurrencyPair;

public interface CountryCurrencyPairMapper {

	public int addCountryCurrencyPair(CountryCurrencyPair ccPair);
	
	public CountryCurrencyPair getCountryCurrencyPairById(Integer id);
	
	public CountryCurrencyPair getCountryCurrencyPairByKeys(CountryCurrencyPair ccPair);
	
	public int updateCountryCurrencyPair(CountryCurrencyPair ccPair);
}
