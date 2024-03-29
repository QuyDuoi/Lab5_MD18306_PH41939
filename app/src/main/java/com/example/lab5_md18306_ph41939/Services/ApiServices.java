package com.example.lab5_md18306_ph41939.Services;


import com.example.lab5_md18306_ph41939.Distrobutor;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiServices {
    String link = "http://192.168.1.9:3000/";
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
}
