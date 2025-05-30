package com.algofusion.businesscard.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.algofusion.businesscard.dto.PrivacyUser;
import com.algofusion.businesscard.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("search")
    public List<PrivacyUser> search(@RequestParam String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<PrivacyUser> searchUsers = userService.searchUsers(keyword).stream()
                .filter(p -> !p.username().equals(username))
                .toList();
        return searchUsers;
    }
}
