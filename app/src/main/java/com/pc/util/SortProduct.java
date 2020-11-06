package com.pc.util;

import com.pc.model.Product;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SortProduct {

    public static List<Product> filterByProductName(List<Product> products, String searchText) {
        return products.stream().filter(product -> {
            if (searchText != null && !searchText.trim().equals("")) {
                assert product != null;
                String productName = Objects.requireNonNull(product.getName());
                return productName.toLowerCase().contains(searchText.toLowerCase());
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static List<Product> sortAlphabetically(List<Product> products, boolean sortAscending){
        Collections.sort(products, (p1, p2) -> {
            String name1 = Objects.requireNonNull(p1.getName());
            String name2 = Objects.requireNonNull(p2.getName());
            if (sortAscending)
                return name1.compareTo(name2);
            return -name1.compareTo(name2);
        });
        return products;
    }
}
