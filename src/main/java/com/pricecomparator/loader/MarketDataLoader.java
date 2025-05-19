package com.pricecomparator.loader;

import java.io.File;
import java.util.*;
import java.time.LocalDate;

import com.pricecomparator.model.Discount;
import com.pricecomparator.model.Product;

public class MarketDataLoader {
    
    public static final String RESOURCE_PATH = "src/main/resources/";

    public static Map<String, List<Product>> loadAllProductForDate(String date)
    {
        Map<String, List<Product>> storeProducts = new HashMap<>();
        File folder = new File(RESOURCE_PATH);
        File[] files = folder.listFiles();
        if (files == null) return storeProducts;

        Map<String, File> latestFileForStore = new HashMap<>();
        Map<String, LocalDate> latestDateForStore = new HashMap<>();
        LocalDate targetDate = LocalDate.parse(date);

        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".csv") && !fileName.contains("discounts")) {
                String[] parts = fileName.replace(".csv", "").split("_");
                if (parts.length < 2) continue;
                String storeName = parts[0];
                // Normalize store name: capitalize first letter, lowercase the rest
                storeName = storeName.substring(0, 1).toUpperCase() + storeName.substring(1).toLowerCase();
                String fileDateStr = parts[1];
                try {
                    LocalDate fileDate = LocalDate.parse(fileDateStr);
                    if (!fileDate.isAfter(targetDate)) {
                        if (!latestDateForStore.containsKey(storeName) || fileDate.isAfter(latestDateForStore.get(storeName))) {
                            latestDateForStore.put(storeName, fileDate);
                            latestFileForStore.put(storeName, file);
                        }
                    }
                } catch (Exception e) {
                    // Ignore files with invalid date format
                }
            }
        }

        for (Map.Entry<String, File> entry : latestFileForStore.entrySet()) {
            String storeName = entry.getKey();
            File file = entry.getValue();
            List<Product> products = ProductLoader.loadFromCSV(file.getPath());
            storeProducts.put(storeName, products);
        }
        return storeProducts;
    } 


    public static Map<String, List<Discount>> loadAllDiscountsForDate(String date)
    {
        Map<String, List<Discount>> storeDiscounts = new HashMap<>();
        File folder = new File(RESOURCE_PATH);
        File[] files = folder.listFiles();

        if(files == null) return storeDiscounts;

        LocalDate targetDate = LocalDate.parse(date);

        for(File file : files)
        {
            String fileName = file.getName();
            if(fileName.contains("discounts") && fileName.endsWith(".csv"))
            {
                String[] parts = fileName.replace(".csv", "").split("_");
                if (parts.length < 3) continue;
                String storeName = parts[0];
                // Normalize store name: capitalize first letter, lowercase the rest
                storeName = storeName.substring(0, 1).toUpperCase() + storeName.substring(1).toLowerCase();
                String fileDateStr = parts[2];
                try {
                    LocalDate fileDate = LocalDate.parse(fileDateStr);
                    
                    List<Discount> discounts = DiscountLoader.loadFromCSV(file.getPath());
                    storeDiscounts.computeIfAbsent(storeName, k -> new ArrayList<>()).addAll(discounts);
                } catch (Exception e) {
                    // Ignore files with invalid date format
                }
            }
        }

        return storeDiscounts; 
    }

    
}
