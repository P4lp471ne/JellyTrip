package com.example.jellytrip.geo;

import com.google.android.gms.maps.model.LatLng;

public interface Coordinates {
    static Coordinates minus(Coordinates nextDot, Coordinates currentDot) {
        return null;
    }

    double getX();
    double getY();
    Coordinates minus(Coordinates coordinates);
    Coordinates plus(Coordinates coordinates);

    LatLng toLatLng();
}
