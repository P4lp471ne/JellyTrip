package com.example.jellytrip.geo;

public class CoordinatesImpl implements Coordinates {
    private double x;
    private double y;

    public CoordinatesImpl(double x, double y){
        this.x = x;
        this.y = y;
    }

    public static Coordinates plus(Coordinates cc, CoordinatesImpl coordinates) {
        return null;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public Coordinates minus(Coordinates coordinates) {
        double newX = x - coordinates.getX();
        double newY = y  -coordinates.getY();

        return new CoordinatesImpl(newX, newY);
    }

    @Override
    public Coordinates plus(Coordinates coordinates) {
        double newX = x + coordinates.getX();
        double newY = y  + coordinates.getY();

        return new CoordinatesImpl(newX, newY);
    }
}
