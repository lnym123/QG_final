package com.controller.servlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidationHelper {

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberPattern = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(phoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean isValidLocation(String location) {
        String locationPattern = "^[\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(locationPattern);
        Matcher matcher = pattern.matcher(location);
        return matcher.matches();
    }

    public static boolean isValidUsername(String Username) {
        String locationPattern = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(locationPattern);
        Matcher matcher = pattern.matcher(Username);
        return matcher.matches();
    }




}



