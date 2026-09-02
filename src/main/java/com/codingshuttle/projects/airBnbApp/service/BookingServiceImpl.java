package com.codingshuttle.projects.airBnbApp.service;

import com.codingshuttle.projects.airBnbApp.dto.BookingDto;
import com.codingshuttle.projects.airBnbApp.dto.BookingRequest;
import com.codingshuttle.projects.airBnbApp.dto.GuestDto;
import com.codingshuttle.projects.airBnbApp.entity.*;
import com.codingshuttle.projects.airBnbApp.entity.enums.BookingStatus;
import com.codingshuttle.projects.airBnbApp.exception.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final GuestRepository guestRepository;

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

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guests for booking with id: {}", bookingId);

        // Find the existing booking because guests must be associated
        // with a specific booking.
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Booking with id: " + bookingId
                        )
                );

        // A booking is only valid for a limited time after initialization.
        // If the 10-minute window has passed, we should not allow
        // guests to be added to the booking.
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has expired");
        }

        // Guests can only be added when the booking is in RESERVED state.
        // This prevents adding guests to bookings that have already moved
        // to another state such as GUESTS_ADDED or CONFIRMED.
        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException(
                    "Booking is not under reserved status, cannot add guests"
            );
        }

        // One booking can have multiple guests,
        // so process each GuestDto from the request.
        for (GuestDto guestDto : guestDtoList) {

            // Convert the API DTO into a Guest entity
            // because JPA repositories save entities, not DTOs.
            Guest guest = modelMapper.map(guestDto, Guest.class);

            // Currently we use a dummy logged-in user.
            // Later this will come from Spring Security/authentication.
            guest.setUser(getCurrentUser());

            // Save the Guest first so it gets its database ID.
            guest = guestRepository.save(guest);

            // Associate the saved Guest with this Booking.
            booking.getGuests().add(guest);
        }

        // All guests have been successfully added.
        // Move the booking from RESERVED → GUESTS_ADDED.
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);

        // Save the updated Booking, including its guest relationship.
        bookingRepository.save(booking);

        // Convert the updated Booking entity to BookingDto
        // so the API can return the booking information to the client.
        return modelMapper.map(booking, BookingDto.class);
    }

    public boolean hasBookingExpired(Booking booking) {

        // A booking is considered expired 10 minutes after it was created.
        return booking.getCreatedAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {

        // Temporary dummy user until authentication is implemented.
        // TODO: Replace this with the currently authenticated user.
        User user = new User();
        user.setId(1L);

        return user;
    }
}
