package com.codingshuttle.projects.airBnbApp.controller;

import com.codingshuttle.projects.airBnbApp.dto.RoomDto;
 import com.codingshuttle.projects.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
 import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId, @RequestBody RoomDto roomDto){
      RoomDto room =  roomService.creaateNewRoom(hotelId,roomDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomInHotel(@PathVariable Long hotelId){
        List<RoomDto> allRoomsInHotel = roomService.getAllRoomsInHotel(hotelId);
        return ResponseEntity.ok(allRoomsInHotel);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId){
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long roomId){
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

}

/*
 ResponseEntity Quick Notes

 1. GET API       -> 200 OK
    return ResponseEntity.ok(data);

 2. POST API      -> 201 CREATED
    return ResponseEntity.status(HttpStatus.CREATED).body(data);

 3. DELETE API    -> 204 NO CONTENT
    return ResponseEntity.noContent().build();

 4. PUT/PATCH API -> 200 OK
    return ResponseEntity.ok(updatedData);

 5. Error Responses
    400 -> Bad Request      (Invalid input)
    401 -> Unauthorized     (No login / Invalid token)
    403 -> Forbidden       (No permission)
    404 -> Not Found       (Resource missing)
    500 -> Internal Server Error

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
*/