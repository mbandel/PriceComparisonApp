package com.pc.retrofit;

import com.pc.model.Category;
import com.pc.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ServerApi {

    @GET("categories")
    public Call<List<Category>> getCategories(@Header("Authorization") String token);

    @POST("register")
    public Call<String> createUser(@Body User user);
}
