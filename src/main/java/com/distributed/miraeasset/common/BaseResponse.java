package com.distributed.miraeasset.common;

import com.distributed.miraeasset.constants.MessageUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse <T> extends Header {
    private String responseCode;
    private String messageVi;
    private String messageEn;
    private T data;


    public BaseResponse success(){
        return new BaseResponse(MessageUnit.CODE_SUCCESS, MessageUnit.MESSAGE_VI_SUCCESS,MessageUnit.MESSAGE_EN_SUCCESS,null);
    }
    public BaseResponse success(T data){
        return new BaseResponse(MessageUnit.CODE_SUCCESS, MessageUnit.MESSAGE_VI_SUCCESS,MessageUnit.MESSAGE_EN_SUCCESS,data);
    }
    public BaseResponse error(String code,String messageVi,String messageEn,T data){
        return new BaseResponse(code,messageVi,messageEn,data);
    }
}
