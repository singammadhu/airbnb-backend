package com.codingshuttle.projects.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


//Step 1 — Create HotelInfoDto
@Data
@AllArgsConstructor
public class HotelInfoDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}

/*
Why do we need this DTO?

Your existing HotelDto gives basic hotel information:

HotelDto
 ├── id
 ├── name
 ├── city
 ├── amenities
 ├── photos
 └── contactInfo

But when a user clicks a hotel, we want:

Hotel Information
       +
Available Room Types

So instructor creates:

HotelInfoDto
 ├── hotel
 └── rooms
*/
