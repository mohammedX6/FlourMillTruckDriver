package com.truckdriverco.truckdriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.truckdriverco.truckdriver.API_Calls.OrderClient;
import com.truckdriverco.truckdriver.Model.Order;
import com.truckdriverco.truckdriver.Model.OrderProducts;
import com.truckdriverco.truckdriver.Model.UpdateTruck;
import com.truckdriverco.truckdriver.Model.User;
import com.truckdriverco.truckdriver.Model.UserLocation;
import com.truckdriverco.truckdriver.Model.UserOrder;
import com.truckdriverco.truckdriver.RecycleViews.RecycleViewAdapterOrderHistoryDetails;

import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OrderHistoryDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecycleViewAdapterOrderHistoryDetails historyDetailsAdapter;
    Retrofit retrofit;
    OrderClient orderClient;
    SharedPreferences pref3;
    LinearLayout linearLayout;
    LinearLayoutManager layoutManager;
    List<OrderProducts> orderList;
    int OrderId;
    int oldId;
    String truckid;
    TextView payment, tons, dateT, statuesT, destinationT;
    ImageView imageView;
    FirebaseFirestore mDB;
    UserLocation userLocation;
    Order order;
    UserLocation userloc;
    FirebaseAuth firebaseAuth;
    String bakeryid;
    String destination;
    String adminid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_details);
        Intent in = getIntent();
        Bundle bundle = in.getExtras();
        OrderId = bundle.getInt("orderId");
        oldId = OrderId;
        firebaseAuth = FirebaseAuth.getInstance();
        int shipmentprice=bundle.getInt("shipment");
        String totalPayment = bundle.getString("payment");
        String totaltons = bundle.getString("tons");
        String customername = bundle.getString("customer");
        String date = bundle.getString("date");
        String statues = bundle.getString("statues");
        destination = bundle.getString("destination");
        bakeryid = bundle.getString("bakeryid");
        adminid = bundle.getString("adminid");
        String orderComment = bundle.getString("comment");
        truckid = bundle.getString("truckid");
        imageView = findViewById(R.id.imageclcik);
        recyclerView = findViewById(R.id.recyclerview91);
        payment = findViewById(R.id.billamount2);
        tons = findViewById(R.id.itemcount2);
        dateT = findViewById(R.id.orderdate2);
        statuesT = findViewById(R.id.orderstatues2);
        destinationT = findViewById(R.id.deliveryaddress2);
        mDB = FirebaseFirestore.getInstance();
        User user;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetOrder();

            }
        });
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging).
                        connectTimeout(5, TimeUnit.MINUTES).
                        writeTimeout(5, TimeUnit.MINUTES).
                        readTimeout(5, TimeUnit.MINUTES)
                .build();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://floutmill-jo.azurewebsites.net/").
                addConverterFactory(GsonConverterFactory.create()).client(client);

        retrofit = builder.build();
        orderClient = retrofit.create(OrderClient.class);


        Call<List<OrderProducts>> call = orderClient.getAllOrdersDetail(OrderId);

        call.enqueue(new Callback<List<OrderProducts>>() {
            @Override
            public void onResponse(Call<List<OrderProducts>> call, Response<List<OrderProducts>> response) {

                if (response.isSuccessful()) {
                    orderList = response.body();
                    historyDetailsAdapter = new RecycleViewAdapterOrderHistoryDetails(getApplicationContext(), orderList);
                    layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(historyDetailsAdapter);
                    Toasty.success(getApplicationContext(), "Order details loaded", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("TAG", response.code() + "");
                    Toasty.error(getApplicationContext(), "Order details not loaded", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<OrderProducts>> call, Throwable t) {

                Toasty.error(getApplicationContext(), "Error in Connection", Toast.LENGTH_LONG).show();

            }
        });

        payment.setText("Bill Amount: " + totalPayment + "$");
        tons.setText("Total Tons: " + totaltons);
        dateT.setText("Order Date: " + date);
        statuesT.setText("Order statues: " + OrderStatues(Integer.parseInt(statues)));
        destinationT.setText("Order Destination: " + destination);
        getUserDetails();
        order = new Order();
        order.setId(OrderId);
        order.setCustomerName(customername);
        order.setBadgeName("badges");
        order.setTotalPayment(Double.parseDouble(totalPayment));
        order.setDestination(destination);
        order.setTotalTons(Double.parseDouble(totaltons));
        order.setOrderComment(orderComment);
        order.setAdministratorID(Integer.parseInt(adminid));
        order.setBakeryID(Integer.parseInt(bakeryid));
        order.setOrderStatues(1);
        order.setOrder_Date(date);
        order.setShipmentPrice(shipmentprice);

    }

    String OrderStatues(int statues) {
        String orderStatues = "";
        if (statues == 0) {
            orderStatues = "New Order";
        }
        if (statues == 1) {
            orderStatues = "In Progress";
        } else {
            orderStatues = "Delivered";
        }
        return orderStatues;

    }


    void SetOrder() {
        UserOrder userOrder = new UserOrder();
        userOrder.setGeo_point(userloc.getGeo_point());
        userOrder.setOrder(order);
        firebaseAuth.getUid();

        userOrder.setUser(userloc.getUser());
        userOrder.setAll_orders(orderList);
        order.setTruckDriverID(userloc.getUser().getUser_id());
        order.setOrderStatues(2);
        DocumentReference OrderRef = mDB.collection("full_order").document(FirebaseAuth.getInstance().getUid());
        OrderRef.set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "latitude: " + userOrder.getGeo_point().getLatitude() +
                            " longitude: " + userOrder.getGeo_point().getLongitude());
                }
            }
        });

        UpdateTruck updateTruck = new UpdateTruck();
        updateTruck.setId(userloc.getUser().getUser_id());
        updateTruck.setOrderStatues(2);
        updateTruck.setOrderid(OrderId);
        updateTruck.setOldId(truckid);

        Call<ResponseBody> call2 = orderClient.UpdateOrder(updateTruck);

        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Toasty.success(getApplicationContext(), "Driver assigned to job.", Toasty.LENGTH_LONG).show();
                    Log.d("Hi new", "new truckdriverid " + updateTruck.getId() + "statues " + updateTruck.getOrderStatues() + "new order id " + updateTruck.getOrderid() + "" + "old truckdriverid " + updateTruck.getOldId());
                } else {
                    Log.d("Hi new", "new truckdriverid " + updateTruck.getId() + "statues " + updateTruck.getOrderStatues() + "new order id " + updateTruck.getOrderid() + "" + "old truckdriverid " + updateTruck.getOldId());
                    Toasty.error(getApplicationContext(), "Driver not assigned to job.", Toasty.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toasty.error(getApplicationContext(), "Error in connection", Toasty.LENGTH_LONG).show();

            }
        });

        Log.d("infoimp", "Integer.parseInt(adminid)" + adminid + "  ");
        Log.d("infoimp", "Integer.parseInt(bakeryid)" + bakeryid + "  ");

        Intent intent = new Intent();
        intent.putExtra("result", "himan");
        intent.putExtra("bID", Integer.parseInt(bakeryid));
        intent.putExtra("aID", Integer.parseInt(adminid));
        intent.putExtra("oID", OrderId);
        intent.putExtra("desti", destination);


        setResult(RESULT_OK, intent);
        finish();


    }

    void getUserDetails() {
        try {
            DocumentReference userRef = mDB.collection("user_locations")
                    .document(FirebaseAuth.getInstance().getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        userloc = task.getResult().toObject(UserLocation.class);
                        Log.d(TAG, "onComplete: successfully set the user client." + userloc.getGeo_point() + " " + userloc.getTimestamp() + " " + userloc.getUser().getUser_id());

                    }

                }
            });
        } catch (Exception e) {

        }


    }
}
