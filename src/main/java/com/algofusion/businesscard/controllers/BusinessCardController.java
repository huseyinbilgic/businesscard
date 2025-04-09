package com.algofusion.businesscard.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algofusion.businesscard.requests.BusinessCardRequest;
import com.algofusion.businesscard.responses.BusinessCardResponse;
import com.algofusion.businesscard.services.BusinessCardService;
import com.algofusion.businesscard.services.security.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/business-cards/")
@RequiredArgsConstructor
public class BusinessCardController {

    private final JwtUtil jwtUtil;
    private final BusinessCardService businessCardService;

    @GetMapping
    public ResponseEntity<List<BusinessCardResponse>> fetchAllBusinesCardsByUsername(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(businessCardService.fetchAllBusinesCardsByUsername(username));
    }

    @PostMapping
    public ResponseEntity<BusinessCardResponse> saveNewBusinessCardByUsername(
            @Valid @RequestBody BusinessCardRequest businessCardRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(businessCardService.saveNewBusinessCardByUsername(username, businessCardRequest));
    }

    @PatchMapping("{id}")
    public ResponseEntity<BusinessCardResponse> updateBusinessCard(
            @PathVariable Long id,
            @Valid @RequestBody BusinessCardRequest businessCardRequest,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(businessCardService.updateBusinessCard(username, id, businessCardRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBusinessCardById(@PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(businessCardService.deleteBusinessCardById(username, id));
    }
}
