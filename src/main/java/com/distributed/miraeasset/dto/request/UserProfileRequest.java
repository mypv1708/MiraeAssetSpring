package com.distributed.miraeasset.dto.request;

import com.distributed.miraeasset.entity.Account;
import com.distributed.miraeasset.entity.BasicAddress;
import com.distributed.miraeasset.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    private String city;
    private String district;
    private String ward;
    private String branchName;
    private String fullName;
    private Date dateOfBirth;
    private String gender;
    private String job;
    private BigDecimal income;
    private String detailAddress;
}
