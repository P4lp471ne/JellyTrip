package com.example.jellytrip.geo;

import android.location.Location;

import com.example.jellytrip.geo.types.Coordinates;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

public class Locator implements LocationProvider {

    final FusedLocationProviderClient client;

    public Locator(FusedLocationProviderClient client) {
        this.client = client;
    }

    @Override
    public Coordinates getCurrentLocation() throws SecurityException, Exception{
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                throw new SecurityException();
//        }
        Task<Location> locationResult = client.getLastLocation();
        // TODO КОСТИЛЬ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        while (!locationResult.isComplete()){
            continue;
        }
        if(!locationResult.isSuccessful()){
            throw locationResult.getException();
        }
        Location location =   locationResult.getResult();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        return new Coordinates(latLng);

    }

}
