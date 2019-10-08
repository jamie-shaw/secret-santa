package com.secretsanta.api.model;

import lombok.Data;

@Data
public class Recipient {

    private String userName;
    private String year;
    private String recipient;
    private boolean assigned;

}
