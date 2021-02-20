package com.example.jellytrip.geo;

public interface Coordinates {
    static Coordinates minus(Coordinates nextDot, Coordinates currentDot) {
        return null;
    }

    double getX();
    double getY();
    Coordinates minus(Coordinates coordinates);
    Coordinates plus(Coordinates coordinates);
}
