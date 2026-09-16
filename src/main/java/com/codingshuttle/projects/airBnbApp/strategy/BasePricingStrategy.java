package com.codingshuttle.projects.airBnbApp.strategy;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;

import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy{

    //Base = starting point → no wrapped.
    //Every decorator after Base = wraps another PricingStrategy.
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getPrice();
    }
}
/*
PricingStrategy only defines the contract: calculatePrice(...)

BasePricingStrategy provides the simplest implementation: Inventory price → return that price
*/