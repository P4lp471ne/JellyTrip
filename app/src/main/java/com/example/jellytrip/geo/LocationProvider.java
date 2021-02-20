package com.example.jellytrip.geo;

public interface LocationProvider {
    Coordinates getCurrentLocation() throws Exception;

    Coordinates getCarLocation();

    double getAzimuth();
}
