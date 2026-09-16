package com.codingshuttle.projects.airBnbApp.strategy;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy  {
    private final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        LocalDate today = LocalDate.now();
        int daysToAdd = 7;

        // Approach 1 - Instructor's approach
        if (!inventory.getDate().isBefore(today)
                && inventory.getDate().isBefore(today.plusDays(daysToAdd))) {

            price = price.multiply(BigDecimal.valueOf(1.15));
        }

        /*
         * Approach 2 - Calculate the number of days between today
         * and the inventory date.
         *
         * long daysUntilBooking = LocalDate.now()
         *         .until(inventory.getDate())
         *         .getDays();
         *
         * if (daysUntilBooking >= 0 && daysUntilBooking < daysToAdd) {
         *     price = price.multiply(BigDecimal.valueOf(1.15));
         * }
         */
        return price;
    }
}
