package com.secretsanta.api.dto;

import lombok.Getter;

@Getter
public class EmailRequest {
    private Addressee addressee;
    private String message;

    public EmailRequest(Addressee addressee, String message) {
        this.addressee = addressee;
        this.message = message;
    }
    
    public enum Addressee {
        RECIPIENT,
        SANTA
    }
}
