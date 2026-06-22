package com.codingshuttle.projects.airBnbApp.advice;

import com.codingshuttle.projects.airBnbApp.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
// Global class for handling exceptions from all controllers
//
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
//
// @ControllerAdvice -> Creates a global class connected to all controllers
//                      (can handle exceptions OR intercept requests/responses)
//
// Here, since we use @ExceptionHandler,
// we can simply say:
// @ControllerAdvice -> handles exceptions globally
//
// @ResponseBody -> Converts Java object to JSON automatically

public class GlobalExceptionHandler {

    // Common method to avoid repeating same ResponseEntity creation logic
    // Takes ApiError -> wraps it inside ApiResponse
    // -> sends response with correct HTTP status

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError){

        return new ResponseEntity<>(
                new ApiResponse<>(apiError), // put error inside wrapper
                apiError.getStatus()         // send HTTP status code
        );
    }


    // Handles custom exception when requested resource is not found
    // Example: Hotel/User/Booking ID does not exist

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exception){

        // Create error object with status + exception message

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)      // 404
                .message(exception.getMessage())   // actual error message
                .build();

        return buildErrorResponseEntity(apiError);
    }


    // Handles any unexpected exception not handled above
    // Acts as fallback exception handler

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception){

        // Create internal server error object

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .message(exception.getMessage())
                .build();

        return buildErrorResponseEntity(apiError);
    }
}


/*
================ WHY GLOBAL EXCEPTION HANDLER =================

Without it:

Every controller method needs try-catch block

Example:

try{
   // logic
}catch(Exception e){
   // handle error
}

With GlobalExceptionHandler:

One central place handles all exceptions

Benefits:

-> Cleaner controller code
-> Reusability
-> Same error format for entire application

*/


/*
================ API RESPONSE FLOW =================

1) ApiError -> Stores error details
   Used only when exception happens

Example:

ApiError {
   status  = 404
   message = "Hotel not found"
}



2) ApiResponse<T> -> Standard wrapper for every API response

Wraps both SUCCESS + ERROR in same format


Success:

new ApiResponse(user)

{
   "timeStamp": "...",
   "data": {
      "id": 1,
      "name": "Madhu"
   },
   "error": null
}


Error:

new ApiResponse(apiError)

{
   "timeStamp": "...",
   "data": null,
   "error": {
      "status": "NOT_FOUND",
      "message": "Hotel not found"
   }
}



3) ResponseEntity -> Sends response to client

Controls:

-> Response Body
-> HTTP Status Code
-> Headers (optional)


Example:

return new ResponseEntity<>(
      new ApiResponse(apiError),
      HttpStatus.NOT_FOUND
);

*/


/*
================ WHY ApiResponse<?> =================

ApiResponse<T> is generic.

T can be anything.

Example:

ApiResponse<User>
ApiResponse<Hotel>
ApiResponse<String>


But in GlobalExceptionHandler,
we don't know what type T will be.

So we use wildcard:

ApiResponse<?>


? = unknown type / any type


Example possible responses:

ApiResponse<User>
ApiResponse<Hotel>
ApiResponse<String>

All can be returned.

*/


/*
================ EASY ANALOGY =================

ApiError

= Information about what went wrong


ApiResponse

= Standard wrapper that holds
  either success data OR error


ResponseEntity

= Sends wrapped response
  along with HTTP status code

*/


/*
================ COMPLETE FLOW =================

Service/Controller throws exception
                ↓

GlobalExceptionHandler catches exception
                ↓

Create ApiError object
                ↓

Wrap inside ApiResponse
                ↓

ResponseEntity sends JSON + HTTP status

*/


/*
================ INTERVIEW ANSWER =================

ApiError
-> Stores error information


ApiResponse
-> Standardizes success/error response
   in one common format


ResponseEntity
-> Sends response body
   + HTTP status code


GlobalExceptionHandler
-> Central place to handle exceptions
   for all controllers

*/