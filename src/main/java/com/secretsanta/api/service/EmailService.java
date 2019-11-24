package com.secretsanta.api.service;

import org.thymeleaf.context.Context;

public interface EmailService {

    public void sendEmail(String destinationAddress, String subject, String template, Context context);

}
