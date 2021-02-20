package com.example.jellytrip.geo;
import com.example.jellytrip.geo.types.Coordinates;
import com.example.jellytrip.geo.types.Route;

public interface RouteProvider {
    Route paveRoute(Coordinates from, Coordinates dest);
    Coordinates getNextDot();
    Coordinates getCurrentDot();
    double getBearing();
}
