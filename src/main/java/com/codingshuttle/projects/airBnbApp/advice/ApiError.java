package com.codingshuttle.projects.airBnbApp.advice;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data//getter + setter + toString + equals + hashCode
@Builder//why is this used
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> subError;
}
//In Lombok, @Builder creates objects using the Builder Pattern instead of constructors.
//
//Without @Builder:
//
//ApiError error = new ApiError();
//error.setStatus(HttpStatus.BAD_REQUEST);
//error.setMessage("Invalid input");
//error.setSubError(List.of("Email is required"));
//
//A lot of setter calls.
//
//With @Builder
//ApiError error = ApiError.builder()
//        .status(HttpStatus.BAD_REQUEST)
//        .message("Invalid input")
//        .subError(List.of("Email is required"))
//        .build();
//
//Cleaner.
//