package com.pricecomparator.repository;

import com.pricecomparator.model.Discount;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscountRepository {
    private final Map<String, List<Discount>> storeDiscounts;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DiscountRepository(Map<String, List<Discount>> storeDiscounts) {
        this.storeDiscounts = storeDiscounts;
    }

    public List<Discount> getDiscountsByStore(String store) {
        return storeDiscounts.getOrDefault(store, List.of());
    }

    public List<Discount> getActiveDiscounts(String store, String date) {
        LocalDate targetDate = LocalDate.parse(date, DATE_FORMATTER);
        List<Discount> activeDiscounts = storeDiscounts.getOrDefault(store, List.of()).stream()
                .filter(d -> isDiscountValid(d, targetDate))
                .collect(Collectors.toList());
        return activeDiscounts;
    }

    public Discount findDiscountForProduct(String store, String productId, String date) {
        LocalDate targetDate = LocalDate.parse(date, DATE_FORMATTER);
        return storeDiscounts.getOrDefault(store, List.of()).stream()
                .filter(d -> d.getProductId().equals(productId))
                .filter(d -> isDiscountValid(d, targetDate))
                .findFirst()
                .orElse(null);
    }

    private boolean isDiscountValid(Discount discount, LocalDate targetDate) {
        LocalDate fromDate = LocalDate.parse(discount.getFromDate(), DATE_FORMATTER);
        LocalDate toDate = LocalDate.parse(discount.getToDate(), DATE_FORMATTER);
        boolean isValid = !targetDate.isBefore(fromDate) && !targetDate.isAfter(toDate);
        return isValid;
    }

    public Map<String, List<Discount>> getAllStoreDiscounts() {
        return storeDiscounts;
    }
} 