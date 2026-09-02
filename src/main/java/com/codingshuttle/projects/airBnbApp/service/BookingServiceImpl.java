package com.codingshuttle.projects.airBnbApp.service;

import com.codingshuttle.projects.airBnbApp.dto.BookingDto;
import com.codingshuttle.projects.airBnbApp.dto.BookingRequest;
import com.codingshuttle.projects.airBnbApp.entity.*;
import com.codingshuttle.projects.airBnbApp.entity.enums.BookingStatus;
import com.codingshuttle.projects.airBnbApp.exception.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.repository.BookingRepository;
import com.codingshuttle.projects.airBnbApp.repository.HotelRepository;
import com.codingshuttle.projects.airBnbApp.repository.InventoryRepository;
import com.codingshuttle.projects.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ModelMapper modelMapper;


    private final InventoryRepository inventoryRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        log.info("Initialising booking for hotels: {}, rooms: {}, data {}-{}",
                bookingRequest.getHotelId(),
                bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate()
        );
        //Find the Hotel
        Hotel hotel =hotelRepository
                .findById(bookingRequest.getHotelId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Hotel not found with id: " + bookingRequest.getHotelId())
                        );

        //Find the Room
        Room room =roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Room not found with id: " + bookingRequest.getRoomId())
                );

        //inventory lookup
        // Inventory = one Hotel + one Room Type + one Date.
        // Therefore each booking date must have a corresponding inventory row.
        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                room.getId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount()
        );

        //rooms availability validation

        //1. counting Requested days count
        long daysCount = ChronoUnit.DAYS.between(
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate()
        )+1;
        //2 . checking available days are equal to Requested days count
        // If we don't have an inventory row for every requested date,
        // the room is not available for the complete booking period.
        if(inventoryList.size()!=daysCount){
            throw new ResourceNotFoundException(
                    "Room is not available for the selected dates."
            );
        }

        // Reserve the requested rooms
        // Temporarily reserve the requested number of rooms.
        // reservedCount represents rooms held for a booking that is
        // not yet fully completed/paid.
        for(Inventory inventory : inventoryList){
            inventory.setReservedCount(
                    inventory.getReservedCount()
                            +bookingRequest.getRoomsCount()
            );
        }
        // Save the updated inventory records
        inventoryRepository.saveAll(inventoryList);



        //Create the Booking

        User user = new User();
        user.setId(1L);//TODO: REMOVE DUMMY USER
        // TODO: calculate dynamic amount
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .user(user)
                .build();
        //save the booking and convert it to BookingDto and return.
        booking = bookingRepository.save(booking);

        return modelMapper.map(
                booking,
                BookingDto.class
                );
    }
}
