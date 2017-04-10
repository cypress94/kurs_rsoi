package ru.belyaeva.rsoi.model;

import lombok.Data;

@Data
public class OAuth2Bean {
    private String authEndpoint;
    private String tokenEndpoint;
    private String redirectEndpoint;
    private String clientId;
    private String clientSecret;
    private String responseType;
    private String scope;
    private String accessType;
    private String accessToken;
    private String profileEndpoint;
}
