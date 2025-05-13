package com.pricecomparator.service;

import java.io.File;
import java.util.*;

import com.pricecomparator.model.Discount;
import com.pricecomparator.model.Product;

public class MarketDataLoader {
    
    public static final String RESOURCE_PATH = "src/main/resources/";

    public static Map<String, List<Product>> loadAllProductForDate(String date)
    {
        Map<String, List<Product>> storeProducts = new HashMap<>();

        File folder = new File(RESOURCE_PATH);
        File[] files = folder.listFiles();

        if(files == null) return storeProducts;

        for(File file : files)
        {
            String fileName = file.getName();

            if(fileName.endsWith(date+".csv") && !fileName.contains("discounts"))
            {
                String storeName = fileName.split("_")[0];
                List<Product> products = ProductLoader.loadFromCSV(file.getPath());
                storeProducts.put(storeName, products);
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

        for(File file : files)
        {
            String fileName = file.getName();
            if(fileName.contains("discounts") && fileName.endsWith(date + ".csv"))
            {
                String storeName = fileName.split("_")[0];
                List<Discount> discounts = DiscountLoader.loadFromCSV(file.getPath());
                storeDiscounts.put(storeName, discounts);
            }
        }

        return storeDiscounts; 
    }
}
