package com.secretsanta.api.controller;

import com.secretsanta.api.SystemContextHolder;

public class BaseController {

    public String getSchema() {
        return SystemContextHolder.getSchema();
    }
}
