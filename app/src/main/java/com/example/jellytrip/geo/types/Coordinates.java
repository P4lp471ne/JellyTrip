package com.example.jellytrip.geo.types;

import com.google.android.gms.maps.model.LatLng;

public class Coordinates {
    private double x;
    private double y;

    public Coordinates(double x, double y){
        this.x = x;
        this.y = y;
    }
    public Coordinates(LatLng latLng){
        this.x = latLng.longitude;
        this.y = latLng.latitude;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public LatLng toLatLng(){
        return new LatLng(y,x);
    }
}
