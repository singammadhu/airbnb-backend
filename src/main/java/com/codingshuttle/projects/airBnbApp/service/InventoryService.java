package com.codingshuttle.projects.airBnbApp.service;

import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Room;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventoriesByRoom(Room room);
    void deleteFutureInventoriesByHotel(Hotel hotel);

}
