package com.truckdriverco.truckdriver;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.truckdriverco.truckdriver.API_Calls.OrderClient;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment {

    TextView Balance, Help, Share, Settings, Logout, myInfo;
    SharedPreferences pref3;
    SharedPreferences.Editor editor;

    Retrofit retrofit;
    OrderClient orderClient;

    public UserFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Balance = getActivity().findViewById(R.id.balance);
        Help = getActivity().findViewById(R.id.help);
        myInfo = getActivity().findViewById(R.id.useinfo);
        Share = getActivity().findViewById(R.id.share);
        Settings = getActivity().findViewById(R.id.settings);
        pref3 = getActivity().getSharedPreferences("secretcode", MODE_PRIVATE);
        editor = pref3.edit();


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

        Log.d("balancenew", FirebaseAuth.getInstance().getUid());
        Call<JsonElement> call = orderClient.getBalance(FirebaseAuth.getInstance().getUid());
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                if (response.isSuccessful()) {
                    String value = response.body().getAsJsonObject().get("balance").getAsString();
                    Log.d("balancenew", value);
                    Balance.setText("Balance " + value);

                } else {
                    Toasty.error(getContext(), "Error", Toasty.LENGTH_LONG).show();
                    Balance.setText("Balance " + 0);

                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toasty.error(getContext(), "Error in connection", Toasty.LENGTH_LONG).show();


            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        Balance = view.findViewById(R.id.balance);

        Help = view.findViewById(R.id.help);
        Share = view.findViewById(R.id.share);
        Settings = view.findViewById(R.id.settings);
        Logout = view.findViewById(R.id.logout2);
        myInfo = view.findViewById(R.id.useinfo);


        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toasty.success(getContext(), "For any informations call me at 0770113245  ", Toasty.LENGTH_LONG).show();

            }
        });
        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.success(getContext(), "Working share ", Toasty.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String ShareSubject = "للاستفسار 0770133245";
                String ShareBody = " مطحنةالشمال";
                intent.putExtra(Intent.EXTRA_SUBJECT, ShareBody);
                intent.putExtra(Intent.EXTRA_TEXT, ShareSubject);
                startActivity(Intent.createChooser(intent, "Share using"));
            }
        });
        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                editor.clear();
                editor.commit();
                Toasty.success(getContext(), "See you soon !", Toasty.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        myInfo.setText("Welcome \n ");

        return view;
    }

}
