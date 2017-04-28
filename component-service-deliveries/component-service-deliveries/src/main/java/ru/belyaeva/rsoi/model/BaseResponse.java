package ru.belyaeva.rsoi.model;

import lombok.Data;

/**
 * Created by user on 18.12.2016.
 */
@Data
public class BaseResponse {
    protected Boolean errorCode;
    protected String errorMessage;

}
