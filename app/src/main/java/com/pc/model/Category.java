package com.pc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
