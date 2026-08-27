package com.codingshuttle.projects.airBnbApp.service;

import com.codingshuttle.projects.airBnbApp.dto.HotelDto;
import com.codingshuttle.projects.airBnbApp.dto.HotelSearchRequest;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventoriesByRoom(Room room);
    void deleteFutureInventoriesByHotel(Hotel hotel);
    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);

}
