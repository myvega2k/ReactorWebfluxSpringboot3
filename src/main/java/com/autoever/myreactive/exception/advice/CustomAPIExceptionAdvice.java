package com.autoever.myreactive.exception.advice;

import com.autoever.myreactive.exception.CustomAPIException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomAPIExceptionAdvice {
//    @ExceptionHandler(CustomAPIException.class)
//    public ResponseEntity<?> handleCustomAPIException(CustomAPIException customAPIException){
//        Map<String, Object> errorMap = new HashMap<>();
//        errorMap.put("error message", customAPIException.getMessage());
//        errorMap.put("status", customAPIException.getHttpStatus().value());
//        //return ResponseEntity.ok(errorMap);
//        return new ResponseEntity<>(errorMap, customAPIException.getHttpStatus());
//    }
    @ExceptionHandler(CustomAPIException.class)
    public ProblemDetail handlePostNotFoundException(CustomAPIException e) throws Exception{
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Customer Not Found");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(new URI("http://localhost:8080/problems/customer-not-found"));
        problemDetail.setProperty("errorCategory", "Generic");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}