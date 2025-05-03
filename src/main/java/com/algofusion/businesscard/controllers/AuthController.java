package com.algofusion.businesscard.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.requests.LoginUserRequest;
import com.algofusion.businesscard.responses.JwtTokenResponse;
import com.algofusion.businesscard.services.AuthService;
import com.algofusion.businesscard.services.security.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @PostMapping("login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserRequest loginUserRequest,
            HttpServletResponse response) {
        JwtTokenResponse login = authService.login(loginUserRequest);

        Cookie refreshCookie = new Cookie("refreshToken", login.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (expiration * 24 * 60 * 60));

        response.addCookie(refreshCookie);
        return ResponseEntity.ok(login.getJwtToken());
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader.substring(7));
        return ResponseEntity.ok("Log out completed now");
    }

    @PostMapping("refresh")
    public ResponseEntity<String> postMethodName(HttpServletRequest request, HttpServletResponse response) {
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
        ;
        return ResponseEntity.ok(token);
    }

}
