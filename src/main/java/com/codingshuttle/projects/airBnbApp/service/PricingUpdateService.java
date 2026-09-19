
package com.codingshuttle.projects.airBnbApp.service;
import com.codingshuttle.projects.airBnbApp.entity.Hotel;
import com.codingshuttle.projects.airBnbApp.entity.HotelMinPrice;
import com.codingshuttle.projects.airBnbApp.entity.Inventory;
import com.codingshuttle.projects.airBnbApp.repository.HotelMinPriceRepository;
import com.codingshuttle.projects.airBnbApp.repository.HotelRepository;
import com.codingshuttle.projects.airBnbApp.repository.InventoryRepository;
import com.codingshuttle.projects.airBnbApp.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal; import java.time.LocalDate;
import java.util.ArrayList; import java.util.List; import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingUpdateService {

    private final HotelRepository hotelRepository;                 // Fetch hotels
    private final InventoryRepository inventoryRepository;         // Fetch/update inventories
    private final HotelMinPriceRepository hotelMinPriceRepository; // Store hotel minimum prices
    private final PricingService pricingService;                   // Calculate dynamic prices

    // Runs automatically every hour at minute 0
    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {

        int page = 0;
        int batchSize = 100;

        while (true) {

            // Fetch 100 hotels at a time to avoid loading all hotels into memory
            Page<Hotel> hotelPage =
                    hotelRepository.findAll(PageRequest.of(page, batchSize));
            // Pageable pageable = PageRequest.of(page, batchSize);
            // Page<Hotel> hotelPage = hotelRepository.findAll(pageable);

            if (hotelPage.isEmpty()) {
                break;
            }

            // Update prices for each hotel in the current page
            for (Hotel hotel : hotelPage.getContent()) {
                updateHotelPrices(hotel);
            }

            page++;
        }
    }

    // Calculates and updates the price of one inventory
    public void updatePrice(Inventory inventory) {

        BigDecimal newPrice = pricingService.calculatePrice(inventory);
        inventory.setPrice(newPrice);

        // Saving is done later using saveAll() for better efficiency
    }

    private void updateHotelPrices(Hotel hotel) {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        // Get this hotel's inventories for the next one year
        List<Inventory> inventoryList =
                inventoryRepository.findByHotelAndDateBetween(
                        hotel,
                        startDate,
                        endDate
                );

        // Calculate and save dynamic prices
        updateInventoryPrices(inventoryList);

        // Calculate the cheapest price for each date
        updateHotelMinPrices(hotel, inventoryList, startDate, endDate);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {

        for (Inventory inventory : inventoryList) {
            updatePrice(inventory);
        }

        // Save all updated inventories in one batch
        inventoryRepository.saveAll(inventoryList);
    }

    private void updateHotelMinPrices(
            Hotel hotel,
            List<Inventory> inventoryList,
            LocalDate startDate,
            LocalDate endDate) {

        /*
         * Group inventories by date.
         *
         * Example:
         * Sept 19 → [Room A, Room B, Room C]
         * Sept 20 → [Room A, Room B, Room C]
         */
        // Group inventories by date to find the cheapest room for each date
        Map<LocalDate, List<Inventory>> inventoriesByDate =
                inventoryList.stream()
                        .collect(Collectors.groupingBy( inventory->inventory.getDate()));//Collectors.groupingBy(Inventory::getDate)

        List<HotelMinPrice> hotelPrices = new ArrayList<>();

        inventoriesByDate.forEach((date, inventories) -> {

            // Find the cheapest inventory price for this date
            BigDecimal minPrice = inventories.stream()
                    .map(Inventory::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

            // Update existing record or create a new one
            HotelMinPrice hotelMinPrice =
                    hotelMinPriceRepository
                            .findByHotelAndDate(hotel, date)
                            .orElse(new HotelMinPrice(hotel, date));

            hotelMinPrice.setPrice(minPrice);
            hotelPrices.add(hotelMinPrice);
        });

        // Save all hotel minimum prices in one batch
        hotelMinPriceRepository.saveAll(hotelPrices);
    }
}

/*
PricingUpdateService
│
├── updatePrices()
│     └── pagination → hotels
│
├── updatePrice()
│     └── calculate price for ONE inventory
│
├── updateHotelPrices()
│     ├── get 1-year inventories
│     ├── updateInventoryPrices()
│     └── updateHotelMinPrices()
│
├── updateInventoryPrices()
│     ├── updatePrice() for each inventory
│     └── saveAll()
│
└── updateHotelMinPrices()
      ├── group by date
      ├── find minimum price
      ├── find/create HotelMinPrice
      └── saveAll()
 */
/*
@Scheduled
    ↓
updatePrices()
    ↓
Pagination
    ↓
Get 100 Hotels
    ↓
updateHotelPrices(hotel)
    ↓
Get 1 year Inventory
    ↓
updateInventoryPrices()
    ↓
updatePrice(inventory)
    ↓
PricingService
    ↓
Base → Surge → Occupancy → Urgency → Holiday
    ↓
saveAll(inventoryList)
    ↓
Group Inventory by Date
    ↓
Find minimum price per date
    ↓
Find/Create HotelMinPrice
    ↓
Add to hotelPrices
    ↓
saveAll(hotelPrices)
 */
/*

 */
//Flow of updateHotelMinPrices
/*
 * Flow:
 *
 * inventoryList
 *      ↓
 * Group inventories by date
 *      ↓
 * Map<LocalDate, List<Inventory>>
 *      ↓
 * For each date
 *      ↓
 * Get all inventories for that date
 *      ↓
 * Get their prices
 *      ↓
 * Find minimum price
 *      ↓
 * Find existing HotelMinPrice
 *      OR
 * Create new HotelMinPrice
 *      ↓
 * Set minimum price
 *      ↓
 * Add to hotelPrices
 *      ↓
 * saveAll()
 */

/*
1.Why do we need this @Scheduled(cron="0 0 * * * *")?

Hotel prices can change based on:
occupancy
urgency
holidays
other pricing strategies
So we don't want someone to manually call the pricing update every time.

2.difference between updatePrices methods
1.updatePrices()
     ↑
     └── Called automatically by Spring
         every hour
    ->Scheduled entry point
    ->Updates prices for ALL hotels/inventories

2.updatePrice(Inventory inventory)
     ↑
     └── Called by our code
         for one Inventory
    ->Works with ONE Inventory
    ->Calculates its dynamic price
 */

