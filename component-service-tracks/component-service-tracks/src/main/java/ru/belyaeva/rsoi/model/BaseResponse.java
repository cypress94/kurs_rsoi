package ru.belyaeva.rsoi.model;

import lombok.Data;

/**
 * Created by user on 18.12.2016.
 */
@Data
public class BaseResponse {
    public static String RESPONSE_OK = "OK";
    protected Boolean errorCode;
    protected String errorMessage;

    public BaseResponse(Boolean a, String msg) {errorCode = a; errorMessage = msg;};

}