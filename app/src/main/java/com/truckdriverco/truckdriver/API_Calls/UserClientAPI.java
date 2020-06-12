package com.truckdriverco.truckdriver.API_Calls;


import com.google.gson.JsonElement;
import com.truckdriverco.truckdriver.Model.TruckDriver;
import com.truckdriverco.truckdriver.Model.TruckDriverLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserClientAPI {

    @POST("/api/TruckDriverAuth/truckdriver_login")
    Call<JsonElement> login(@Body TruckDriverLogin login);

    @POST("/api/TruckDriverAuth/truckdriver_facebookregister")
    Call<JsonElement> registerFacebook(@Body TruckDriver register);

    @POST("/api/TruckDriverAuth/truckdriver_register")
    Call<JsonElement> register(@Body TruckDriver register);

    @GET("/api/TruckDriverAuth/checkTruck/{uid}")
    Call<JsonElement> checkTruck(@Path("uid") String uid);


}
