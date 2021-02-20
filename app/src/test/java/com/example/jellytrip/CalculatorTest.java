package com.example.jellytrip;

import com.example.jellytrip.geo.CalculatorImpl;
import com.example.jellytrip.geo.Coordinates;
import com.example.jellytrip.geo.CoordinatesImpl;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CalculatorTest {

    @Test
    public void CanGetDist() throws IOException {
        // Arrange
        CalculatorImpl calc  = new CalculatorImpl();
        // Act
        CoordinatesImpl from = new CoordinatesImpl(40.6655101,-73.89188969999998);
        CoordinatesImpl to = new CoordinatesImpl(41.43206,-81.38992);
        double dist = calc.dist(from ,to);
        // Assert
        assertEquals(741027,dist,0);
    }
}
