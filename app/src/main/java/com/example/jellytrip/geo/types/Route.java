package com.example.jellytrip.geo.types;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private List<Coordinates> steps;

    private Coordinates next;

    private Coordinates current;

    private Boolean isOver = false;

    public List<Coordinates> getSteps() {
        return steps;
    }


    public Route(List<Coordinates> steps) {

        this.steps = steps;

        current = steps.get(0);
        if (steps.size() > 1) {
            next = steps.get(1);
        }
    }

    public List<LatLng> getInLatLng() {
        ArrayList points = new ArrayList<LatLng>();
        for (Coordinates coor : steps) {
            points.add(coor.toLatLng());
        }
        return points;
    }

    public Coordinates getNext() {
        if(next==null){
            return null;
        }
        Coordinates nextToReturn = next;
        Integer nextIndex = steps.indexOf(next);
        Integer currIndex = steps.indexOf(current);

        current = nextToReturn;
        if (steps.size() >= nextIndex) {
            next = steps.get(nextIndex++);
        } else {
            next = null;
            isOver = true;
        }

        return nextToReturn;
    }
    public Coordinates getCurrent(){
        return current;
    }

    public double getBearing(){
        //  Calculate bearing method from two coordinates
        double lat1 = current.getY();
        double lng1 = current.getX();

        double lat2 = next.getY();
        double lng2 = next.getX();

        double dLon = (lng2-lng1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double brng = Math.toDegrees((Math.atan2(y, x)));
        brng = (360 - ((brng + 360) % 360));

        return brng;
    }

    public Boolean getIsOver(){
        return isOver;
    }
    //here will be method for azimut


}
