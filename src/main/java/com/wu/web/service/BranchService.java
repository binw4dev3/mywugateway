package com.wu.web.service;

import com.wu.web.mapper.BranchMapper;
import com.wu.web.model.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchService {
    @Autowired
    BranchMapper branchMapper;

    public boolean addBranch(Branch branch) {
        return branchMapper.addBranch(branch);
    }
}
