package com.example.jellytrip.geo;

import java.io.IOException;

public interface Calculator {
    double dist(Coordinates from, Coordinates to) throws IOException;
}
