package com.pc.model;

import androidx.annotation.Nullable;

public class Poster {

    private int id;
    private Product product;
    private Double price;
    private String date;
    private User user;
    private Store store;
    private int ratingValue;
    private Double promotionPrice;
    private String promotionDate;

    public Poster(){
        ratingValue = 0;
    }

    public Poster(int id){
        this.id = id;
        ratingValue = 0;
    }

    public Poster(Double promotionPrice, String promotionDate){
        this.promotionPrice = promotionPrice;
        this.promotionDate = promotionDate;
    }

    public Poster (int id, Product product, Double price, String date, User user, Store store){
        this.id = id;
        this.product = product;
        this.price = price;
        this.date = date;
        this.user = user;
        this.store = store;
        this.ratingValue = 0;
    }

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

    public Store getStore() { return store; }

    public int getRatingValue(){return ratingValue;}

    public void setId(int id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Double getPromotionPrice() {
        return promotionPrice;
    }

    public String getPromotionDate() {
        return promotionDate;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Poster poster;
        if (!(obj instanceof Poster)) {
            return false;
        } else {
            poster = (Poster) obj;
            return this.id == poster.getId();
        }
    }
}
