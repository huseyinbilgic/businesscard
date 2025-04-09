package com.algofusion.businesscard.errors;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorResponse {
    private HttpStatus httpStatus;

    private String message;

    private long timeStamp;

    public ErrorResponse() {
    }

    public ErrorResponse(HttpStatus httpStatus, String message, long timeStamp) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
