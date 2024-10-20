package com.distributed.miraeasset.repository;

import com.distributed.miraeasset.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepositoryServer extends BaseAccountRepository<Account> {
    Account findByEmail(String email);

    Account findByUserAccount(String userAccount);

    @Query(value = "select u from Account u where u.email =:email and u.password =:password and u.isAdmin = false ")
    Account getUserAuthenticate(String email, String password);
}
