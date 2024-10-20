package com.distributed.miraeasset.utils;

import org.springframework.mail.SimpleMailMessage;

import java.util.Random;

public class MailUtil {
    public static SimpleMailMessage buildContentMail(String mailTo, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailTo);
        message.setSubject(subject);
        message.setText(text);
        return  message;
    }
    public static String generateOTP(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
