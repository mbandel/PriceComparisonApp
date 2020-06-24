package com.pc.retrofit;

import com.pc.model.Category;
import com.pc.model.Product;
import com.pc.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerApi {


    @GET("user/email/{email")
    public Call<User> findUserByEmail(@Path("email") String email);

    @GET("user/username{username}")
    public Call<User> findUserByUsername(@Path("username") String username);

    @GET("categories")
    public Call<List<Category>> getCategories(@Header("Authorization") String token);

    @GET("products/category/{id}")
    public Call<List<Product>> getProductsByCategoryId(@Header("Authorization") String token, @Path("id") int id);

    @POST("register")
    public Call<String> createUser(@Body User user);


}
