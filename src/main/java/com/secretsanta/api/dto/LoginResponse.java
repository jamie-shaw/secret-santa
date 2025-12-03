package com.secretsanta.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String displayName;
    private String email;
    
    public LoginResponse(String token, String username, String displayName, String email) {
        this.token = token;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
    }
}
