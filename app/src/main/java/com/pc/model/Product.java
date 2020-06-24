package com.pc.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Product {

    private int id;
    private String name;
    private Category category;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }
}
