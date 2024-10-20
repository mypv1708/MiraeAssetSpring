package com.distributed.miraeasset.repository;

import com.distributed.miraeasset.entity.UserProfile;
import org.springframework.data.jpa.repository.Query;

public interface CustomerDetailRepositoryServer extends BaseAccountRepository<UserProfile>{
    @Query("SELECT u.branch.branchId FROM UserProfile u WHERE u.account.accountId = :accountId")
    Integer findBranchByAccountId(Integer accountId);

    @Query("SELECT u FROM UserProfile u WHERE u.account.accountId = :accountId")
    UserProfile findCustomerProfileByAccountId(Integer accountId);
}
