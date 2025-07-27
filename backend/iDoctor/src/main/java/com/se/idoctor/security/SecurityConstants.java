package com.se.idoctor.security;

import io.github.cdimascio.dotenv.Dotenv;

public class SecurityConstants {
    private static final Dotenv dotenv = Dotenv.load();
    public static final String SECRET_KEY = dotenv.get("JWT_SECRET");
    public static final int TOKEN_EXPIRATION = 7200000; // 7200000 milliseconds = 7200 seconds = 2 hours.
    public static final String BEARER = "Bearer "; // Authorization : "Bearer " + Token
    public static final String AUTHORIZATION = "Authorization"; // "Authorization" : Bearer Token
    public static final String REGISTER_PATH = "/api/userx/register"; // Public path that clients can use to register.

    private SecurityConstants() {}
}
