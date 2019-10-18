package com.secretsanta.api.model;

import lombok.Data;

@Data
public class PasswordChangeForm {

    private String password;
    private String confirmPassword;
    
}
