package com.secretsanta.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Gift {

    private String id;
    private String userName;
    private String description;
    private String link;
    private String year;

}