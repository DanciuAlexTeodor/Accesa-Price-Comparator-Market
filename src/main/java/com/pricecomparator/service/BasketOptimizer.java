package com.pricecomparator.service;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;


import java.util.*;

import com.pricecomparator.model.Discount;
import com.pricecomparator.model.Product;

public class BasketOptimizer {

    public static void optimizeBasketSplit(List<String> basketProductIds, String date) {
    Map<String, List<Product>> storeProducts = MarketDataLoader.loadAllProductForDate(date);
    Map<String, List<Discount>> storeDiscounts = MarketDataLoader.loadAllDiscountsForDate(date);

    Map<String, List<String>> storeToItems = new HashMap<>();
    Map<String, Double> storeToCost = new HashMap<>();

    double totalOriginalPrice = 0;
    double totalDiscountedPrice = 0;
    Set<String> uniqueProducts = new HashSet<>(basketProductIds);
    List<String> outputLines = new ArrayList<>();

    outputLines.add("Optimized Basket Split for " + date + ":\n");

    for (String productId : uniqueProducts) {
        Product bestProduct = null;
        String bestStore = null;
        double bestFinalPrice = Double.MAX_VALUE;
        int appliedDiscount = 0;

        for (String store : storeProducts.keySet()) {
            for (Product p : storeProducts.get(store)) {
                if (!p.getId().equals(productId)) continue;

                double price = p.getPrice();
                int discount = storeDiscounts.getOrDefault(store, List.of()).stream()
                        .filter(d -> d.getProductId().equals(productId))
                        .mapToInt(Discount::getDiscountPercent)
                        .findFirst()
                        .orElse(0);

                double discountedPrice = price * (1 - discount / 100.0);

                if (discountedPrice < bestFinalPrice) {
                    bestProduct = p;
                    bestStore = store;
                    bestFinalPrice = discountedPrice;
                    appliedDiscount = discount;
                }
            }
        }

        if (bestProduct != null) {
            String line = "- " + bestProduct.getName() + ": " + String.format("%.2f", bestFinalPrice)
                    + " RON" + (appliedDiscount > 0 ? " (-" + appliedDiscount + "%)" : "");

            storeToItems.computeIfAbsent(bestStore, k -> new ArrayList<>()).add(line);
            storeToCost.put(bestStore, storeToCost.getOrDefault(bestStore, 0.0) + bestFinalPrice);

            totalOriginalPrice += bestProduct.getPrice();
            totalDiscountedPrice += bestFinalPrice;
        } else {
            outputLines.add("Product " + productId + " not found in any store.\n");
        }
    }

    for (String store : storeToItems.keySet()) {
        outputLines.add("\n" + capitalize(store) + " Shopping List:");
        outputLines.addAll(storeToItems.get(store));
        outputLines.add("Subtotal: " + String.format("%.2f", storeToCost.get(store)) + " RON\n");
    }

    double savings = totalOriginalPrice - totalDiscountedPrice;
    outputLines.add("Original total (no discounts): " + String.format("%.2f", totalOriginalPrice) + " RON");
    outputLines.add("Optimized total: " + String.format("%.2f", totalDiscountedPrice) + " RON");
    outputLines.add("Total money saved: " + String.format("%.2f", savings) + " RON");

    writeOutputToFile("output/optimized_basket_" + date + ".txt", outputLines);
    System.out.println("Result saved to: output/optimized_basket_" + date + ".txt");
}

private static void writeOutputToFile(String filePath, List<String> lines) {
    try {
        new File("output").mkdir(); // create output folder if missing
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8));

        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }

        writer.close();
    } catch (IOException e) {
        System.err.println("Failed to write output file: " + e.getMessage());
    }
}

private static String capitalize(String text) {
    if (text == null || text.isEmpty()) return text;
    return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
}

}
