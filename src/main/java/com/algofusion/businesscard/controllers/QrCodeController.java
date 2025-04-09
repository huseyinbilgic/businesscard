package com.algofusion.businesscard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.algofusion.businesscard.services.QrCodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/qr-code/")
@RequiredArgsConstructor
public class QrCodeController {
    private final QrCodeService qrCodeService;

    @GetMapping("generate")
    public ResponseEntity<String> generateQrCodeForBusinessCard(@RequestParam String text) {
        try {
            return ResponseEntity
                    .ok("data:image/png;base64," + qrCodeService.generateQRCodeBase64(text));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("QR code generation failed");
        }
    }
}
