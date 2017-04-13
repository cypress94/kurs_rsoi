package ru.belyaeva.rsoi.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.belyaeva.rsoi.model.BaseResponse;

import javax.persistence.EntityNotFoundException;


@RestControllerAdvice(annotations = RestController.class)
public class ExceptionController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public BaseResponse notFound(EntityNotFoundException exception) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrorCode(false);
        baseResponse.setErrorMessage(exception.getMessage());
        return baseResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public BaseResponse exception(IllegalArgumentException exception) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrorCode(false);
        baseResponse.setErrorMessage(exception.getMessage());
        return baseResponse;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(IllegalAccessException.class)
    public BaseResponse exception(IllegalAccessException exception) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrorCode(false);
        baseResponse.setErrorMessage(exception.getMessage());
        return baseResponse;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public BaseResponse exception(Exception exception)
    {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setErrorCode(false);
        baseResponse.setErrorMessage(exception.getMessage());
        return baseResponse;
    }





}
