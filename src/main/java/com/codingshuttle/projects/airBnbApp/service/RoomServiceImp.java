package com.codingshuttle.projects.airBnbApp.service;

import com.codingshuttle.projects.airBnbApp.dto.RoomDto;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import com.codingshuttle.projects.airBnbApp.exception.ResourceNotFoundException;
import com.codingshuttle.projects.airBnbApp.repository.HotelRepository;
import com.codingshuttle.projects.airBnbApp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
 import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImp implements RoomService{

    private final RoomRepository roomRepository;
    private  final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper  modelMapper;



    @Override
    public RoomDto creaateNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
        Room room = modelMapper.map(roomDto,Room.class);
        room.setHotel(hotel);
        Room savedRoom = roomRepository.save(room);
        //TODO: Create inventory as soon as room is created and if hotel is active
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(savedRoom);
        }
        return modelMapper.map(savedRoom, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all room in the hotel with ID: {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        return hotel
                .getRooms()
                .stream()
                .map((element)->modelMapper.map(element,RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting all room with ID: {}",roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Room not found with ID: " + roomId));
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    public void deleteRoomById(Long roomId) {
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        log.info("Deleting room with ID: {}", roomId);
        //TODO: delete all future inventory for this room
        // delete all future inventory
        inventoryService.deleteFutureInventoriesByRoom(room);
        // delete room
        roomRepository.deleteById(roomId);

    }
}
