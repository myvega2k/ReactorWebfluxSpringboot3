package com.autoever.myreactive.exception.advice;

import com.autoever.myreactive.exception.CustomAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomAPIExceptionAdvice {

    @ExceptionHandler(CustomAPIException.class)
    public ResponseEntity<?> handleCustomAPIException(CustomAPIException customAPIException){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("error message", customAPIException.getMessage());
        errorMap.put("status", customAPIException.getHttpStatus().value());
        //return ResponseEntity.ok(errorMap);
        return new ResponseEntity<>(errorMap, customAPIException.getHttpStatus());
    }
}