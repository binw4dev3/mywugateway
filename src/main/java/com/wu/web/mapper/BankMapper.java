package com.wu.web.mapper;

import com.wu.web.model.Bank;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface BankMapper {
    List<Bank> getAllBanks();
    boolean addBank(Bank bank);
}
