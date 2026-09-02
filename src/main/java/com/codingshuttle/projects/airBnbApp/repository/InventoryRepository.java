package com.codingshuttle.projects.airBnbApp.repository;

import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
 import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
//    void deleteByDateAfterAndRoom(LocalDate date, Room room);
    void deleteByDateGreaterThanEqualAndRoom(LocalDate date,Room room);
    void deleteByDateGreaterThanEqualAndHotel(LocalDate date, Hotel hotel);

    //jpql
    @Query(
            """
                SELECT DISTINCT i.hotel
                FROM Inventory i
                WHERE i.city=:city
                      And i.date BETWEEN :startDate AND :endDate
                      And i.closed=false
                      And (i.totalCount-i.bookedCount-i.reservedCount)>=:roomsCount
                
                GROUP BY i.hotel, i.room      
                HAVING COUNT(i.date) =:dateCount
                
                   """
    )
     Page<Hotel> findHotelsWithAvailableInventory(
             @Param("city") String city,
             @Param("startDate") LocalDate startDate,
             @Param("endDate") LocalDate enDate,
             @Param("roomsCount")Integer roomsCount,
             @Param("dateCount") Long dateCount,
             Pageable pageable
             );

    // Lock the inventory rows so two users cannot reserve the same
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
             select i
             from Inventory i
             where
                   i.room.id=:roomId
               AND i.date between :startDate AND :endDate
               AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
               AND i.closed= false
"""
    )
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

// Inventory represents one Hotel + one Room Type + one Date.
//
// A Room belongs to one Hotel.
// Therefore, once we know the roomId, its associated Hotel is already known.
//
// However, one Room has many Inventory rows because inventory is maintained
// separately for each date. So we use roomId + date range to find the
// required inventory rows.
//
// We could also make the query more explicit by checking hotelId:
//
// SELECT i
// FROM Inventory i
// WHERE i.hotel.id = :hotelId
//   AND i.room.id = :roomId
//   AND i.date BETWEEN :startDate AND :endDate
//   AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
//   AND i.closed = false
//
// Instructor uses roomId only, so we follow that implementation for now.

  //Your mental model
//    Hotel
//     ↓ 1 : many
//    Room
//    ↓ 1 : many
//    Inventory
//    ↓
//    Date


}


/*/
@Query("""



        SELECT DISTINCT i.hotel
        /*
           Final output:
           Return unique hotels only.

           DISTINCT is used because one hotel can have multiple room types
           (Deluxe, Single, Suite), but we want hotel only once.
        */

  //  FROM Inventory i
        /*
           Inventory table stores daily room availability.

           Example:
           Taj + Deluxe + 1 July
           Taj + Deluxe + 2 July
           Taj + Deluxe + 3 July
        */

     //   WHERE i.city = :city
        /*
           Filter hotels by city user entered.

           Example:
           User searches Hyderabad
           → only Hyderabad hotels.
        */

   //     AND i.date BETWEEN :startDate AND :endDate
        /*
           Keep only requested stay dates.

           Example:
           Check-in = 1 July
           Check-out = 4 July

           We need inventory only for:
           1 July
           2 July
           3 July

           Ignore all other dates.
        */

      //  AND i.closed = false
        /*
           Ignore rooms which are blocked/closed.

           Example:
           Room under maintenance
           closed = true

           Do not show to user.
        */

    //    AND (i.totalCount - i.bookedCount) >= :roomsCount
        /*
           Check enough rooms available.

           totalCount  = total physical rooms
           bookedCount = already booked rooms

           availableRooms = totalCount - bookedCount

           Example:

           total = 10
           booked = 7

           available = 3

           If user needs 2 rooms:

           3 >= 2 → valid
        */

    //    GROUP BY i.hotel, i.room
        /*
           Create groups for each hotel + room type.

           Example:

           Group 1 → Taj + Deluxe
           Group 2 → Taj + Single
           Group 3 → Oberoi + Deluxe

           Why room also?

           Because same room type must be available
           for all requested dates.

           If we group only by hotel,
           Deluxe and Single can mix together
           → wrong result.
        */

     //   HAVING COUNT(i.date) = :dateCount
        /*
           dateCount = stay duration

           Example:

           Check-in = 1 July
           Check-out = 4 July

           Staying nights:

           1 July
           2 July
           3 July

           So:

           dateCount = 3


           COUNT(i.date) means:

           Count how many valid inventory rows exist
           inside each hotel + room group.

           Example:

           Taj + Deluxe

           1 July
           2 July
           3 July

           COUNT = 3


           Compare:

           COUNT(i.date) == dateCount

           3 == 3 → hotel valid


           If missing one date:

           Taj + Deluxe

           1 July
           2 July

           COUNT = 2

           2 != 3

           Reject hotel.


           Simple meaning:

           Check whether room is available
           for ALL requested stay dates.
        */

      //  """)


