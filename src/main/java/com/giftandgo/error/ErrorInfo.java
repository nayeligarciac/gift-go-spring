package com.giftandgo.error;

import org.springframework.http.HttpStatus;

public class ErrorInfo {
    public final HttpStatus status;
    public final int error;
    public final String message;

    public ErrorInfo(HttpStatus status, Exception ex) {
        this.status = status;
        this.error = status.value();
        this.message = ex.getLocalizedMessage();
    }
}
