package ru.belyaeva.rsoi.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by user on 13.12.2016.
 */
@Data
public class UserInfo {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("redirect_uri")
    private String redirectUrl;


}
