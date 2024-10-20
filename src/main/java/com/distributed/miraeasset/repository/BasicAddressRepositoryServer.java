package com.distributed.miraeasset.repository;

import com.distributed.miraeasset.entity.BasicAddress;
import org.springframework.data.jpa.repository.Query;

public interface BasicAddressRepositoryServer extends BaseAccountRepository<BasicAddress>{
    @Query(value = "select b from BasicAddress b where b.city =:city and b.district =:district and b.ward =:ward ")
    BasicAddress finPlaceIdByAddress(String city, String district, String ward);
}
