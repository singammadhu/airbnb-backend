package com.codingshuttle.projects.airBnbApp.service;

import com.codingshuttle.projects.airBnbApp.dto.BookingDto;
import com.codingshuttle.projects.airBnbApp.dto.BookingRequest;

public interface BookingService {
    //input->BookingRequest and output->BookingDto
    BookingDto initialiseBooking(BookingRequest bookingRequest);
}
