package com.giftandgo.error;

public class BlockedRequestException extends RuntimeException {

    public BlockedRequestException(String message) {
        super(message);
    }

    public BlockedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
