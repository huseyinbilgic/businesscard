package com.algofusion.businesscard.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String redirectUri = request.getParameter("redirectUri");

        if (redirectUri == null || redirectUri.isBlank()) {
            redirectUri = "/";
        }

        String errorMessage = URLEncoder.encode("Login failed: " + exception.getMessage(), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUri + "?error=" + errorMessage);
    }

}
