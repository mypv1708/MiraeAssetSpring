package com.distributed.miraeasset.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class JwtResponse implements Serializable {
    private static final long serialVersionUID = 7564750209741580927L;
    private String email;
    private String jwtToken;
    private String expiredToken;
}
