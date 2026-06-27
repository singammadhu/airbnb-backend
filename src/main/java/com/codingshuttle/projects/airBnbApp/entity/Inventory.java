package com.codingshuttle.projects.airBnbApp.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = @UniqueConstraint(//For one hotel + one room type + one date → only one record allowed.
                name="unique_hotel_room_date",
                columnNames = {"hotel_id","room_id","date"}
        )
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
// @AllArgsConstructor
// → Builder internally uses all fields constructor

// @NoArgsConstructor
// → Needed by Spring/JPA/Hibernate to create object using reflection

// Remember:
// Builder = Object creation
// AllArgs = Builder needs constructor
// NoArgs = Framework needs empty constructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)//default its eager
    @JoinColumn(name="hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id",nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false,columnDefinition = "INTEGER DEFAULT 0")
    private Integer bookedCount;
    // Number of already booked rooms
    // availableRooms = totalCount - bookedCount

    @Column(nullable = false)
    private Integer totalCount;
    // Total physical rooms of this room type

    @Column(nullable = false,precision = 5,scale = 2)
    private BigDecimal surgeFactor;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price;//basePrice*surgeFactor //it is called as Precomputed data or Denormalization
    //Why not calculate every time?
    //We store price separately to avoid joins and repeated calculation during search queries,
    // improving database performance at scale.

    @Column(nullable = false)
    private String city;
    // Stored separately to avoid joining Hotel table during city search

    @Column(nullable = false)
    private Boolean closed;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
