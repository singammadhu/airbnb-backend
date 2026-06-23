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

//@RestControllerAdvice
// Global Response Interceptor
//
// Purpose:
// 1. Intercepts every controller response globally
// 2. Runs before sending response to client
// 3. Wraps normal response inside ApiResponse
//    so all APIs follow same response structure
//
// Example:
//
// Controller returns:
//      User
//
// Final response sent:
//      ApiResponse<User>
//
// Why use?
// Avoid manually wrapping response in every controller
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {


    // Decides whether beforeBodyWrite() should run
    //
    // return true  -> apply to all controller responses
    // return false -> skip interception

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        return true;
    }


    // Runs AFTER controller returns response
    // Runs BEFORE response goes to client
    //
    // Used to modify/wrap response globally

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {


        // Skip wrapping for routes like swagger/actuator

        List<String> allowedRoutes = List.of(
                "/v3/api-docs",
                "/actuator"
        );


        boolean isAllowed = allowedRoutes.stream()
                .anyMatch(route ->
                        request.getURI().getPath().contains(route)
                );


        // If already wrapped OR route excluded
        // return original response

        if (body instanceof ApiResponse<?> || isAllowed) {
            return body;
        }


        // Wrap normal response inside ApiResponse

        return new ApiResponse<>(body);
    }
}
/*
What minimum understanding should you keep?

You should be able to explain in 3 lines:

1. It intercepts every controller response globally.
2. beforeBodyWrite() runs before sending response to client.
3. It wraps normal responses inside ApiResponse for consistent API structure.

And know:

supports() → decides whether interception should happen.
return true → apply to all controllers.

That is enough.
 */


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