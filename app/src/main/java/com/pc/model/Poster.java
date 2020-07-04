package com.pc.model;

public class Poster {

    private int id;
    private Product product;
    private Double price;
    private String date;
    private User user;
    private Store store;

    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Double getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public Store getStore() {
        return store;
    }

    public Poster (int id, Product product, Double price, String date, User user, Store store){
        this.id = id;
        this.product = product;
        this.price = price;
        this.date = date;
        this.user = user;
        this.store = store;
    }

}
