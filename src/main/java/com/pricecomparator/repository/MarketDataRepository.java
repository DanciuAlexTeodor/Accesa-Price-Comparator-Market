package com.pricecomparator.repository;

import com.pricecomparator.model.Product;
import com.pricecomparator.model.Discount;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MarketDataRepository {
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public MarketDataRepository(ProductRepository productRepository, DiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
    }

    public Map<String, List<Product>> getProductsForDate(String date) {
        return productRepository.getAllStoreProducts();
    }

    public Map<String, List<Discount>> getValidDiscountsForDate(String date) {
        Map<String, List<Discount>> allDiscounts = discountRepository.getAllStoreDiscounts();
        Map<String, List<Discount>> filteredDiscounts = new HashMap<>();
        LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Filter discounts by date validity (must be active on the target date)
        allDiscounts.forEach((store, discounts) -> {
            List<Discount> validDiscounts = discounts.stream()
                .filter(discount -> {
                    LocalDate fromDate = LocalDate.parse(discount.getFromDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    LocalDate toDate = LocalDate.parse(discount.getToDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    
                    // Discount is valid if target date is between fromDate and toDate (inclusive)
                    return !targetDate.isBefore(fromDate) && !targetDate.isAfter(toDate);
                })
                .collect(Collectors.toList());
                
            if (!validDiscounts.isEmpty()) {
                filteredDiscounts.put(store, validDiscounts);
            }
        });

        return filteredDiscounts;
    }

    public Product getProduct(String store, String productId) {
        return productRepository.findProductById(store, productId);
    }

    public Discount getActiveDiscount(String store, String productId, String date) {
        return discountRepository.findDiscountForProduct(store, productId, date);
    }
} 