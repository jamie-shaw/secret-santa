package com.secretsanta.api.controller;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.secretsanta.api.dao.RecipientDao;
import com.secretsanta.api.model.Recipient;
import com.secretsanta.api.model.RequestContext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Recipients", description = "Endpoints for managing Secret Santa recipients and pick history")
@SecurityRequirement(name = "bearerAuth")
public class RecipientController {
    
    @Resource
    private RecipientDao recipientDao;
    
    @Operation(
        summary = "Get current user's recipient",
        description = "Retrieves the Secret Santa recipient assigned to the currently user"
    )
    @GetMapping("/recipient")
    public Recipient getRecipient() {
        return recipientDao.getRecipientForCurrentUser(RequestContext.getUsername());
    }
    
    @Operation(
        summary = "Get pick status",
        description = "Retrieves the list of all recipients and their pick status for the current year"
    )
    @GetMapping("/pick/status")
    public List<Recipient> showPickStatus(Model model) {
        return recipientDao.getAllRecipients();
    }
    
    @Operation(
        summary = "Get available years",
        description = "Retrieves the list of years for which historical pick data is available"
    )
    @GetMapping("/history/years")
    public List<String> getAvailableYears() {
        return recipientDao.getActiveYears();
    }
    
    @Operation(
        summary = "Get pick history for a year",
        description = "Retrieves the complete pick history for a specific year"
    )
    @GetMapping("/history/{selectedYear}")
    public List<Recipient> getPickHistory(
            @Parameter(description = "The year to retrieve pick history for", required = true, example = "2026")
            @PathVariable Integer selectedYear) {
        return recipientDao.getAllRecipientsForSelectedYear(selectedYear);
    }
}
