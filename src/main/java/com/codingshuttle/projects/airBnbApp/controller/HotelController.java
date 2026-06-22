package com.codingshuttle.projects.airBnbApp.controller;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.codingshuttle.projects.airBnbApp.dto.HotelDto;
import com.codingshuttle.projects.airBnbApp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attempting to create a new hotel with name: {}",hotelDto.getName());
        HotelDto newHotelDto = hotelService.createNewHotel(hotelDto);
//        return new ResponseEntity<>(newHotelDto, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(newHotelDto);
    }
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){
        HotelDto hotelDto = hotelService.getHotelById(hotelId);
//        return new ResponseEntity<>(hotelDto,HttpStatus.OK);
        return ResponseEntity.ok(hotelDto);
    }
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        List<HotelDto> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId, @RequestBody  HotelDto hotelDto){
        HotelDto hotel = hotelService.updateHotelById(hotelId, hotelDto);
        return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId){
        hotelService.deleteHotelById(hotelId);
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