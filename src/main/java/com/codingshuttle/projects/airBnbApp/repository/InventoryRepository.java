package com.codingshuttle.projects.airBnbApp.repository;

import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
//    void deleteByDateAfterAndRoom(LocalDate date, Room room);
    void deleteByDateGreaterThanEqualAndRoom(LocalDate date,Room room);
    void deleteByDateGreaterThanEqualAndHotel(LocalDate date, Hotel hotel);
}

