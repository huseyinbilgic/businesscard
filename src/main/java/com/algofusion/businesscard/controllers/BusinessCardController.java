package com.algofusion.businesscard.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algofusion.businesscard.requests.BusinessCardRequest;
import com.algofusion.businesscard.responses.BusinessCardResponse;
import com.algofusion.businesscard.services.BusinessCardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/business-cards/")
@RequiredArgsConstructor
public class BusinessCardController {

    private final BusinessCardService businessCardService;

    @GetMapping
    public ResponseEntity<List<BusinessCardResponse>> fetchAllBusinesCardsByUsername(
            // @RequestHeader("Authorization") String authorizationHeader
            @AuthenticationPrincipal UserDetails userDetails) {
        // String token = authorizationHeader.substring(7);
        // String username = jwtUtil.extractUsername(token);
        String username = userDetails.getUsername();
        return ResponseEntity.ok(businessCardService.fetchAllBusinesCardsByUsername(username));
    }

    @GetMapping("{id}")
    public ResponseEntity<BusinessCardResponse> fetchBusinesCardById(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(businessCardService.fetchBusinessCardById(username, id));
    }

    @PostMapping
    public ResponseEntity<BusinessCardResponse> saveNewBusinessCardByUsername(
            @Valid @RequestBody BusinessCardRequest businessCardRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(businessCardService.saveNewBusinessCardByUsername(username, businessCardRequest));
    }

    @PatchMapping("{id}")
    public ResponseEntity<BusinessCardResponse> updateBusinessCard(
            @PathVariable Long id,
            @Valid @RequestBody BusinessCardRequest businessCardRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(businessCardService.updateBusinessCard(username, id, businessCardRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBusinessCardById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(businessCardService.deleteBusinessCardById(username, id));
    }
}
