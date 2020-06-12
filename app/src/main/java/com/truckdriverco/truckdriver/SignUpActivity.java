package com.truckdriverco.truckdriver;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.gson.JsonElement;
import com.truckdriverco.truckdriver.API_Calls.UserClientAPI;
import com.truckdriverco.truckdriver.Model.TruckDriver;
import com.truckdriverco.truckdriver.Model.User;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    TextView t2;
    Button b2, b1;
    int mYear, mMonth, mDay;
    String myFinalDate = "";
    User user2;

    EditText na, em, pass, myBirth, MyNational, e1, e2;
    Retrofit retrofit2;
    String namec = "", emailc = "", passowrd1 = "";
    UserClientAPI userClientAPI2;
    String job = "", phone = "";
    String userid;
    SharedPreferences pref3;
    SharedPreferences.Editor editor;
    private ProgressBar mProgressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        mProgressBar = findViewById(R.id.progressBar);

        mDb = FirebaseFirestore.getInstance();

        hideSoftKeyboard();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging).
                        connectTimeout(5, TimeUnit.MINUTES).
                writeTimeout(5, TimeUnit.MINUTES).
                readTimeout(5, TimeUnit.MINUTES)
                .build();


        Retrofit.Builder builder2 = new Retrofit.Builder().baseUrl("https://floutmill-jo.azurewebsites.net/").
                addConverterFactory(GsonConverterFactory.create()).client(client);
        retrofit2 = builder2.build();
        userClientAPI2 = retrofit2.create(UserClientAPI.class);
        pref3 = getApplicationContext().getSharedPreferences("secretcode", MODE_PRIVATE);
        editor = pref3.edit();
        setTitle("SignUpActivity");
        b1 = findViewById(R.id.birthbutton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        t2 = findViewById(R.id.signup);
        b2 = findViewById(R.id.upBtn);
        na = findViewById(R.id.name);
        em = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        myBirth = findViewById(R.id.birth);
        MyNational = findViewById(R.id.national);
        e1 = findViewById(R.id.job);
        e2 = findViewById(R.id.phone);


        SetSpan(t2);
        b2.setOnClickListener(this);
        b1.setOnClickListener(this);
    }

    void SetSpan(TextView text1) {
        try {
            SpannableString spanUp = new SpannableString("Already a member? LoginActivity");
            ClickableSpan clickableSpan = new ClickableSpan() {

                public void onClick(View textView) {
                    Intent myIntet = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(myIntet);
                }

                public void updateDrawState(TextPaint textP) {
                    super.updateDrawState(textP);
                    textP.setUnderlineText(true);
                    int myColor = ContextCompat.getColor(getApplicationContext(), R.color.myYellow);

                    textP.setColor(myColor);
                }
            };
            spanUp.setSpan(clickableSpan, 18, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text1.setText(spanUp);
            text1.setMovementMethod(LinkMovementMethod.getInstance());
            int myColor = ContextCompat.getColor(getApplicationContext(), R.color.myYellow);
            //  text1.setHighlightColor(myColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent myInter2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(myInter2);
                return true;
        }
        return true;
    }

    boolean checkname() {
        String na1 = na.getText().toString().trim();
        if (na1.isEmpty()) {
            na.setError("This field can not be blank");
            return false;
        } else {
            na.setError(null);

            return true;
        }
    }

    boolean checkemail() {
        String ea1 = em.getText().toString().trim();
        if (ea1.isEmpty()) {
            em.setError("This field can not be blank");
            return false;
        } else {
            em.setError(null);

            return true;
        }
    }

    boolean checkpass() {
        String passc = pass.getText().toString().trim();
        if (passc.isEmpty()) {
            pass.setError("This field can not be blank");
            return false;
        } else {
            pass.setError(null);

            return true;
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.upBtn: {
                if (!checkname() | !checkemail() | !checkpass()) {
                    return;
                }
                try {
                    namec = na.getText().toString();
                    emailc = em.getText().toString();
                    passowrd1 = pass.getText().toString();
                    job = e1.getText().toString();
                    phone = e2.getText().toString();
                    showDialog();
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailc, passowrd1)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());


                                        user2 = new User();
                                        user2.setEmail(emailc);
                                        user2.setUsername(emailc.substring(0, emailc.indexOf("@")));
                                        user2.setUser_id(FirebaseAuth.getInstance().getUid());
                                        userid = user2.getUser_id();
                                        TruckDriver MyRegister = new TruckDriver(userid, namec, passowrd1, emailc, myFinalDate, Integer.parseInt(MyNational.getText().toString()), phone, job);
                                        Call<JsonElement> call = userClientAPI2.register(MyRegister);
                                        call.enqueue(new Callback<JsonElement>() {
                                            @Override
                                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                                if (response.isSuccessful()) {

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


                                                    Log.d("TAG", response.code() + "");
                                                    GoToLogIn();
                                                    Toast.makeText(getApplicationContext(), "Account Created successfully", Toast.LENGTH_SHORT).show();
                                                } else {


                                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                Log.d("TAG", "User deleted" + "");


                                                            }

                                                        }
                                                    });
                                                    Log.d("TAG", response.code() + "");
                                                    Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<JsonElement> call, Throwable t) {

                                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            Log.d("TAG", t.getMessage() + "");

                                                            Toast.makeText(SignUpActivity.this, "Error in request", Toast.LENGTH_LONG).show();


                                                        }

                                                    }
                                                });



                                            }
                                        });

                                        hideDialog();

                                    } else {
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                                        hideDialog();
                                    }

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    void GoToLogIn() {

        try {
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()

                    .build();
            mDb.setFirestoreSettings(settings);

            DocumentReference newUserRef = mDb
                    .collection("Users")
                    .document(FirebaseAuth.getInstance().getUid());


            newUserRef.set(user2).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideDialog();

                    if (task.isSuccessful()) {

                        redirectLoginScreen();
                    } else {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
