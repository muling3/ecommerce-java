package com.muling3.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
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
        Map<String, String> errorsResponse = new HashMap<>();
        errorsResponse.put("status", "FAILED");
        errorsResponse.put("error", ex.getMessage());

        return errorsResponse;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public Map<String, String> catchUsernameNotFoundException(UsernameNotFoundException ex){
        Map<String, String> errorsResponse = new HashMap<>();
        errorsResponse.put("status", "FAILED");
        errorsResponse.put("error", ex.getMessage());

        return errorsResponse;
    }
    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleAuthenticationException(Exception ex) {
        Map<String, String> errorsResponse = new HashMap<>();
        errorsResponse.put("status", "FAILED");
        errorsResponse.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorsResponse);
    }
}
