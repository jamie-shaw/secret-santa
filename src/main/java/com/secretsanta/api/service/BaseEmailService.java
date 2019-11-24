package com.secretsanta.api.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.secretsanta.api.model.SessionContext;
import com.secretsanta.api.model.SystemContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseEmailService {

    @Value("${email.destinationAddressOverride}")
    private String destinationAddressOverride;
    
    @Autowired
    private SystemContext systemContext;
    
    @Autowired
    private SessionContext sessionContext;
    
    @Autowired
    private SpringTemplateEngine templateEngine;
    
    String getFinalDestinationAddress(String destinationAddress) {
        if (StringUtils.isNotBlank(destinationAddressOverride)) {
            log.debug("Overriding destinationAddress: " + destinationAddressOverride);
            return destinationAddressOverride;
        } else {
            log.debug("Not overriding destinationAddress: " + destinationAddressOverride);
            return destinationAddress;
        }
    }
    
    String getFinalSubject(String subject) {
        return subject + " (" + StringUtils.capitalize(sessionContext.getSchema()) + " Edition)";
    }
    
    String buildEmailBody(String templateName, Context templateContext) {
        
        templateContext.setVariable("applicationUrl", systemContext.getApplicationUrl());
        
        return templateEngine.process(templateName, templateContext);
    }
}
