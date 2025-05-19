package com.pricecomparator.repository;

import com.pricecomparator.model.Product;
import java.util.List;
import java.util.Map;

public class ProductRepository {
    private final Map<String, List<Product>> storeProducts;

    public ProductRepository(Map<String, List<Product>> storeProducts) {
        this.storeProducts = storeProducts;
    }

    public List<Product> getProductsByStore(String store) {
        return storeProducts.getOrDefault(store, List.of());
    }

    public Product findProductById(String store, String productId) {
        return storeProducts.getOrDefault(store, List.of()).stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public Map<String, List<Product>> getAllStoreProducts() {
        return storeProducts;
    }
} 