package com.truckdriverco.truckdriver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.truckdriverco.truckdriver.API_Calls.OrderClient;
import com.truckdriverco.truckdriver.Model.Order;
import com.truckdriverco.truckdriver.RecycleViews.RecycleViewAdapterOrderHistory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


public class HistoryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecycleViewAdapterOrderHistory recycleViewAdapterOrderHistory;
    List<Order> myOrders;
    private Retrofit retrofit;
    private RecyclerView r1;
    private OrderClient orderClient;
    private LinearLayoutManager layoutManager;
    private SharedPreferences pref;
    public HistoryFragment() {

    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        pref = getActivity().getSharedPreferences("secretcode", MODE_PRIVATE);
        r1 = getActivity().findViewById(R.id.myorderhistory);
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

        Call<List<Order>> call = orderClient.GetTruckDriverOrderHistory("Bearer " + pref.getString("token", "0"));
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    myOrders = response.body();
                    recycleViewAdapterOrderHistory = new RecycleViewAdapterOrderHistory(getContext(), myOrders);
                    r1.setAdapter(recycleViewAdapterOrderHistory);


                    Log.d("orderhistory", "order history  loaded");
                } else {
                    Log.d("orderhistory", "order history not loaded");
                }

            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

                Log.d("orderhistory", "error in connection");

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_history, container, false);
        r1 = v.findViewById(R.id.myorderhistory);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        r1.setLayoutManager(layoutManager);
        r1.setAdapter(recycleViewAdapterOrderHistory);
        return v;
    }
}
