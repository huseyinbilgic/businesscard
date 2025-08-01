package com.algofusion.businesscard.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.helper.CookieHelper;
import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.algofusion.businesscard.responses.JwtTokenResponse;
import com.algofusion.businesscard.services.AuthService;
import com.algofusion.businesscard.services.security.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Value("${refresh-token.expiration}")
    long expiration;

    @PostMapping("signup")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody RegisterUserRequest registerUserRequest) {
        authService.signup(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserRequest loginUserRequest,
            HttpServletResponse response) {
        JwtTokenResponse login = authService.login(loginUserRequest);

        ResponseCookie refreshCookie = CookieHelper.generateCookie("refreshToken", login.getRefreshToken(), expiration);

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ResponseEntity.ok(login.getJwtToken());
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader,
            HttpServletResponse response) {
        authService.logout(authorizationHeader.substring(7));

        ResponseCookie refreshCookie = CookieHelper.generateCookie("refreshToken", "", 0);

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ResponseEntity.ok("Log out completed now");
    }

    @PostMapping("refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found");
        }

        User userFromRefreshToken = jwtUtil.extractUsernameFromRefreshToken(refreshToken);
        if (!jwtUtil.validateRefreshTokenWithDate(userFromRefreshToken.getRefreshTokenExpiresAt())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String token = authService.generateTokenForUsername(userFromRefreshToken.getUsername());

        return ResponseEntity.ok(token);
    }

}
