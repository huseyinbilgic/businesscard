package com.algofusion.businesscard.helper;

import org.springframework.http.ResponseCookie;

public class CookieHelper {
    public static ResponseCookie generateCookie(String name, String value, long day) {
        return ResponseCookie.from(name,
                value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(day * 24 * 60 * 60)
                .sameSite("None")
                .build();
    }
}
