package ru.belyaeva.rsoi.web.model;

import lombok.Data;

/**
 * Created by user on 13.12.2016.
 */
@Data
public class CheckTokenResponse {

    private Boolean validToken;
    private Long userId;
}
