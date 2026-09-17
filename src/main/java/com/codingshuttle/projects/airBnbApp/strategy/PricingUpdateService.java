package com.codingshuttle.projects.airBnbApp.strategy;

import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingUpdateService {
    private final PricingService pricingService;

    public void updatePrice(Inventory inventory) {
        BigDecimal newPrice = pricingService.calculatePrice(inventory);
        inventory.setPrice(newPrice);
       // inventoryRepository.save(inventory);
        /*
         * We don't call inventoryRepository.save(inventory) here.
         *
         * InventoryServiceImp creates multiple Inventory objects,
         * adds them to a List, and finally uses saveAll(inventories).
         *
         * So here we only calculate and update the price in memory.
         * saveAll() will persist all the Inventory objects to the database.
         */
    }

}
