package com.distributed.miraeasset.repository;

import com.distributed.miraeasset.entity.BasicAddress;
import com.distributed.miraeasset.entity.CitizenIdentification;
import com.distributed.miraeasset.entity.UserProfile;
import org.springframework.data.jpa.repository.Query;

public interface CitizenIdentificationRepositoryBranch extends BaseAccountRepository<CitizenIdentification>{
    @Query("SELECT c FROM CitizenIdentification c WHERE c.customerId.customerId = :userCurrentId")
    CitizenIdentification findCitizenIdentificationByCustomerId(Integer userCurrentId);
}
