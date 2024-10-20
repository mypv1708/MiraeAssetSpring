package com.distributed.miraeasset.repository;

import com.distributed.miraeasset.entity.Branch;
import org.springframework.data.jpa.repository.Query;

public interface BranchRepositoryServer extends BaseAccountRepository<Branch>{
    @Query(value = "select b from Branch b where b.branchName = :branchName ")
    Branch findIdByBranch(String branchName);

    @Query(value = "select b.branchCode from Branch b where b.branchId = :branchId ")
    String findNameByBranchId(Integer branchId);
}
