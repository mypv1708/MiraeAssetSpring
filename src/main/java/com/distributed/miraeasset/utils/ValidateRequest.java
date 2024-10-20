package com.distributed.miraeasset.utils;

import java.util.regex.Pattern;

public class ValidateRequest {
    private static String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static String NUMBER_PATTERN = "^0\\d{9}$";
    private static String PASSWORD_PATTERN = "^[\\S]{8,}$";
//    chứa ít nhất một chữ cái viết hoa, một chữ cái thường, một chữ số và một ký tự đặc biệt
//    private static String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";



    public static Boolean validateEmail(String email) {
        if (Pattern.matches(EMAIL_PATTERN, email)) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean validatePhone(String phone) {
        if (Pattern.matches(NUMBER_PATTERN, phone)) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean validatePassword(String password) {
        if (Pattern.matches(PASSWORD_PATTERN, password)) {
            return true;
        } else {
            return false;
        }
    }


    public static Boolean isNotEmpty(String path) {
        return !path.isEmpty();
    }
}
