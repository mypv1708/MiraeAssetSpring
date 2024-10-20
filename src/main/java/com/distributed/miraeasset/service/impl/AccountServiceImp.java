package com.distributed.miraeasset.service.impl;

import com.distributed.miraeasset.common.BaseResponse;
import com.distributed.miraeasset.config.DataSourceContextHolder;
import com.distributed.miraeasset.constants.MessageUnit;
import com.distributed.miraeasset.dto.request.*;
import com.distributed.miraeasset.entity.*;
import com.distributed.miraeasset.repository.*;
import com.distributed.miraeasset.dto.response.JwtResponse;
import com.distributed.miraeasset.security.JwtTokenUtil;
import com.distributed.miraeasset.service.AccountService;
import com.distributed.miraeasset.service.ImageService;
import com.distributed.miraeasset.service.JwtUserDetailsService;
import com.distributed.miraeasset.utils.ValidateRequest;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@Service
public class AccountServiceImp implements AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImp.class);
    private final AccountRepositoryServer accountRepositoryServer;
    private final CustomerDetailRepositoryServer customerDetailRepositoryServer;
    private final BasicAddressRepositoryServer basicAddressRepositoryServer;
    private final BranchRepositoryServer branchRepositoryServer;
    private final EkycRepositoryBranch ekycRepositoryBranch;
    private final CitizenIdentificationRepositoryBranch citizenIdentificationRepositoryBranch;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImp(AccountRepositoryServer accountRepositoryServer, CustomerDetailRepositoryServer customerDetailRepositoryServer, BasicAddressRepositoryServer basicAddressRepositoryServer, BranchRepositoryServer branchRepositoryServer, EkycRepositoryBranch ekycRepositoryBranch, CitizenIdentificationRepositoryBranch citizenIdentificationRepositoryBranch, JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.accountRepositoryServer = accountRepositoryServer;
        this.customerDetailRepositoryServer = customerDetailRepositoryServer;
        this.basicAddressRepositoryServer = basicAddressRepositoryServer;
        this.branchRepositoryServer = branchRepositoryServer;
        this.ekycRepositoryBranch = ekycRepositoryBranch;
        this.citizenIdentificationRepositoryBranch = citizenIdentificationRepositoryBranch;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public BaseResponse<?> register(AccountRegisterRequest request) {
        DataSourceContextHolder.setDataSourceType("central");
        // Validate Empty
        if (!ValidateRequest.isNotEmpty(request.getUserAccount())) {
            log.error("[ERROR] [AccountServiceImp] [register] " + "Username: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Tên đăng nhập : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Username : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }

        if (!ValidateRequest.isNotEmpty(request.getEmail())) {
            log.error("[ERROR] [AccountServiceImp] [register] " + "Email: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Email : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Email : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }

        if (!ValidateRequest.isNotEmpty(request.getPhoneNumber())) {
            log.error("[ERROR] [AccountServiceImp] [register] " + "PhoneNumber: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Số điện thoại : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "PhoneNumber : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }

        if (!ValidateRequest.isNotEmpty(request.getPassword())) {
            log.error("[ERROR] [AccountServiceImp] [register] " + "Password: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Mật khẩu : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Password: " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.error("[ERROR] [AccountServiceImp] [register] " + request.getPassword() + "," + request.getConfirmPassword() + " : " + MessageUnit.MESSAGE_VI_NO_MATCHING_FIELDS);
            return new BaseResponse<>().error(MessageUnit.NO_MATCHING_FIELDS, request.getPassword() + "," + request.getConfirmPassword() + " : " + MessageUnit.MESSAGE_VI_NO_MATCHING_FIELDS, request.getPassword() + "," + request.getConfirmPassword() + " : " + MessageUnit.MESSAGE_EN_NO_MATCHING_FIELDS, null);
        }

        // Validate format
        if (!ValidateRequest.validateEmail(request.getEmail())) {
            log.error("[ERROR] [AccountServiceImp] [register] " + request.getEmail() + " : " + MessageUnit.MESSAGE_VI_FORMAT_WRONG);
            return new BaseResponse<>().error(MessageUnit.FORMAT_WRONG, request.getEmail() + " : " + MessageUnit.MESSAGE_VI_FORMAT_WRONG, request.getEmail() + " : " + MessageUnit.MESSAGE_EN_FORMAT_WRONG, null);
        }

        if (!ValidateRequest.validatePhone(request.getPhoneNumber())) {
            log.error("[ERROR] [AccountServiceImp] [register] " + request.getPhoneNumber() + " : " + MessageUnit.MESSAGE_VI_FORMAT_WRONG);
            return new BaseResponse<>().error(MessageUnit.FORMAT_WRONG, request.getPhoneNumber() + " : " + MessageUnit.MESSAGE_VI_FORMAT_WRONG, request.getPhoneNumber() + " : " + MessageUnit.MESSAGE_EN_FORMAT_WRONG, null);
        }

        if (!ValidateRequest.validatePassword(request.getPassword())) {
            log.error("[ERROR] [AccountServiceImp] [register] " + request.getPassword() + " : " + MessageUnit.MESSAGE_VI_FORMAT_PASSWORD_WRONG);
            return new BaseResponse<>().error(MessageUnit.FORMAT_PASSWORD_WRONG, request.getPassword() + " : " + MessageUnit.MESSAGE_VI_FORMAT_PASSWORD_WRONG, request.getPassword() + " : " + MessageUnit.MESSAGE_EN_FORMAT_PASSWORD_WRONG, null);
        }

        try {
            //Check xem email và UserAccount đã tồn tại chưa?
            Account isCheckUserAccountExits = accountRepositoryServer.findByUserAccount(request.getUserAccount().trim());
            Account isCheckEmailExits = accountRepositoryServer.findByEmail(request.getEmail().trim());

            if (Objects.nonNull(isCheckUserAccountExits)) {
                log.error("[ERROR] [AccountServiceImp] [register] " + request.getUserAccount() + " : " + MessageUnit.MESSAGE_VI_USER_EXITS);
                return new BaseResponse<>().error(MessageUnit.USER_EXITS, request.getUserAccount() + " : " + MessageUnit.MESSAGE_VI_USER_EXITS, request.getUserAccount() + " : " + MessageUnit.MESSAGE_EN_USER_EXITS, null);
            }
            if (Objects.nonNull(isCheckEmailExits)) {
                log.error("[ERROR] [AccountServiceImp] [register] " + request.getEmail() + " : " + MessageUnit.MESSAGE_VI_EMAIL_EXITS);
                return new BaseResponse<>().error(MessageUnit.EMAIL_EXITS, request.getEmail() + " : " + MessageUnit.MESSAGE_VI_EMAIL_EXITS, request.getEmail() + " : " + MessageUnit.MESSAGE_EN_EMAIL_EXITS, null);
            }

            Account account = new Account();
            account.setUserAccount(request.getUserAccount());
            account.setEmail(request.getEmail());
            account.setPhoneNumber(request.getPhoneNumber());
            account.setPassword(passwordEncoder.encode(request.getPassword()));
            account.setIsAdmin(false);
            account.setRowguid(UUID.randomUUID());
            accountRepositoryServer.save(account);
            return new BaseResponse<>().success(account);

        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [register] " + e.getMessage(), e);
            return new BaseResponse<>().error(MessageUnit.INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_VI_INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_EN_INTERNAL_ERROR_SERVER, null);
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    @Override
    public BaseResponse<?> authenticate(UserCredentials request, HttpServletResponse httpServletResponse) {
        try {
            // Tìm account trong database
            Account currentAccount = accountRepositoryServer.findByEmail(request.getEmail().trim());
            if (currentAccount != null && passwordEncoder.matches(request.getPassword().trim(), currentAccount.getPassword())) {
                // Xác thực thành công, tạo token JWT
                final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getEmail());
                final String token = jwtTokenUtil.generateToken(userDetails, String.valueOf(currentAccount.getIsAdmin()));
                final String expiredToken = String.valueOf(jwtTokenUtil.getExpirationDateFromToken(token).getTime());

                Integer branchIdDatabase = customerDetailRepositoryServer.findBranchByAccountId(currentAccount.getAccountId());
                String branchNameDatabase = branchRepositoryServer.findNameByBranchId(branchIdDatabase);
                if (Objects.isNull(branchNameDatabase)) {
                    log.info("[INFO] [UserServiceImp] [authenticate] " + branchNameDatabase + " : " + MessageUnit.BRANCH_NOT_EXITS);
                } else {
                    // Create a cookie with the branch information
                    Cookie branchCookie = new Cookie("userBranch", branchNameDatabase);
                    branchCookie.setMaxAge(7 * 24 * 60 * 60);
                    branchCookie.setPath("/");
                    branchCookie.setHttpOnly(true);
                    branchCookie.setSecure(true);
                    httpServletResponse.addCookie(branchCookie);
                }

                return new BaseResponse<>().success(new JwtResponse(currentAccount.getEmail(), token, expiredToken));
            } else {
                // Xác thực thất bại
                return new BaseResponse<>().error(MessageUnit.USER_EXITS_WRONG_PASS, MessageUnit.MESSAGE_VI_USER_EXITS_WRONG_PASS, MessageUnit.MESSAGE_EN_USER_EXITS_WRONG_PASS, null);
            }
        } catch (Exception e) {
            // Xử lý exception
            log.error("[ERROR] [AccountServiceImp] [authenticate] " + request.getEmail() + " : " + e.getMessage(), e);
            return new BaseResponse<>().error(MessageUnit.INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_VI_INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_EN_INTERNAL_ERROR_SERVER, null);
        }
    }

    @Override
    public BaseResponse<UserProfile> createUserProfile(UserProfileRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // Validate Empty
        if (!ValidateRequest.isNotEmpty(request.getCity())) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "City: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Tỉnh thành : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "City : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getDistrict())) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "District: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Quận,huyện : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "District : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getWard())) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "Ward: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Phường,xã : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Ward : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getBranchName())) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "Branch: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Chi nhánh : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Branch : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getFullName())) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "FullName: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Họ tên khách hàng : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "FullName : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(String.valueOf(request.getDateOfBirth()))) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "DateOfBirth: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Ngày sinh : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "DateOfBirth : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getGender())) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "Gender: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Giới tính : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Gender : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getJob())) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "Job: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Nghề nghiệp : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Job : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(String.valueOf(request.getIncome()))) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "Income: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Thu nhập : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Income : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getDetailAddress())) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + "DetailAddress: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Địa chỉ chi tiết : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "DetailAddress : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }

        String token = httpServletRequest.getHeader("Authorization").substring(7);
        // CheckToken đã hết hạn chưa?
        try {
            jwtTokenUtil.isTokenExpired(token);
        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + MessageUnit.MESSAGE_VI_TOKEN_EXPIRED);
            return new BaseResponse<>().error(MessageUnit.TOKEN_EXPIRED, MessageUnit.MESSAGE_VI_TOKEN_EXPIRED, MessageUnit.MESSAGE_EN_TOKEN_EXPIRED, null);
        }

        try {
            // Get User
            String email = jwtTokenUtil.decodeToken(token);
            Account currentUser = accountRepositoryServer.findByEmail(email.trim());
            if (Objects.isNull(currentUser)) {
                log.error("[ERROR] [AccountServiceImp] [createUserProfile] " + email + " : " + MessageUnit.MESSAGE_VI_USER_FORGOT);
                return new BaseResponse<>().error(MessageUnit.USER_FORGOT, email + " : " + MessageUnit.MESSAGE_VI_USER_FORGOT, email + " : " + MessageUnit.MESSAGE_EN_USER_FORGOT, null);
            }
            Branch checkBranchId = branchRepositoryServer.findIdByBranch(request.getBranchName());
            BasicAddress checkBasicAddress = basicAddressRepositoryServer.finPlaceIdByAddress(request.getCity(), request.getDistrict(), request.getWard());

            if (Objects.isNull(checkBranchId)) {
                log.error("[ERROR] [UserServiceImp] [createUserProfile] " + request.getBranchName() + " : " + MessageUnit.MESSAGE_VI_NOT_FOUND_BRANCH_ID);
                return new BaseResponse<>().error(MessageUnit.NOT_FOUND_BRANCH_ID, request.getBranchName() + " : " + MessageUnit.MESSAGE_VI_NOT_FOUND_BRANCH_ID, request.getBranchName() + " : " + MessageUnit.MESSAGE_EN_NOT_FOUND_BRANCH_ID, null);
            }

            UserProfile userProfile = new UserProfile();
            userProfile.setAccount(currentUser);
            userProfile.setCurrentAddress(checkBasicAddress);
            userProfile.setBranch(checkBranchId);
            userProfile.setFullName(request.getFullName());
            userProfile.setDateOfBirth(request.getDateOfBirth());
            userProfile.setJob(request.getJob());
            userProfile.setGender(request.getGender());
            userProfile.setIncome(request.getIncome());
            userProfile.setDetailAddress(request.getDetailAddress());
            userProfile.setRowguid(UUID.randomUUID());
            customerDetailRepositoryServer.save(userProfile);

            // Create a cookie with the branch information
            Cookie branchCookie = new Cookie("userBranch", checkBranchId.getBranchCode());
            branchCookie.setMaxAge(7 * 24 * 60 * 60);
            branchCookie.setPath("/");
            branchCookie.setHttpOnly(true);
            branchCookie.setSecure(true);
            httpServletResponse.addCookie(branchCookie);

            return new BaseResponse<>().success(userProfile);

        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [createUserProfile]: " + e.getMessage());
            return new BaseResponse<>().error(MessageUnit.INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_VI_INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_EN_INTERNAL_ERROR_SERVER, null);
        }
    }

    @Override
    public BaseResponse<CitizenIdentification> createUserCitizenIdentification(UserCitizenIdentificationRequest request, HttpServletRequest httpServletRequest) {
        // Validate Empty
        if (!ValidateRequest.isNotEmpty(request.getFullName())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "City: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Tỉnh thành : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "City : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(String.valueOf(request.getDob()))) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "District: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Quận,huyện : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "District : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getGender())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "Ward: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Phường,xã : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Ward : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getNationality())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "Branch: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Chi nhánh : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Branch : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(String.valueOf(request.getCityOrigin()))) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "FullName: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Họ tên khách hàng : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "FullName : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getCityOrigin())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "CityOrigin: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Tỉnh thành : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "CityOrigin : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getDistrictOrigin())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "DistrictOrigin: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Quận,huyện : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "DistrictOrigin : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getWardOrigin())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "WardOrigin: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Phường,xã : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "WardOrigin : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getCityResidence())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "CityResidence: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Tỉnh thành : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "CityResidence : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getDistrictResidence())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "DistrictResidence: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Quận,huyện : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "DistrictResidence : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getWardResidence())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "WardResidence: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Phường,xã : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "WardResidence : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(request.getIssuedAt())) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "Gender: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Giới tính : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Gender : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(String.valueOf(request.getIssuedDate()))) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "Job: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Nghề nghiệp : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Job : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        if (!ValidateRequest.isNotEmpty(String.valueOf(request.getCardValidity()))) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + "Income: " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS);
            return new BaseResponse<>().error(MessageUnit.REQUIRED_FIELDS, "Thu nhập : " + MessageUnit.MESSAGE_VI_REQUIRED_FIELDS, "Income : " + MessageUnit.MESSAGE_EN_REQUIRED_FIELDS, null);
        }
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        // CheckToken đã hết hạn chưa?
        try {
            jwtTokenUtil.isTokenExpired(token);
        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + MessageUnit.MESSAGE_VI_TOKEN_EXPIRED);
            return new BaseResponse<>().error(MessageUnit.TOKEN_EXPIRED, MessageUnit.MESSAGE_VI_TOKEN_EXPIRED, MessageUnit.MESSAGE_EN_TOKEN_EXPIRED, null);
        }
        try {
            // Get User
            String email = jwtTokenUtil.decodeToken(token);
            Account currentUser = accountRepositoryServer.findByEmail(email.trim());
            if (Objects.isNull(currentUser)) {
                log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + email + " : " + MessageUnit.MESSAGE_VI_USER_FORGOT);
                return new BaseResponse<>().error(MessageUnit.USER_FORGOT, email + " : " + MessageUnit.MESSAGE_VI_USER_FORGOT, email + " : " + MessageUnit.MESSAGE_EN_USER_FORGOT, null);
            }

            String branchNameCookie = getCookieValue(httpServletRequest, "userBranch");
            DataSourceContextHolder.setDataSourceType(branchNameCookie);

            UserProfile userCurrentId = customerDetailRepositoryServer.findCustomerProfileByAccountId(currentUser.getAccountId());
//            Integer branchIdDatabase = customerDetailRepositoryServer.findBranchByAccountId(currentUser.getAccountId());
//            String branchNameDatabase = branchRepositoryServer.findNameByBranchId(branchIdDatabase);
//            if (branchNameCookie == null || !branchNameCookie.equals(branchNameDatabase)) {
//                log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] Invalid branch code for user");
//                return new BaseResponse<>().error(MessageUnit.INVALID_BRANCH, "Invalid branch code for user", "Invalid branch code for user", null);
//            }

            BasicAddress checkPlaceOfpOriginIdByAddress = basicAddressRepositoryServer.finPlaceIdByAddress(request.getCityOrigin(), request.getDistrictOrigin(), request.getWardOrigin());
            BasicAddress checkPlaceOfResidenceIdByAddress = basicAddressRepositoryServer.finPlaceIdByAddress(request.getCityResidence(), request.getDistrictResidence(), request.getWardResidence());

            if (Objects.isNull(checkPlaceOfpOriginIdByAddress)) {
                log.error("[ERROR] [UserServiceImp] [createUserCitizenIdentification] " + request.getCityOrigin() + " : " + request.getDistrictOrigin() + " : " + request.getWardOrigin() + " : " + MessageUnit.MESSAGE_VI_NOT_FOUND_ADDRESS_ID);
                return new BaseResponse<>().error(MessageUnit.NOT_FOUND_ADDRESS_ID, request.getCityOrigin() + " : " + MessageUnit.MESSAGE_VI_NOT_FOUND_ADDRESS_ID, request.getCityOrigin() + " : " + MessageUnit.MESSAGE_EN_NOT_FOUND_ADDRESS_ID, null);
            }
            if (Objects.isNull(checkPlaceOfResidenceIdByAddress)) {
                log.error("[ERROR] [UserServiceImp] [createUserCitizenIdentification] " + request.getCityResidence() + " : " + request.getDistrictResidence() + " : " + request.getWardResidence() + " : " + MessageUnit.MESSAGE_VI_NOT_FOUND_ADDRESS_ID);
                return new BaseResponse<>().error(MessageUnit.NOT_FOUND_ADDRESS_ID, request.getCityResidence() + " : " + MessageUnit.MESSAGE_VI_NOT_FOUND_ADDRESS_ID, request.getCityResidence() + " : " + MessageUnit.MESSAGE_EN_NOT_FOUND_ADDRESS_ID, null);
            }

            CitizenIdentification citizenIdentification = new CitizenIdentification();
            citizenIdentification.setFullName(request.getFullName());
            citizenIdentification.setDob(request.getDob());
            citizenIdentification.setGender(request.getGender());
            citizenIdentification.setNationality(request.getNationality());
            citizenIdentification.setPlaceOfpOriginId(checkPlaceOfpOriginIdByAddress);
            citizenIdentification.setPlaceOfResidenceId(checkPlaceOfResidenceIdByAddress);
            citizenIdentification.setIssuedAt(request.getIssuedAt());
            citizenIdentification.setIssuedDate(request.getIssuedDate());
            citizenIdentification.setCardValidity(request.getCardValidity());
            citizenIdentification.setRowguid(UUID.randomUUID());
            citizenIdentification.setCustomerId(userCurrentId);
            citizenIdentificationRepositoryBranch.save(citizenIdentification);

            return new BaseResponse<>().success(citizenIdentification);

        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification]: " + e.getMessage());
            return new BaseResponse<>().error(MessageUnit.INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_VI_INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_EN_INTERNAL_ERROR_SERVER, null);
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    @Override
    public BaseResponse<?> uploadMultiple(MultipartFile[] multipartFiles, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        // CheckToken đã hết hạn chưa?
        try {
            jwtTokenUtil.isTokenExpired(token);
        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [uploadMultiple] " + MessageUnit.MESSAGE_VI_TOKEN_EXPIRED);
            return new BaseResponse<>().error(MessageUnit.TOKEN_EXPIRED, MessageUnit.MESSAGE_VI_TOKEN_EXPIRED, MessageUnit.MESSAGE_EN_TOKEN_EXPIRED, null);
        }

        try {
            // Get User
            String email = jwtTokenUtil.decodeToken(token);
            Account currentAccountUser = accountRepositoryServer.findByEmail(email.trim());
            if (Objects.isNull(currentAccountUser)) {
                log.error("[ERROR] [AccountServiceImp] [uploadMultiple] " + email + " : " + MessageUnit.MESSAGE_VI_USER_FORGOT);
                return new BaseResponse<>().error(MessageUnit.USER_FORGOT, email + " : " + MessageUnit.MESSAGE_VI_USER_FORGOT, email + " : " + MessageUnit.MESSAGE_EN_USER_FORGOT, null);
            }

            String branchNameCookie = getCookieValue(httpServletRequest, "userBranch");
            DataSourceContextHolder.setDataSourceType(branchNameCookie);

            UserProfile userCurrentId = customerDetailRepositoryServer.findCustomerProfileByAccountId(currentAccountUser.getAccountId());
            CitizenIdentification citizenIdentification = citizenIdentificationRepositoryBranch.findCitizenIdentificationByCustomerId(userCurrentId.getCustomerId());

//            Integer branchIdDatabase = customerDetailRepositoryServer.findBranchByAccountId(currentAccountUser.getAccountId());
//            String branchNameDatabase = branchRepositoryServer.findNameByBranchId(branchIdDatabase);
//            if (branchNameCookie == null || !branchNameCookie.equals(branchNameDatabase)) {
//                log.error("[ERROR] [AccountServiceImp] [uploadMultiple] Invalid branch code for user");
//                return new BaseResponse<>().error(MessageUnit.INVALID_BRANCH, "Invalid branch code for user", "Invalid branch code for user", null);
//            }

            String imageFront = currentAccountUser.getUserAccount() + "_cccd_mat_truoc";
            String imageBack = currentAccountUser.getUserAccount() + "_cccd_mat_sau";
            List<String> imageNameList = List.of(imageFront, imageBack);

            List<String> urls = new ArrayList<>();
            int index = 0;
            for (MultipartFile file : multipartFiles) {
                if (index < imageNameList.size()) {
                    String imageName = imageNameList.get(index);
                    String url = this.upload(file, imageName, branchNameCookie);
                    urls.add(url);
                    index++;
                }
            }


            if (urls.size() == 2) {
                citizenIdentification.setFrontCitizenImage(urls.get(0));
                citizenIdentification.setBackCitizenImage(urls.get(1));
            }
//            else if (urls.size() == 1) {
//                citizenIdentification.setFrontCitizenImage(urls.get(0));
//            }
            citizenIdentificationRepositoryBranch.save(citizenIdentification);
            return new BaseResponse<>().success(citizenIdentification);

        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [uploadMultiple]: " + e.getMessage());
            return new BaseResponse<>().error(MessageUnit.INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_VI_INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_EN_INTERNAL_ERROR_SERVER, null);
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    @Override
    public BaseResponse<EKYC> createRequestEkyc(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        // CheckToken đã hết hạn chưa?
        try {
            jwtTokenUtil.isTokenExpired(token);
        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + MessageUnit.MESSAGE_VI_TOKEN_EXPIRED);
            return new BaseResponse<>().error(MessageUnit.TOKEN_EXPIRED, MessageUnit.MESSAGE_VI_TOKEN_EXPIRED, MessageUnit.MESSAGE_EN_TOKEN_EXPIRED, null);
        }
        try {
            // Get User
            String email = jwtTokenUtil.decodeToken(token);
            Account currentUser = accountRepositoryServer.findByEmail(email.trim());
            if (Objects.isNull(currentUser)) {
                log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification] " + email + " : " + MessageUnit.MESSAGE_VI_USER_FORGOT);
                return new BaseResponse<>().error(MessageUnit.USER_FORGOT, email + " : " + MessageUnit.MESSAGE_VI_USER_FORGOT, email + " : " + MessageUnit.MESSAGE_EN_USER_FORGOT, null);
            }

            String branchNameCookie = getCookieValue(httpServletRequest, "userBranch");
            DataSourceContextHolder.setDataSourceType(branchNameCookie);

            UserProfile userCurrentId = customerDetailRepositoryServer.findCustomerProfileByAccountId(currentUser.getAccountId());

            EKYC ekyc = new EKYC();
            ekyc.setCustomerId(userCurrentId);
            ekyc.setRowguid(UUID.randomUUID());
            ekycRepositoryBranch.save(ekyc);

            return new BaseResponse<>().success(ekyc);

        } catch (Exception e) {
            log.error("[ERROR] [AccountServiceImp] [createUserCitizenIdentification]: " + e.getMessage());
            return new BaseResponse<>().error(MessageUnit.INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_VI_INTERNAL_ERROR_SERVER, MessageUnit.MESSAGE_EN_INTERNAL_ERROR_SERVER, null);
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }

    }

    private String uploadFile(File file, String fileName, String contentType, String branch) throws IOException {
        BlobId blobId = BlobId.of("distributejava.appspot.com", branch + "/" + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();

//        InputStream inputStream = AccountService.class.getClassLoader().getResourceAsStream("distributejava-firebase-adminsdk-9l9a7-3010584d70.json");
        InputStream inputStream = ImageService.class.getClassLoader().getResourceAsStream("distributejava-firebase-adminsdk-9l9a7-3010584d70.json");

        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        Blob blob = storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        String token = UUID.randomUUID().toString();
        blob.toBuilder().setMetadata(Map.of("firebaseStorageDownloadTokens", token)).build().update();

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s";

        return String.format(DOWNLOAD_URL, blob.getBucket(), URLEncoder.encode(branch + "/" + fileName, StandardCharsets.UTF_8), token);
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    public String upload(MultipartFile multipartFile, String fileName, String branch) {
        try {
            File file = this.convertToFile(multipartFile, fileName);
            String contentType = multipartFile.getContentType();
            String URL = this.uploadFile(file, fileName, contentType, branch);
            file.delete();
            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
