package com.secretsanta.api.service;

public class StubEmailService implements EmailService{
    
    @Override
    public void sendEmail(String address, String subject, String message) {
        
        System.out.println(address + ", " + subject);
        
    }

}
