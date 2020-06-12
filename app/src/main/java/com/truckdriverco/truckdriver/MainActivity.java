package com.truckdriverco.truckdriver;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.truckdriverco.truckdriver.Services.LocationService;

public class MainActivity extends AppCompatActivity {
    private final int ACTION_LOCATION_SETTING = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        LocationManager lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (gps_enabled && network_enabled) {

            startLocationService();
            goToAnotherActivityDelay();
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("You mush enable GPS before continue")
                    .setPositiveButton("Yes", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    Intent locationSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(locationSettingIntent, ACTION_LOCATION_SETTING);
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_LOCATION_SETTING:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                    goToAnotherActivityDelay();
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void goToAnotherActivityDelay() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                finish();
                Intent menu = new Intent(getBaseContext(), MiddleActivity.class);
                startActivity(menu);
            }
        }, 3000);
    }


    void startLocationService() {
        if (!isLocationServiceIsRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MainActivity.this.startForegroundService(serviceIntent);

            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceIsRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.truckdriverco.truckdriver.Services.LocationService".equals(serviceInfo.service.getClassName())) {
                Log.d("MainActivity", "Location service is already running.");
                return true;
            }
        }
        return false;
    }


}
