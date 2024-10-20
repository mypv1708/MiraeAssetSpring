package com.distributed.miraeasset.dto.request;

import com.distributed.miraeasset.entity.BasicAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCitizenIdentificationRequest {
    private String fullName;
    private Date Dob;
    private String gender;
    private String nationality;
    private String cityOrigin;
    private String districtOrigin;
    private String wardOrigin;
    private String cityResidence;
    private String districtResidence;
    private String wardResidence;
    private String issuedAt;
    private Date issuedDate;
    private Date cardValidity;
}
