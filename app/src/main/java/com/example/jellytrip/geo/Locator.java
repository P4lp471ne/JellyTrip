package com.example.jellytrip.geo;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class Locator implements LocationProvider {

    final FusedLocationProviderClient client;

    public Locator(Context context) {
        this.client =
                LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public Coordinates getCurrentLocation() throws SecurityException, Exception{
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                throw new SecurityException();
//        }
        return null;
    }

    @Override
    public Coordinates getCarLocation() {
        return null;
    }

    @Override
    public double getAzimuth() {
        return 0;
    }

}
