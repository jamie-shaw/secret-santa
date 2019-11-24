package com.secretsanta.api.service;

public interface EmailService {

    public void sendEmail(String destinationAddress, String subject, String message);

}
