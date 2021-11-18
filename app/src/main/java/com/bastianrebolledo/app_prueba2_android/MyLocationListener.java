package com.bastianrebolledo.app_prueba2_android;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.AccessibleObject;
import java.util.List;

public class MyLocationListener implements LocationListener {

    private TextView textView;
    private Activity mActivity;
    private Location actualLocation;

    public Location getActualLocation() {
        return actualLocation;
    }

    public MyLocationListener(Activity mActivity, TextView textView) {
        this.textView = textView;
        this.mActivity = mActivity;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        textView.setText(location.toString());
        actualLocation = location;
        //bottomNavigationView = (BottomNavigationView) mActivity.findViewById(R.id.bottomNavigationView);
        //bottomNavigationView.getMenu().findItem(R.id.navigation_my_location).setEnabled(true);
        //bottomNavigationView.getMenu().findItem(R.id.navigation_my_location).setVisible(true);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
