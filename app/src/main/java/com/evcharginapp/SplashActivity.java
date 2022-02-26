package com.evcharginapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.evcharginapp.AppConstants.AppConstant;
import com.evcharginapp.apppreferences.AppPreference;
import com.evcharginapp.databinding.ActivityMainBinding;
import com.evcharginapp.databinding.ActivitySplashBinding;
import com.evcharginapp.dialog.Alert;
import com.evcharginapp.ui.activity.LoginActivity;
import com.evcharginapp.utils.LocationUtils;
import com.google.android.material.navigation.NavigationView;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    LocationUtils mLocationUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.mLocationUtils = new LocationUtils(this);
        AppPreference.INSTANCE.initSharedPreference(this);
        checkPermissionRequest();

    }
    private void checkPermissionRequest() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 1010);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1010) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                Alert.show(this, getString(R.string.app_name), "Please enable permission to get near by Deals");
            } else {
                getLocation();
            }
        }
    }
    private void getLocation() {
        this.mLocationUtils.beginUpdates(this);
        if (!this.mLocationUtils.hasLocationEnabled()) {
            startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 1);
        } else {
            sendToOtherActivity();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sendToOtherActivity();
    }
    private void sendToOtherActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (AppPreference.INSTANCE.getLoginStatus())
                {
                    Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        }, AppConstant.DELAY_TIMER);
    }

}