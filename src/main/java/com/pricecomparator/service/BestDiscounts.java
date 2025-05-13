package com.pricecomparator.service;

import java.util.*;

import com.pricecomparator.model.Discount;

public class BestDiscounts {

    public static final String AllStoresOption = "All stores";

    public static void showBestDiscounts(String store, String date, int numberOfOffers) {
        
        Map<String,List<Discount>> storeDiscounts = MarketDataLoader.loadAllDiscountsForDate(date);

        class StoreDiscount{

            Discount discount;
            String storeName;
            

            StoreDiscount(Discount discount, String storeName)
            {
                this.discount = discount;
                this.storeName = storeName;
            }
        }

        List<StoreDiscount> combined = new ArrayList<>();

        if(store.equals(AllStoresOption))
        {
            storeDiscounts.forEach((storeName,discounts) -> 
                discounts.forEach(d -> combined.add(new StoreDiscount(d,storeName))));
        }

        else if(storeDiscounts.containsKey(store.toLowerCase())){
            for(Discount d : storeDiscounts.get(store.toLowerCase())){
                combined.add(new StoreDiscount(d,store));
            }
        }
        else{
            System.out.println("No discounts found for store: " + store);
            return;
        }

        if(combined.isEmpty()){
            System.out.println("No discounts available at date : " + date);
        }


        combined.sort((a,b) -> Integer.compare(a.discount.getDiscountPercent(), b.discount.getDiscountPercent()));
        
        combined.sort((a, b) -> Integer.compare(b.discount.getDiscountPercent(), a.discount.getDiscountPercent()));

        System.out.println("\n🔥 Best Discounts on " + date + " (" + store + "):\n");

        for (int i = 0; i < Math.min(numberOfOffers, combined.size()); i++) {
            StoreDiscount sd = combined.get(i);
            String line = String.format(
                    "%d. %s (%s) - %d%% OFF",
                    i + 1,
                    sd.discount.getProductName(),
                    sd.discount.getBrand(),
                    sd.discount.getDiscountPercent()
            );

            if (store.equalsIgnoreCase(AllStoresOption)) {
                line += " @ " + capitalize(sd.storeName);
            }

            System.out.println(line);
        }
    }

     private static String capitalize(String s) {
        return s == null || s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
