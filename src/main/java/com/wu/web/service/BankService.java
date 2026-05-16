package com.wu.web.service;

import com.wu.web.mapper.BankMapper;
import com.wu.web.model.Bank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {
    @Autowired
    BankMapper bankMapper;

    public List<Bank> getAllBanks() {
        return bankMapper.getAllBanks();
    }

    public boolean addBank(Bank bank) {
        return bankMapper.addBank(bank);
    }
}
