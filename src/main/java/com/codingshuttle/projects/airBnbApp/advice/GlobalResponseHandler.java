package com.codingshuttle.projects.airBnbApp.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
//
// @ControllerAdvice -> Creates global class connected to all controllers
//                      (can handle exceptions OR intercept request/response)
//
// Here we intercept responses globally
//
// @ResponseBody -> Converts Java object to JSON automatically

public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {


    // Decides whether beforeBodyWrite() should run or not
    // return true = intercept every controller response

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        return true; // apply for all controllers
    }


    // Runs BEFORE response goes to client
    // Used to wrap normal success response inside ApiResponse

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {


        // Some routes should NOT be wrapped
        // Example: Swagger docs / actuator endpoints

        List<String> allowedRoutes = List.of(
                "/v3/api-docs",
                "/actuator"
        );


        // Check if current request belongs to allowed routes

        boolean isAllowed = allowedRoutes.stream()
                .anyMatch(route ->
                        request.getURI().getPath().contains(route)
                );


        // If response is already ApiResponse OR route is allowed
        // do nothing, return original response

        if (body instanceof ApiResponse<?> || isAllowed) {
            return body;
        }


        // Wrap normal controller response inside ApiResponse

        return new ApiResponse<>(body);
    }
}



/*
================ WHY GLOBAL RESPONSE HANDLER =================

Without it:

Every controller method needs manual wrapping


Example:

return new ApiResponse(user);



With GlobalResponseHandler:

Controller can simply return:

return user;


GlobalResponseHandler automatically converts:

user

into

{
   "timeStamp": "...",
   "data": user,
   "error": null
}


Benefits:

-> Cleaner controller code
-> Standard response format everywhere
-> No need to manually wrap every response

*/



/*
================ WHY implements ResponseBodyAdvice<Object> =================

ResponseBodyAdvice is Spring interface.

It allows us to intercept response
BEFORE sending it to client.


Flow:

Controller returns object
        ↓
beforeBodyWrite() runs
        ↓
Modify response if needed
        ↓
Send final response to client


We use:

implements ResponseBodyAdvice<Object>

because we want to intercept
ALL types of responses


Example possible responses:

User
Hotel
String
List<Hotel>
Booking


Object = accept any response type

*/



/*
================ FLOW =================

Controller returns:

return hotel;


Spring checks:

Does any class implement ResponseBodyAdvice ?

        ↓

GlobalResponseHandler found

        ↓

beforeBodyWrite() executes

        ↓

Wrap response:

new ApiResponse<>(hotel)

        ↓

Send JSON to client


Final JSON:

{
   "timeStamp": "...",
   "data": {
      "id": 1,
      "name": "Taj Hotel"
   },
   "error": null
}

*/



/*
================ DIFFERENCE =================

GlobalExceptionHandler

-> Handles ERROR responses

Example:

Hotel not found

Creates:

ApiError + ApiResponse




GlobalResponseHandler

-> Handles SUCCESS responses

Example:

return hotel;

Automatically converts to:

ApiResponse(hotel)



Both together make response format consistent

*/


/*
================ EASY ANALOGY =================

Controller

= Writes a letter


GlobalResponseHandler

= Puts success letter inside envelope


GlobalExceptionHandler

= Puts error letter inside envelope


ApiResponse

= Common envelope format


Client always receives same format

*/


/*
================ INTERVIEW ANSWER =================

ResponseBodyAdvice

-> Intercepts controller response before sending to client


GlobalResponseHandler

-> Automatically wraps all successful responses
   inside ApiResponse


Benefit:

-> Standard response format
-> Cleaner controller code
-> No need to manually wrap responses everywhere

*/