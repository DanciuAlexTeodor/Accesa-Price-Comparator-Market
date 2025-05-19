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

        // First find the most recent date before or equal to the target date
        LocalDate targetDate = LocalDate.parse(date);
        LocalDate mostRecentDate = null;
        
        System.out.println("Loading products for " + targetDate);
        
        // Find the most recent date that has product files
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".csv") && !fileName.contains("discounts")) {
                String[] parts = fileName.replace(".csv", "").split("_");
                if (parts.length < 2) continue;
                String fileDateStr = parts[1];
                try {
                    LocalDate fileDate = LocalDate.parse(fileDateStr);
                    if (!fileDate.isAfter(targetDate)) {
                        if (mostRecentDate == null || fileDate.isAfter(mostRecentDate)) {
                            mostRecentDate = fileDate;
                        }
                    }
                } catch (Exception e) {
                    // Ignore files with invalid date format
                }
            }
        }
        
        if (mostRecentDate == null) {
            System.out.println("No product files found for date " + targetDate);
            return storeProducts;
        }
        
        System.out.println("Using products from date: " + mostRecentDate + " (most recent before " + targetDate + ")");
        
        // Now only load files from the most recent date
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
                    
                    // Only include files from the most recent date
                    if (fileDate.equals(mostRecentDate)) {
                        System.out.println("  Loading products for " + storeName + " from file: " + fileName);
                        List<Product> products = ProductLoader.loadFromCSV(file.getPath());
                        storeProducts.put(storeName, products);
                    }
                } catch (Exception e) {
                    // Ignore files with invalid date format
                }
            }
        }
        
        return storeProducts;
    } 


    public static Map<String, List<Discount>> loadAllDiscountsForDate(String date)
    {
        Map<String, List<Discount>> storeDiscounts = new HashMap<>();
        File folder = new File(RESOURCE_PATH);
        File[] files = folder.listFiles();

        if(files == null) return storeDiscounts;

        // First find the most recent date before or equal to the target date
        LocalDate targetDate = LocalDate.parse(date);
        LocalDate mostRecentDate = null;
        
        System.out.println("Loading discounts for " + targetDate);
        
        // Find the most recent date that has discount files
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.contains("discounts") && fileName.endsWith(".csv")) {
                String[] parts = fileName.replace(".csv", "").split("_");
                if (parts.length < 3) continue;
                String fileDateStr = parts[2];
                try {
                    LocalDate fileDate = LocalDate.parse(fileDateStr);
                    if (!fileDate.isAfter(targetDate)) {
                        if (mostRecentDate == null || fileDate.isAfter(mostRecentDate)) {
                            mostRecentDate = fileDate;
                        }
                    }
                } catch (Exception e) {
                    // Ignore files with invalid date format
                }
            }
        }
        
        if (mostRecentDate == null) {
            System.out.println("No discount files found for date " + targetDate);
            return storeDiscounts;
        }
        
        System.out.println("Using discounts from date: " + mostRecentDate + " (most recent before " + targetDate + ")");

        // Now only load files from the most recent date
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.contains("discounts") && fileName.endsWith(".csv")) {
                String[] parts = fileName.replace(".csv", "").split("_");
                if (parts.length < 3) continue;
                String storeName = parts[0];
                // Normalize store name: capitalize first letter, lowercase the rest
                storeName = storeName.substring(0, 1).toUpperCase() + storeName.substring(1).toLowerCase();
                String fileDateStr = parts[2];
                try {
                    LocalDate fileDate = LocalDate.parse(fileDateStr);
                    
                    // Only include files from the most recent date
                    if (fileDate.equals(mostRecentDate)) {
                        System.out.println("  Loading discounts for " + storeName + " from file: " + fileName);
                        List<Discount> discounts = DiscountLoader.loadFromCSV(file.getPath());
                        storeDiscounts.put(storeName, discounts);
                    }
                } catch (Exception e) {
                    // Ignore files with invalid date format
                }
            }
        }

        return storeDiscounts; 
    }

    
}
