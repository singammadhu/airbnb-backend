package com.codingshuttle.projects.airBnbApp.strategy;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy{
    private final PricingStrategy wrapped;//Think of wrapped as: "Whatever pricing strategy came before me."
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory); // Get the price calculated by the previous/wrapped strategy.
        return price.multiply(BigDecimal.valueOf(1.10));
    }
}
/*
SurgePricingStrategy
        ↓
wrapped.calculatePrice(inventory)
        ↓
BasePricingStrategy
        ↓
inventory.getPrice()
        ↓
₹5,000
 */
