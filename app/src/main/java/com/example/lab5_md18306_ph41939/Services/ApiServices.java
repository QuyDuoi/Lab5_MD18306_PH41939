package com.example.lab5_md18306_ph41939.Services;


import com.example.lab5_md18306_ph41939.Model.Distrobutor;
import com.example.lab5_md18306_ph41939.Model.Fruit;
import com.example.lab5_md18306_ph41939.Model.Response;
import com.example.lab5_md18306_ph41939.Model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiServices {
    String link = "http://192.168.1.4:3000/";
    @GET("/api/get-list-distributor")
    Call<ArrayList<Distrobutor>> getListDistributor();

    @GET("/api/search-distributor")
    Call<ArrayList<Distrobutor>> searchDistributor(@Query("key") String key);

    @POST("/api/add-distributor")
    Call<Distrobutor> addDistributor(@Body Distrobutor distributor);

    @PUT("/api/update-distributor-by-id/{id}")
    Call<Distrobutor> updateDistributor(@Path("id") String id, @Body Distrobutor distributor);

    @DELETE("/api/delete-distributor-by-id/{id}")
    Call<Void> deleteDistributor(@Path("id") String id);

    @Multipart
    @POST("/api/register-send-email")
    Call<Response<User>> register(@Part("username") RequestBody username,
                                  @Part("password") RequestBody password,
                                  @Part("email") RequestBody email,
                                  @Part("name") RequestBody name,
                                  @Part MultipartBody.Part avatar);
    @POST("/api/login")
    Call<Response<User>> login(@Body User user);

    @DELETE("/api/delete-fruit/{id}")
    Call<Void> deleteFruits (@Path("id") String id);

    @GET("/api/get-list-fruit")
    Call<Response<ArrayList<Fruit>>> getListFruit();

    @GET("/api/get-list-fruit")
    Call<Response<ArrayList<Fruit>>> getListFruit(@Header("Authorization")String token);

    @Multipart
    @POST("/api/add-fruit-with-file-image")
    Call<Response<Fruit>> addFruitWithFileImage(@PartMap Map<String, RequestBody> requestBodyMap,
                                                @Part ArrayList<MultipartBody.Part> ds_hinh
    );


    @Multipart
    @PUT("/api/update-fruit-by-id/{id}")
    Call<Response<Fruit>> updateFruit(@Path("id") String id,@PartMap Map<String, RequestBody> requestBodyMap,
                                      @Part ArrayList<MultipartBody.Part> ds_hinh);
}
