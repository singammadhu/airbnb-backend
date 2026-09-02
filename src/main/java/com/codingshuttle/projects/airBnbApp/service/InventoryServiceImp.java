package com.codingshuttle.projects.airBnbApp.service;

import com.codingshuttle.projects.airBnbApp.dto.HotelDto;
import com.codingshuttle.projects.airBnbApp.dto.HotelSearchRequest;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import com.codingshuttle.projects.airBnbApp.repository.InventoryRepository;
 import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImp implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

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

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {

        log.info("searching Hotels for {} city, from {} to {}", hotelSearchRequest.getCity(),hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate());
        // Create a Pageable object using the requested page number and page size
        Pageable pageable = PageRequest.of(
                hotelSearchRequest.getPage(),  // Page number (0-based)
                hotelSearchRequest.getSize()   // Number of hotels per page
        );

        // Calculate the total number of days between the check-in and check-out dates,
        // including both the start date and the end date.
        // Example: 10th to 12th = 3 days → 10th, 11th, and 12th are included.
        long dateCount = ChronoUnit.DAYS.between(
                hotelSearchRequest.getStartDate(),  // Check-in date
                hotelSearchRequest.getEndDate()     // Check-out date
        ) + 1; // +1 because BETWEEN includes both start and end dates

        // Fetch hotels that have sufficient inventory available
        // for the requested city, dates, and number of rooms
        Page<Hotel> hotelPage = inventoryRepository
                .findHotelsWithAvailableInventory(
                        hotelSearchRequest.getCity(),        // City to search hotels in
                        hotelSearchRequest.getStartDate(),   // Check-in date
                        hotelSearchRequest.getEndDate(),     // Check-out date
                        hotelSearchRequest.getRoomsCount(), // Number of rooms required
                        dateCount,                          // Number of days required
                        pageable                            // Pagination information
                );

        // Convert each Hotel entity into HotelDto
        // Page.map() preserves the pagination information
        return hotelPage.map(
                (element) -> modelMapper.map(element, HotelDto.class)
        );
    }
}

/*
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TimeBetweenExample {
    public static void main(String[] args) {
        LocalDate start = LocalDate.of(2026, 8, 20);
        LocalDate end = LocalDate.of(2026, 8, 25);

        // All calculations below are based on a 5-day window (Aug 20 to Aug 25)
        long years  = ChronoUnit.YEARS.between(start, end);   // Returns: 0 (Less than a full year)
        long months = ChronoUnit.MONTHS.between(start, end);  // Returns: 0 (Less than a full month)
        long weeks  = ChronoUnit.WEEKS.between(start, end);   // Returns: 0 (Less than a full 7-day week)

        // Returns: 5 (Includes 20, 21, 22, 23, 24 but excludes checkout date 25)
        long days   = ChronoUnit.DAYS.between(start, end);
    }
}

 */