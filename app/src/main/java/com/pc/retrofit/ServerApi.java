package com.pc.retrofit;

import com.pc.model.Category;
import com.pc.model.Comment;
import com.pc.model.Credentials;
import com.pc.model.Poster;
import com.pc.model.Product;
import com.pc.model.Rating;
import com.pc.model.ShoppingList;
import com.pc.model.Store;
import com.pc.model.Token;
import com.pc.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServerApi {

    @GET("user/email/{email}")
    public Call<User> findUserByEmail(@Header("Authorization") String token, @Path("email") String email);

    @GET("user/username{username}")
    public Call<User> findUserByUsername(@Path("username") String username);

    @GET("categories")
    public Call<List<Category>> getCategories(@Header("Authorization") String token);

    @GET("products")
    public Call<List<Product>> getProducts(@Header("Authorization") String token);

    @GET("products/category/{id}")
    public Call<List<Product>> getProductsByCategoryId(@Header("Authorization") String token, @Path("id") int id);

    @GET("posters/product/{id}")
    public Call<List<Poster>> getPostersByProductId(@Header("Authorization") String token, @Path("id") int id);

    @GET("poster/{id}")
    public Call<Poster> getPosterById(@Header("Authorization") String token, @Path("id") int id);

    @GET("poster/comments/{id}")
    public Call<List<Comment>> getCommentsByPosterId(@Header("Authorization") String token, @Path("id") int id);

    @GET("poster/ratings/{id}")
    public Call<List<Rating>> getPosterRatingsByPosterId(@Header("Authorization") String token, @Path("id") int id);

    @GET("rating/poster/{posterId}/user/{userId}")
    public Call<Rating> getRatingByUserAndPoster(@Header("Authorization") String token, @Path("posterId") int posterId, @Path("userId") int userId);

    @GET("stores")
    public Call<List<Store>> getStores(@Header("Authorization") String token);

    @GET("shoppingList/user/{id}")
    public Call<List<ShoppingList>> getShoppingListsByUserId(@Header("Authorization") String token, @Path("id") int id);

    @GET("shoppingList/{id}/posters")
    public Call<List<Poster>> getPostersByShoppingList(@Header("Authorization") String token, @Path("id") int id);

    @POST("rating")
    public Call<String> addRating(@Header("Authorization") String token, @Body Rating rating);

    @PUT("rating/{id}")
    public Call<String> editRating(@Header("Authorization") String token, @Path("id") int id, @Body Rating rating);

    @POST("authenticate")
    public Call<Token> authenticate(@Body Credentials credentials);

    @POST("comment")
    public Call<String> addComment(@Header("Authorization") String token, @Body Comment comment);

    @POST("register")
    public Call<String> createUser(@Body User user);

    @POST("poster")
    public Call<String> addPoster(@Header("Authorization") String token, @Body Poster poster);

    @POST("shoppingList")
    public Call<String> addShoppingList(@Header("Authorization") String token, @Body ShoppingList shoppingList);
}
