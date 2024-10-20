package com.distributed.miraeasset.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EkycRequest {
    private String status;
    private String review;
}
