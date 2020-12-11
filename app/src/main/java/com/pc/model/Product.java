package com.pc.model;

public class Product {

    private int id;
    private String name;
    private Category category;

    public Product(){ }

    public Product(int id){
        this.id = id;
    }

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
