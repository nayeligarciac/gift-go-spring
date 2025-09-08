package com.giftandgo.controller;

import com.giftandgo.error.BadRequestException;
import com.giftandgo.error.ErrorInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlingController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestException.class
    })
    @ResponseBody
    ErrorInfo handleBadRequest( Exception ex) {
        return new ErrorInfo(HttpStatus.BAD_REQUEST, ex);
    }
}
