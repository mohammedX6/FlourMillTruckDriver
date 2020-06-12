package com.truckdriverco.truckdriver;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView v1;
    Fragment selected;
    FrameLayout framelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        v1 = findViewById(R.id.navigationView);
        framelay = findViewById(R.id.frame1);
        selected = new NewOrdersFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame1, selected).commit();
        v1.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.track: {
                        selected = new NewOrdersFragment();
                        break;
                    }

                    case R.id.hisory: {
                        selected = new HistoryFragment();
                        break;
                    }
                    case R.id.account: {
                        selected = new UserFragment();
                        break;
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame1, selected).commit();

                return true;
            }
        });
    }

}

