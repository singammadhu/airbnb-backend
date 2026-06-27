package com.codingshuttle.projects.airBnbApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

 import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    private String city;

    @Column(columnDefinition = "TEXT[]")
    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    @Embedded
    private HotelContactInfo contactInfo;
    //contactInfo_address
    //contactInfo_phoneNumber
    //contactInfo_email
    //contactInfo_location

    @Column(nullable = false)
    private Boolean active;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    private User owner;

    @OneToMany(
            mappedBy = "hotel",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
            // when hotel is deleted → delete its rooms

            // orphanRemoval = true
            // when child removed from collection
            // hotel.getRooms().remove(room)
            // parent exists, child removed → then delete child
    )
    private List<Room> rooms;
    //Stores room categories/types for hotel
// Example:
// Deluxe -> 20 rooms
// Suite -> 10 rooms
// Standard -> 15 rooms
// rooms.size() = number of room types, not physical rooms

}
