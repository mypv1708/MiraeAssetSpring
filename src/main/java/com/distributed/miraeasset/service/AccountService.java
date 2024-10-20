package com.distributed.miraeasset.service;

import com.distributed.miraeasset.common.BaseResponse;
import com.distributed.miraeasset.dto.request.*;
import com.distributed.miraeasset.entity.CitizenIdentification;
import com.distributed.miraeasset.entity.EKYC;
import com.distributed.miraeasset.entity.UserProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AccountService {
    BaseResponse<?> register(AccountRegisterRequest accountRequest);

    BaseResponse<?> authenticate(UserCredentials request, HttpServletResponse httpServletResponse);

    BaseResponse<UserProfile> createUserProfile(UserProfileRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    BaseResponse<CitizenIdentification> createUserCitizenIdentification(UserCitizenIdentificationRequest request, HttpServletRequest httpServletRequest);

    BaseResponse<?> uploadMultiple(MultipartFile[] files, HttpServletRequest httpServletRequest);

    BaseResponse<EKYC> createRequestEkyc(HttpServletRequest httpServletRequest);
}
