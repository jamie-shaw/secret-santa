package com.secretsanta.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.secretsanta.api.model.SystemContext;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseEmailService {

    @Value("${email.destinationAddressOverride}")
    private String destinationAddressOverride;
    
    @Autowired
    SystemContext systemContext;
    
    @Autowired
    private SpringTemplateEngine templateEngine;
    
    String getDestinationAddress(String destinationAddress) {
        if (StringUtils.isNotBlank(destinationAddressOverride)) {
            log.debug("Overriding destinationAddress: " + destinationAddressOverride);
            return destinationAddressOverride;
        } else {
            log.debug("Not overriding destinationAddress: " + destinationAddressOverride);
            return destinationAddress;
        }
    }
    
    String buildEmailBody(String templateName, Context templateContext) {
        
        templateContext.setVariable("applicationUrl", systemContext.getApplicationUrl());
        
        return templateEngine.process(templateName, templateContext);
    }
}
