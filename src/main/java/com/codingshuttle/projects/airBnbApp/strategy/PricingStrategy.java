package com.codingshuttle.projects.airBnbApp.strategy;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;

import javax.swing.*;
import java.math.BigDecimal;

public interface PricingStrategy {//Any pricing strategy must know how to calculate a price from an Inventory
    BigDecimal calculatePrice(Inventory inventory);
}
