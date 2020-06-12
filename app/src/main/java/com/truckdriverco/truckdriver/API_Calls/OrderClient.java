package com.truckdriverco.truckdriver.API_Calls;


import com.google.gson.JsonElement;
import com.truckdriverco.truckdriver.Model.FinishOrder;
import com.truckdriverco.truckdriver.Model.Order;
import com.truckdriverco.truckdriver.Model.OrderProducts;
import com.truckdriverco.truckdriver.Model.UpdateTruck;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderClient {

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("api/Orders/update_truck")
    Call<ResponseBody> UpdateOrder(@Body UpdateTruck updateTruck);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("api/Orders/getallTruck")
    Call<List<Order>> getAllOrders(@Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("api/Orders/GetOrderDetailTruck/{id}")
    Call<List<OrderProducts>> getAllOrdersDetail(@Path("id") int id);


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("api/Orders/finish_order")
    Call<ResponseBody> FinishOrder(@Body FinishOrder finishOrder);


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("api/Orders/get_balance/{id}")
    Call<JsonElement> getBalance(@Path("id") String id);


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("api/Orders/getjob")
    Call<JsonElement> ReturnPreviousJob(@Header("Authorization") String auth);


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("api/Orders/checkTruck")
    Call<JsonElement> CheckIfTruckDriverHasAleradyJob(@Header("Authorization") String auth);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("api/Orders/gethistory")
    Call<List<Order>> GetTruckDriverOrderHistory(@Header("Authorization") String auth);

}
