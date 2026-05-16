package com.wu.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wu.gw.mybatis.model.CountryCurrencyPair;
import com.wu.web.model.RespBean;
import com.wu.web.service.CountryCurrencyPairService;

@RestController
public class CountryCurrencyPairController {

	@Autowired
	CountryCurrencyPairService ccPairService;
	
	@GetMapping("/getccpair")
	public RespBean getCountryCurrencyPairById(@RequestParam Integer id) {
		CountryCurrencyPair ccPair = ccPairService.getCountryCurrencyPairById(id);
		if(ccPair != null) {
			return RespBean.ok("CountryCurrency Pair : ", ccPair);
		}
		
		return RespBean.error("Country Currency Pair does not exist : " + id);
	}
	
	@PostMapping("/addccpair")
	public RespBean addCountryCurrencyPair(@RequestBody CountryCurrencyPair ccPair) {
		return ccPairService.addCountryCurrencyPair(ccPair);
	}
	
	@DeleteMapping("/deleteccpair")
	public RespBean deleteCountryCurrencyPair(@RequestParam Integer id) {
		return ccPairService.deleteCountryCurrencyPair(id);
	}
}
