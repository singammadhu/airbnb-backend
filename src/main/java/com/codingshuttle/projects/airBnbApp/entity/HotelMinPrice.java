package com.codingshuttle.projects.airBnbApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class HotelMinPrice {//HotelMinPrice represents: Hotel + Date + Cheapest Price
//It is not storing every room's price. It stores the minimum price for a hotel on a particular date.
    /*
  Hotel
  │
  └── Inventory
       ├── Room A → ₹5,000
       ├── Room B → ₹7,000
       └── Room C → ₹10,000
                ↓
         HotelMinPrice
              ₹5,000
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many HotelMinPrice records can belong to one Hotel.
    // Example: Hotel A can have one minimum price record for each date.
    // LAZY = don't load the full Hotel object unless we actually access it.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
    /*
    Hotel
  │
  ├── HotelMinPrice (2026-09-17 → ₹5,000)
  ├── HotelMinPrice (2026-09-18 → ₹5,500)
  ├── HotelMinPrice (2026-09-19 → ₹4,800)
  └── ...
     */

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private BigDecimal price;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public HotelMinPrice(Hotel hotel, LocalDate date) {
        //is also useful because the price will be calculated later.
        this.hotel = hotel;
        this.date = date;
    }
}

