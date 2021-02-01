package com.pc.model;

public class Store {

    private int id;
    private String name;
    private String address;

    public Store(){}

    public Store(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
