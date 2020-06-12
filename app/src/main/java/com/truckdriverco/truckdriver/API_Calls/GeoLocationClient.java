package com.truckdriverco.truckdriver.API_Calls;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface GeoLocationClient {

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("api/GeoLocation/getLocation/{id}")
    Call<JsonElement> getGeoLocation(@Path("id") int id);


}
