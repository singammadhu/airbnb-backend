package com.codingshuttle.projects.airBnbApp.dto;

import com.codingshuttle.projects.airBnbApp.entity.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
//BookingDto is output:
public class BookingDto {
    private Long id;
    private HotelDto hotel;
    private RoomDto room;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus bookingStatus;
}
