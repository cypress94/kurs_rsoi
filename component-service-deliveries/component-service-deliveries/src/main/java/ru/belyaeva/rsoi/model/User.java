package ru.belyaeva.rsoi.model;

import lombok.Data;

/**
 * Created by user on 16.11.2016.
 */
@Data
public class User {

    private String username;
    private String password;
    private String email;
    private String realFirstName;
    private String realLastName;
    private String mobile;
    private Long requestId;
}