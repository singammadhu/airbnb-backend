package com.codingshuttle.projects.airBnbApp.strategy;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;

import javax.swing.*;
import java.math.BigDecimal;

public interface PricingStrategy {//Any pricing strategy must know how to calculate a price from an Inventory
    BigDecimal calculatePrice(Inventory inventory);
}
//We separated individual pricing rules into independent strategies and composed them together,
//instead of putting all pricing conditions into one large class.