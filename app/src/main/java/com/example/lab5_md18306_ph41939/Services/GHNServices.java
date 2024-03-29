package com.example.lab5_md18306_ph41939.Services;

import com.example.lab5_md18306_ph41939.DistrictRequest;
import com.example.lab5_md18306_ph41939.Model.District;
import com.example.lab5_md18306_ph41939.Model.Province;
import com.example.lab5_md18306_ph41939.Model.Ward;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GHNServices {
    public static String GHN_URL = "https://dev-online-gateway.ghn.vn/";
    @POST("shiip/public-api/master-data/district")
    Call<ResponeGHN<ArrayList<District>>> getListDistrict(@Body DistrictRequest districtRequest);

    @GET("shiip/public-api/master-data/province")
    Call<ResponeGHN<ArrayList<Province>>> getListProvince();

    @GET("shiip/public-api/master-data/ward")
    Call<ResponeGHN<ArrayList<Ward>>> getListWard(@Query("district_id") int district_id);

}
