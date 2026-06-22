package com.codingshuttle.projects.airBnbApp.advice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

 import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {
    private LocalDateTime timeStamp;
    private T data;        // success payload
    private ApiError error; // error payload

    //Why ApiResponse wrapper?
    //Answer To maintain a consistent response structure across all APIs so frontend can handle success and error uniformly.
    public ApiResponse(){

        this.timeStamp=LocalDateTime.now();
    }
    public ApiResponse(T data){
        this();
        this.data=data;
    }
    public ApiResponse(ApiError error){
        this();
        this.error=error;
    }

}
/*
WHY NOT @NoArgsConstructor ?
--------------------------------
@NoArgsConstructor creates:

    public ApiResponse(){}

But we want:

    this.timeStamp = LocalDateTime.now();

every time object is created.

So manual constructor needed.


WHY NOT @RequiredArgsConstructor ?
--------------------------------
@RequiredArgsConstructor works only for:

1. final fields
2. @NonNull fields

Example:

    final String name;

creates:

    ApiResponse(String name)

But here:

    LocalDateTime timeStamp;
    T data;
    ApiError error;

No final / @NonNull fields.

So not useful here.


WHY 3 CONSTRUCTORS ?
--------------------------------

1. Success Response

    new ApiResponse(data)

returns:

    {
      timeStamp,
      data,
      error=null
    }


2. Error Response

    new ApiResponse(error)

returns:

    {
      timeStamp,
      data=null,
      error
    }


3. Base Constructor

    ApiResponse()

Used to initialize common field:

    timeStamp = LocalDateTime.now()


WHAT DOES this() DO ?
--------------------------------

this() = call constructor of same class

Example:

    new ApiResponse(data)

internally:

    Step 1 -> ApiResponse()
             timeStamp = now

    Step 2 -> data = value


WHY USE this() ?
--------------------------------

Avoid repeating code (DRY Principle)

Without this():

    constructor1 -> set timestamp
    constructor2 -> set timestamp

Repeated code.

With this():

    write common logic once.


INTERVIEW ANSWER
--------------------------------

Created overloaded constructors
to handle success/error responses.

Used constructor chaining (this())
to initialize common fields once
and avoid code duplication.
*/


/*
 ApiResponse<T> is a generic wrapper class.
 T = success payload (can be any type)
 Examples:
 ApiResponse<User>
 ApiResponse<Hotel>
 ApiResponse<String>

 Structure:
 data  -> stores SUCCESS response
 error -> stores FAILURE response

 Rule:
 Success -> data filled, error = null
 Failure -> error filled, data = null

 Note:
 ApiResponse<ApiError> is technically possible,
 but not used here because ApiError already has
 its own separate error field.

 Goal:
 Keep same JSON structure for every API response.
*/
