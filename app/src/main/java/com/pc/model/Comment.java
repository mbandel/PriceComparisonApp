package com.pc.model;

public class Comment {

    private int id;
    private String content;
    private User user;
    private String date;
    private Poster poster;

    public Comment(String content, User user, String date, Poster poster){
        this.content = content;
        this.user = user;
        this.date = date;
        this.poster = poster;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Poster getPoster() {
        return poster;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }


}
