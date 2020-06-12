package com.truckdriverco.truckdriver;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.facebook.CallbackManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.truckdriverco.truckdriver.API_Calls.UserClientAPI;
import com.truckdriverco.truckdriver.Model.TruckDriver;
import com.truckdriverco.truckdriver.Model.TruckDriverLogin;
import com.truckdriverco.truckdriver.Model.TruckDriverLoginFirebase;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompleteRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView t1;
    Button b1;
    EditText passwordx1, emaila;
    SharedPreferences pref3;
    TruckDriverLogin truckDriverLogin;
    SharedPreferences.Editor editor;
    Retrofit retrofit;
    UserClientAPI userClientAPI;
    TruckDriverLoginFirebase firebase;
    CallbackManager callbackManager;
    FirebaseAuth firebaseAuth;
    int mYear, mMonth, mDay;
    String myFinalDate = "";
    EditText jobnumber, myBirth, MyNational, phoneNumber;
    Button btn1, btn2;
    private ProgressBar mProgressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);
        getSupportActionBar().hide();
        jobnumber = findViewById(R.id.jobnumber);
        MyNational = findViewById(R.id.nationalnewid);
        phoneNumber = findViewById(R.id.phonenumber);
        myBirth = findViewById(R.id.birthdate);
        btn1 = findViewById(R.id.birthbutton);
        btn2 = findViewById(R.id.upBtnnew);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        pref3 = getApplicationContext().getSharedPreferences("secretcode", MODE_PRIVATE);
        editor = pref3.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        mProgressBar = findViewById(R.id.progressBar);
        //   Toasty.success(getApplicationContext(), "creater", Toasty.LENGTH_LONG).show();
        setupRetrofit();
    }


    void login() {

        try {
            String userid = ((UserClient) (getApplicationContext())).getUser().getUser_id();
            String namec = ((UserClient) (getApplicationContext())).getUser().getUsername();
            String emailc = ((UserClient) (getApplicationContext())).getUser().getEmail();
            String job = jobnumber.getText().toString();
            String phone = phoneNumber.getText().toString();
            TruckDriver MyRegister = new TruckDriver(userid, namec, emailc, myFinalDate, phone, job, Long.parseLong(MyNational.getText().toString()));
            Log.d("test999", userid);
            Log.d("test999", namec);
            Log.d("test999", emailc);
            Log.d("test999", job);
            Log.d("test999", phone);
            Log.d("test999", userid);
            Log.d("test999", myFinalDate);
            Log.d("test999", Integer.parseInt(MyNational.getText().toString()) + "");
            Call<JsonElement> call = userClientAPI.registerFacebook(MyRegister);

            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.isSuccessful()) {

                        Log.d("TAG", response.message() + "");

                        String value = response.body().getAsJsonObject().get("token").getAsString();

                        JWT jwt = new JWT(value);

                        Claim userId = jwt.getClaim("nameid");
                        Claim name = jwt.getClaim("unique_name");

                        String myNameId = userId.asString();
                        editor.putString("token", value);
                        editor.putString("nameid", myNameId);
                        editor.putString("name", name.asString());
                        Log.d("token ", value);
                        editor.apply();

                        Intent intent = new Intent(CompleteRegisterActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();


                    } else {
                        Log.d("TAG", response.code() + "");
                        Toast.makeText(getApplicationContext(), "Some informations have wrong values.", Toast.LENGTH_LONG).show();

                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Log.d("TAG", t.getMessage() + "");

                    Toast.makeText(getApplicationContext(), "Error in request", Toast.LENGTH_LONG).show();


                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }


    public void setupRetrofit() {
        try {
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
            userClientAPI = retrofit.create(UserClientAPI.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upBtnnew: {
                login();

                break;
            }
            case R.id.birthbutton: {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                myFinalDate = mYear + "-" + mMonth + "-" + mDay;

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                myBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


                break;
            }
        }
    }
}
