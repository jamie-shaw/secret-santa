package com.secretsanta.api.service;

import org.springframework.beans.factory.annotation.Value;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseEmailService {

    @Value("${email.destinationAddressOverride}")
    private String destinationAddressOverride;
    
    String getDestinationAddress(String destinationAddress) {
        if (StringUtils.isNotBlank(destinationAddressOverride)) {
            log.debug("Overriding destinationAddress: " + destinationAddressOverride);
            return destinationAddressOverride;
        } else {
            log.debug("Not overriding destinationAddress: " + destinationAddressOverride);
            return destinationAddress;
        }
    }
    
}
