package com.algofusion.businesscard.config;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.errors.CustomException;
import com.algofusion.businesscard.helper.CookieHelper;
import com.algofusion.businesscard.requests.RegisterUserRequest;
import com.algofusion.businesscard.requests.UserUpdateForRefreshTokenRequest;
import com.algofusion.businesscard.services.UserService;
import com.algofusion.businesscard.services.security.JwtUtil;
import com.algofusion.businesscard.services.security.RedisTokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisTokenService redisTokenService;
    private final UserService userService;

    @Value("${refresh-token.expiration}")
    long expiration;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        String email = user.getAttribute("email");

        if (email == null) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }

        String refreshToken = generateRefreshToken(email);

        String jwtToken = jwtUtil.generateTokenForUserName(Map.of(), email);
        redisTokenService.storeToken(email, jwtToken);

        ResponseCookie refreshCookie = CookieHelper.generateCookie("refreshToken", refreshToken, expiration);

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        HttpSession session = request.getSession(false);
        String redirectUri = (session != null) ? (String) session.getAttribute("redirectUri") : null;

        if (redirectUri == null) {
            redirectUri = "http://localhost:3000/login";
        }

        response.sendRedirect(redirectUri + "?jwtToken=" + jwtToken);
    }

    private String generateRefreshToken(String email) {
        User byUsernameOrEmail = userService.findByUsernameOrEmail(email);
        String refreshToken = null;
        if (byUsernameOrEmail == null) {
            userService.saveUser(RegisterUserRequest.builder()
                    .email(email)
                    .username(email)
                    .build());

            User byUsername = userService.findByUsername(email);
            return byUsername.getRefreshToken();
        } else {
            if (!jwtUtil.validateRefreshTokenWithDate(byUsernameOrEmail.getRefreshTokenExpiresAt())) {
                refreshToken = jwtUtil.generateRefreshToken();
                userService.updateUserForRefreshToken(byUsernameOrEmail.getEmail(),
                        UserUpdateForRefreshTokenRequest.builder()
                                .refreshToken(refreshToken)
                                .refreshTokenExpiresAt(Instant.now().plus(expiration, ChronoUnit.DAYS))
                                .build());
                return refreshToken;
            }
        }
        throw new CustomException("Refresh token could not generate");
    }
}