package com.pc.model;

import java.io.Serializable;
import java.util.List;

public class ShoppingList implements Serializable {

    private int id;
    private String name;
    private String date;
    private Store store;
    private List<Poster> posters;
    private User user;

    public ShoppingList(String name, String date, Store store, User user) {
        this.name = name;
        this.date = date;
        this.store = store;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public Store getStore() {
        return store;
    }

    public List<Poster> getPosters() {
        return posters;
    }

    public User getUser() {
        return user;
    }
}
