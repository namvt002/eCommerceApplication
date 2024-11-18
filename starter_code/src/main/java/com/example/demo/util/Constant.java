package com.example.demo.util;

public class Constant {
    // Change SECRET to a more secure value
    public static final String SECRET = "myNewSuperSecretKey123!";

    // Change EXPIRATION_TIME to a different value, e.g., 1 day (24 hours)
    public static final long EXPIRATION_TIME = 864_000_00;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/user/create";
    public static final String SIGN_IN_URL = "/login";
}
