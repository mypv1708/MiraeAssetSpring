package com.distributed.miraeasset.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRegisterRequest {
    private String userAccount;
    private String email;
    private String phoneNumber;
    private String password;
    private String confirmPassword;
}
