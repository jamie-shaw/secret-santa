package com.secretsanta.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Recipient {

    private String userName;
    private String year;
    private String recipient;
    private boolean assigned;

}
