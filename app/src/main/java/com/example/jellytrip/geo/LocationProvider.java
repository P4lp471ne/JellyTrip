package com.example.jellytrip.geo;

import com.example.jellytrip.geo.types.Coordinates;

public interface LocationProvider {
    Coordinates getCurrentLocation() throws Exception;
}
