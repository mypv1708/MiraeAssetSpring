package com.distributed.miraeasset.controller;

import com.distributed.miraeasset.common.BaseResponse;
import com.distributed.miraeasset.dto.request.*;
import com.distributed.miraeasset.entity.CitizenIdentification;
import com.distributed.miraeasset.entity.EKYC;
import com.distributed.miraeasset.entity.UserProfile;
import com.distributed.miraeasset.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<?>> register(@RequestBody AccountRegisterRequest request) {
        return ResponseEntity.ok(accountService.register(request));
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<?>> authenticate(@RequestBody UserCredentials request, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(accountService.authenticate(request, httpServletResponse));
    }

    @RequestMapping(value = "/add-user-profile", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<UserProfile>> createUserProfile(@RequestBody UserProfileRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(accountService.createUserProfile(request, httpServletRequest, httpServletResponse));
    }

    @RequestMapping(value = "/add-user-citizen-identification", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<CitizenIdentification>> createUserCitizenIdentification(@RequestBody UserCitizenIdentificationRequest request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(accountService.createUserCitizenIdentification(request, httpServletRequest));
    }

    @RequestMapping(value = "/upload-image-identification", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<?>> uploadFiles(@RequestParam("files") MultipartFile[] files, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(accountService.uploadMultiple(files, httpServletRequest));
    }

    @RequestMapping(value = "/create-request-ekyc", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<EKYC>> createRequestEkyc(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(accountService.createRequestEkyc(httpServletRequest));
    }

//    @RequestMapping(value = "/create-request-loan", method = RequestMethod.POST)
//    public ResponseEntity<BaseResponse<CitizenIdentification>> createUserCitizenIdentification(@RequestBody UserCitizenIdentificationRequest request, HttpServletRequest httpServletRequest) {
//        return ResponseEntity.ok(accountService.createUserCitizenIdentification(request, httpServletRequest));
//    }
}
