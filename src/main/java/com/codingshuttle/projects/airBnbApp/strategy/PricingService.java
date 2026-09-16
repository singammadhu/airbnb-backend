package com.codingshuttle.projects.airBnbApp.strategy;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class PricingService {
    public BigDecimal calculatePrice(Inventory inventory) {
        PricingStrategy pricingStrategy = new BasePricingStrategy();
// Base → ₹5,000

        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
// Surge wraps Base
// 5000 × 1.10 = ₹5,500

        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
// Occupancy wraps Surge
// 5500 × 1.20 = ₹6,600

        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
// Urgency wraps Occupancy
// 6600 × 1.15 = ₹7,590

        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);
// Holiday wraps Urgency
// 7590 × 1.25 = ₹9,487.50

        return pricingStrategy.calculatePrice(inventory);
     }
}
//That's why this pattern is powerful: we can add/remove pricing rules without changing the existing strategies.