package ru.belyaeva.rsoi.web.model;

import lombok.Data;

/**
 * Created by user on 16.11.2016.
 */
@Data
public class User {

    private String username;
    private String password;
    private String email;
    private String mobile;
    private String firstNameReal;
    private String lastNameReal;
    private Boolean isCourier;
    private String Description;

    private Long requestId;
    private String clientId;
    private String clientSecret;


}