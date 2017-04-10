package ru.belyaeva.rsoi.web.model;

import lombok.Data;

/**
 * Created by user on 13.12.2016.
 */
@Data
public class CheckTokenRequest {

    private String token;
    public CheckTokenRequest(String token){
        this.token = token;
    }
}
