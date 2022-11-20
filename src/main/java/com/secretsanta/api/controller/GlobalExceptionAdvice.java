package com.secretsanta.api.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {
   
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e) {
       
       log.error("GlobalExceptionAdvice", e);
       
       return "error-page";
     }

}