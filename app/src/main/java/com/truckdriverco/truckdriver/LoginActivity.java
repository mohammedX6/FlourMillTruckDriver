package com.truckdriverco.truckdriver;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonElement;
import com.truckdriverco.truckdriver.API_Calls.UserClientAPI;
import com.truckdriverco.truckdriver.Model.TruckDriverLogin;
import com.truckdriverco.truckdriver.Model.TruckDriverLoginFirebase;
import com.truckdriverco.truckdriver.Model.User;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    TextView t1;
    Button b1;
    EditText passwordx1, emaila;
    SharedPreferences pref3;
    SharedPreferences.Editor editor;
    TruckDriverLogin truckDriverLogin;

    Retrofit retrofit;
    UserClientAPI userClientAPI;
    TruckDriverLoginFirebase firebase;
    CallbackManager callbackManager;
    FirebaseAuth firebaseAuth;
    LoginButton loginButton;
    AccessTokenTracker accessTokenTracker;
    boolean flag;
    private ProgressBar mProgressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        pref3 = getApplicationContext().getSharedPreferences("secretcode", MODE_PRIVATE);
        editor = pref3.edit();
        firebaseAuth = FirebaseAuth.getInstance();
        mProgressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebookcontext", "success " + loginResult);
                Log.d("facebookcontext", "success " + loginResult.getAccessToken());
                handleFacebookToken(loginResult.getAccessToken());


            }

            @Override
            public void onCancel() {
                Log.d("facebookcontext", "cancle ");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("facebookcontext", "faiil ");
            }
        });


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                if (currentAccessToken == null) {
                    firebaseAuth.signOut();
                }


            }
        };


        hideSoftKeyboard();
        setupRetrofit();
        setupFirebaseAuth();
        setTitle("Log in");
        t1 = findViewById(R.id.signup);
        b1 = findViewById(R.id.loginBtn);
        passwordx1 = findViewById(R.id.passwordLog);
        emaila = findViewById(R.id.emailLog);

        setSpan2(t1);
       /* shared1 = getSharedPreferences("Account", 0);
        String token = pref3.getString("token", "");
        if (!token.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();

        } else {

        }
*/
        b1.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookToken(AccessToken token) {
        Log.d("token", token + " ");
        try {
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());


            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Log.d("task firebase", token + " ");
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        DocumentReference userRef = db.collection("Users")
                                .document(user.getUid());


                        User user1 = new User();
                        user1.setEmail(user.getEmail());
                        user1.setUser_id(user.getUid());
                        user1.setUsername(user.getDisplayName());
                        user1.setAvatar(user.getPhotoUrl().toString());
                        Log.d("checkio", "email:" + user1.getEmail());
                        Log.d("checkio", "email:" + user1.getUser_id());
                        Log.d("checkio", "email:" + user1.getUsername());

                        userRef.set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User saved.");
                                }
                            }
                        });

                        Call<JsonElement> call = userClientAPI.checkTruck(user1.getUser_id());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                                if (response.isSuccessful()) {


                                    String cValue = response.body().getAsJsonObject().get("check").getAsString();
                                    Log.d("cvalues ", cValue);

                                    if (cValue.equals("no")) {
                                        flag = true;


                                    } else {

                                        String value = response.body().getAsJsonObject().get("token").getAsString();

                                        JWT jwt = new JWT(value);

                                        Claim userId = jwt.getClaim("nameid");
                                        Claim name = jwt.getClaim("unique_name");

                                        String myNameId = userId.asString();
                                        editor.putString("token", value);
                                        editor.putString("nameid", myNameId);
                                        editor.putString("name", name.asString());
                                        Log.d("token ", value);
                                        Log.d("newTokenss", value);
                                        editor.apply();
                                        flag = false;

                                    }
                                } else {
                                    Log.d("erroids", "error ");
                                }

                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {

                                Log.d("erroids", "error in connection ");

                            }
                        });


                    } else {
                        Log.d("failed", task.getException() + " ");


                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void updateUI(User user) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    void setSpan2(TextView text2) {
        SpannableString spanIn = new SpannableString("No account yet? Create one");
        ClickableSpan clickableSpan = new ClickableSpan() {

            public void onClick(View textView) {
                Intent myIntet = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(myIntet);
            }

            public void updateDrawState(TextPaint textP) {
                super.updateDrawState(textP);
                textP.setUnderlineText(true);
                textP.setUnderlineText(true);
                int myColor = ContextCompat.getColor(getApplicationContext(), R.color.myYellow);
                textP.setColor(myColor);


            }
        };
        spanIn.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        text2.setText(spanIn);
        text2.setMovementMethod(LinkMovementMethod.getInstance());
        int myColor = ContextCompat.getColor(getApplicationContext(), R.color.myYellow);
        // text2.setHighlightColor(myColor);
    }

    boolean checkpass() {
        String passc = passwordx1.getText().toString().trim();
        if (passc.isEmpty()) {
            passwordx1.setError("This field can not be blank");
            return false;
        } else {
            passwordx1.setError(null);

            return true;
        }
    }

    boolean checkemail() {
        String ea1 = emaila.getText().toString().trim();
        if (ea1.isEmpty()) {
            emaila.setError("This field can not be blank");
            return false;
        } else {
            emaila.setError(null);

            return true;
        }
    }

    @Override
    public void onClick(View v) {
        String emailc1 = "", passowrd1 = "";
        String prefEmail = "", prefPass;
        switch (v.getId()) {
            case R.id.loginBtn: {
                if (!checkemail() | !checkpass()) {
                    return;
                } else {

                    login();
                }

            }
        }
    }

    void login() {

        try {
            String v1 = emaila.getText().toString();
            String v2 = passwordx1.getText().toString();

            truckDriverLogin = new TruckDriverLogin(v1, v2);

            Call<JsonElement> call = userClientAPI.login(truckDriverLogin);
            firebase = new TruckDriverLoginFirebase(v1, v2);
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


                    } else {
                        Log.d("TAG", response.code() + "");
                        Toast.makeText(LoginActivity.this, "Log in information not correct", Toast.LENGTH_LONG).show();

                    }

                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Log.d("TAG", t.getMessage() + "");

                    Toast.makeText(LoginActivity.this, "Error in request", Toast.LENGTH_LONG).show();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                signInFirebase();
            }
        }, 2000);

    }

    private void signInFirebase() {

        try {


            showDialog();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(firebase.getEmail(),
                    firebase.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            hideDialog();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupFirebaseAuth() {
        try {


            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        DocumentReference userRef = db.collection("Users")
                                .document(user.getUid());


                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: successfully set the user client.");
                                    User user = task.getResult().toObject(User.class);

                                    ((UserClient) (getApplicationContext())).setUser(user);
                                }
                            }
                        });
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (flag == false) {
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else if (flag == true) {
                                    Intent intent = new Intent(LoginActivity.this, CompleteRegisterActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }, 3000);


                    } else {
                        Log.d(TAG, "signed_out");
                    }

                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
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
}
