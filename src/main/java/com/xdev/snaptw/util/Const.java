package com.xdev.snaptw.util;

public class Const {
    public static final String BASE_URL = "/snaptw/api/v1";
    public static final long TOKEN_VALIDITY = 5*60*60*1000;
    public static final String SIGNING_KEY = "214125442A472D4B6150645367566B58703273357638792F423F4528482B4D6251655468576D5A7133743677397A24432646294A404E635266556A586E327235";
    public static final String [] ADMIN_ENDPOINTS = {
        BASE_URL + "/users",
        BASE_URL + "/users/",
        BASE_URL + "/users/username/*",
        BASE_URL + "/users/email/*",
        BASE_URL + "/users/id/*"
    };
    public static final String [] ENDPOINTS_WHITE_LIST = {
        BASE_URL + "/auth/**"
    };
}
