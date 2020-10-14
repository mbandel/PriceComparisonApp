package com.pc.model;

public class Rating {

    private int id;
    private User user;
    private int value;
    private Poster poster;

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public int getValue() {
        return value;
    }

    public Poster getPoster() {
        return poster;
    }

    public Rating(int value, User user, Poster poster){
        this.value = value;
        this.user = user;
        this.poster = poster;
    }

    public Rating(int value){
        this.value = value;
    }
}
