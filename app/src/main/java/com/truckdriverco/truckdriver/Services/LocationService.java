package com.truckdriverco.truckdriver.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.truckdriverco.truckdriver.Model.User;
import com.truckdriverco.truckdriver.Model.UserLocation;
import com.truckdriverco.truckdriver.UserClient;

public class LocationService extends Service {


    static final String TAG = "LocationService";
    final static long UPDATE_INTERVAL = 3000;
    final static long FASTEST_INTERVAL = 3000;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("");

            startForeground(1, builder.build());

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        getLocation();
        return START_NOT_STICKY;
    }

    private void getLocation() {

        LocationRequest locationRequestHighAccuracy = new LocationRequest();
        locationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        locationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


      /*  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "getLocation:stop location service");
            stopSelf();
            return;
        }*/
        Log.d(TAG, "getLocation: getting location information.");
        fusedLocationProviderClient.requestLocationUpdates(locationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                     //   Log.d(TAG, "onLocationResult: got location result.");
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            User user = ((UserClient) (getApplicationContext())).getUser();
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            UserLocation userLocation = new UserLocation(user, geoPoint, null);
                            saveUserLocation(userLocation);
                        }
                    }
                },
                Looper.myLooper());
    }

    private void saveUserLocation(final UserLocation location) {
        try {
            DocumentReference locationRef = FirebaseFirestore.getInstance().collection("user_locations").document(FirebaseAuth.getInstance().getUid());
            locationRef.set(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                   //     Log.d(TAG, "OnComplete" + location.getGeo_point().getLongitude() + "/" + location.getGeo_point().getLatitude());
                    }
                }
            });


        } catch (NullPointerException e) {
            Log.d(TAG, "saveuserlocation: User is null Stop.");
            Log.d(TAG, "saveuserlocation: nullpointer " + e.getMessage());
            stopSelf();

        }


    }
}