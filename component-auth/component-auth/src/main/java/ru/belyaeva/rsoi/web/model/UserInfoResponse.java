package ru.belyaeva.rsoi.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by user on 13.12.2016.
 */
@Data
public class UserInfoResponse {

    @JsonProperty("display_name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("isCourier")
    private Boolean isCourier;

    @JsonProperty("description")
    private String description;

    @JsonProperty("firstName")
    private String firstname;

    @JsonProperty("lastName")
    private String lastname;

}
