package com.pricecomparator.service;

import java.util.*;

import com.pricecomparator.model.Product;
import com.pricecomparator.repository.MarketDataRepository;

/**
 * Service for comparing products by their price-to-quantity ratio (value per unit).
 */
public class ValueUnit {

    private final MarketDataRepository marketDataRepository;
    
    // Constants for unit conversion
    private static final Set<String> VOLUME_SMALL_UNITS = Set.of("ml");
    private static final Set<String> VOLUME_LARGE_UNITS = Set.of("l");
    private static final Set<String> WEIGHT_SMALL_UNITS = Set.of("g");
    private static final Set<String> WEIGHT_LARGE_UNITS = Set.of("kg");
    private static final Set<String> COUNT_UNITS = Set.of("buc", "role");

    public ValueUnit(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

   
    public Map<String, Double> getBestValuePerUnit(String productId, String currentDate) {
        Map<String, List<Product>> storeProducts = marketDataRepository.getProductsForDate(currentDate);
        Map<String, Double> valueUnitPrices = new HashMap<>();
        String productName = "";

        for (String store : storeProducts.keySet()) {
            for (Product product : storeProducts.get(store)) {
                if (product.getId().equals(productId)) {
                    productName = product.getName();
                }
            }
        }
        
        System.out.println("Value comparison for " + productName);
        

        for (String store : storeProducts.keySet()) {
            for (Product product : storeProducts.get(store)) {
                if (product.getName().equals(productName)) {
                    double valueUnitPrice = calculateValueUnitPrice(product);
                    valueUnitPrices.put(store, valueUnitPrice);
                    
                    String unit = product.getUnit();
                    String numeUnitate = "";
                    if (WEIGHT_SMALL_UNITS.contains(unit)) {
                        numeUnitate = "kg";
                    } else if (VOLUME_SMALL_UNITS.contains(unit)) {
                        numeUnitate = "l";
                    }

                    System.out.printf("Store: %s, %s - %.2f RON per %s %n" , 
                        store, product.getName(), valueUnitPrice, numeUnitate);
                }
            }
        }
        
        if (valueUnitPrices.isEmpty()) {
            System.out.println("No products found with ID: " + productId);
        } else {
            // Find and display the store with the best value
            String bestStore = valueUnitPrices.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
                
            if (bestStore != null) {
              
                System.out.printf("Best value: %s (%.2f RON per standard unit)%n", 
                    bestStore, valueUnitPrices.get(bestStore));
            }
        }
        
        return valueUnitPrices;
    }
    
   
    private double calculateValueUnitPrice(Product product) {
        double price = product.getPrice();
        double quantity = product.getQuantity();
        String unit = product.getUnit();
        
        if (WEIGHT_SMALL_UNITS.contains(unit) || VOLUME_SMALL_UNITS.contains(unit)) {
            return price / quantity * 1000;
        } else if (WEIGHT_LARGE_UNITS.contains(unit) || VOLUME_LARGE_UNITS.contains(unit) || 
                  COUNT_UNITS.contains(unit)) {
            // Already in standard units (kg, l) or count-based units
            return price / quantity;
        } else {
            // Unknown unit, return price as is
            System.out.println("Warning: Unknown unit '" + unit + "' for product " + product.getName());
            return price;
        }
    }
}
