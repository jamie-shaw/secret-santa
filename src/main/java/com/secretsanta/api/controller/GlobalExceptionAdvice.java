package com.secretsanta.api.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionAdvice {
   
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e) {
       return "error-page";
     }

}