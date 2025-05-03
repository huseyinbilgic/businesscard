package com.algofusion.businesscard.config;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.algofusion.businesscard.entities.User;
import com.algofusion.businesscard.enums.Role;
import com.algofusion.businesscard.repositories.UserRepository;
import com.algofusion.businesscard.services.security.JwtUtil;
import com.algofusion.businesscard.services.security.RedisTokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;

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

        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .email(email)
                    .username(email)
                    .role(Role.CUSTOMER)
                    .refreshToken(jwtUtil.generateRefreshToken())
                    .refreshTokenExpiresAt(Instant.now().plus(expiration, ChronoUnit.DAYS))
                    .build());
        }

        String jwtToken = jwtUtil.generateTokenForUserName(Map.of(), email);
        redisTokenService.storeToken(email, jwtToken);

        HttpSession session = request.getSession(false);
        String redirectUri = (session != null) ? (String) session.getAttribute("redirectUri") : null;

        if (redirectUri == null) {
            redirectUri = "http://localhost:3000/login";
        }

        response.sendRedirect(redirectUri + "?jwtToken=" + jwtToken);
    }
}