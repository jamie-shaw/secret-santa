package com.secretsanta.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to provide convenient access to the Angular application
 */
@Controller
public class AngularController {

    /**
     * Redirect /ng to the Angular app at /app/
     * The actual Angular app is served via static resources from /app/
     */
    @GetMapping(value = "/ng")
    public String angularApp() {
        return "redirect:/app/";
    }
}
