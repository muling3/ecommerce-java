package com.muling3.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AppExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public Map<String, String> catchProductBoundException(ProductNotFoundException ex){
        Map<String, String> errorsResponse = new HashMap<>();
        errorsResponse.put("status", "FAILED");
        errorsResponse.put("error", ex.getMessage());

        return errorsResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomerNotFoundException.class)
    public Map<String, String> catchCustomerBoundException(CustomerNotFoundException ex){

        System.out.println("Inside CustomerNotFoundException");

        Map<String, String> errorsResponse = new HashMap<>();
        errorsResponse.put("status", "FAILED");
        errorsResponse.put("error", ex.getMessage());

        return errorsResponse;
    }

}
