package com.codingshuttle.projects.airBnbApp.entity;

import jakarta.persistence.*;
 import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String type;
    // Room category/type
    // Example: Deluxe, Suite, Standard

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    // precision = total digits (10)
    // scale = digits after decimal (2)
    // Example: 12345678.99

    @Column(nullable = false)
    private Integer totalCount;
    // Number of actual physical rooms for this type

    // Example:
    // Hotel
    //   ├── Deluxe   -> 20 rooms
    //   ├── Suite    -> 10 rooms
    //   └── Standard -> 15 rooms

    @Column(nullable = false)
    private Integer capacity;
    // Max people allowed in one room
    // Example: capacity = 2

    @Column(columnDefinition = "TEXT[]")
    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    // Set only once when inserted
    // Never changes after creation

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // Updates automatically whenever entity changes
}