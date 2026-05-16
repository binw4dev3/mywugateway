package com.wu.web.mapper;

import com.wu.web.model.Branch;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface BranchMapper {
    boolean addBranch(Branch branch);
}
