package com.codingshuttle.projects.airBnbApp.service;

import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import com.codingshuttle.projects.airBnbApp.repository.InventoryRepository;
 import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImp implements InventoryService{

    private final InventoryRepository inventoryRepository;

    @Override
    // @Transactional // → if one insert fails, rollback all records
    public void initializeRoomForAYear(Room room) {
        LocalDate today=LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        List<Inventory> inventories= new ArrayList<>();
        for(;!today.isAfter(endDate);today =today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())//hotel.getRooms().size(); can i do this instesasd this here
                    .closed(false)
                    .build();
//            inventoryRepository.save(inventory);
            inventories.add(inventory);
        }
        inventoryRepository.saveAll(inventories);
        //Use saveAll when inserting multiple records to reduce database round trips and improve performance.
    }

    @Override
    public void deleteFutureInventoriesByRoom(Room room) {
        LocalDate date =LocalDate.now();
        inventoryRepository.deleteByDateGreaterThanEqualAndRoom(date,room);
    }

    @Override
    public void deleteFutureInventoriesByHotel(Hotel hotel) {
        LocalDate date =LocalDate.now();
        inventoryRepository.deleteByDateGreaterThanEqualAndHotel(date,hotel);
    }


}
