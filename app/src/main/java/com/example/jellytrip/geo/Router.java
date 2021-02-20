package com.example.jellytrip.geo;

import android.util.Log;

import com.example.jellytrip.geo.types.Route;
import com.example.jellytrip.geo.utils.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Router implements RouteProvider {

    private final  String googleMapsApiKey;
    private Route currentRoute;
//    final FusedLocationProviderClient client;
    private Coordinates dest;

    public Router(String googleMapsApiKey){
        this.googleMapsApiKey = googleMapsApiKey;
//        this.client = client;
    }
    @Override
    public Route paveRoute(Coordinates from, Coordinates dest) {

        String url = getDirectionUrl(from, dest);
        String data= "";
        try {
            data = downloadUrl(url);
        }catch (IOException exp){
            Log.d("Download of route failed", exp.toString());
        }

        List<List<HashMap<String, String>>> routes = parse(data);
        ArrayList<Coordinates> steps = getSteps(routes);
        Route route = new Route(steps);
        this.currentRoute = route;

        return route;

    }

    /**
     * @return if return null, route is over
     */
    @Override
    public Coordinates getNextDot() {
        // just give next point and don`t fuck my brain
        Coordinates nextDot = currentRoute.getNext();

        return nextDot;
    }
    @Override
    public double getBearing(){
      return currentRoute.getBearing();
    }
    @Override
    public Coordinates getCurrentDot(){
        return currentRoute.getCurrent();
    }

    private ArrayList<Coordinates> getSteps(List<List<HashMap<String,String>>> routesJson){
        ArrayList<Coordinates> points = new ArrayList<Coordinates>();
        for (int i =0; i < routesJson.size(); i++){

            List<HashMap<String, String>> path = routesJson.get(i);

            for (int j =0 ; j < path.size(); j++){
                HashMap point = path.get(j);

                double lat = Double.parseDouble(point.get("lat").toString()) ;
                double lng = Double.parseDouble(point.get("lng").toString());
                Coordinates position = new CoordinatesImpl(lng, lat);

                points.add(position);
            }
        }

        return points;
    }
    private List<List<HashMap<String, String>>> parse(String jsonData){
        JSONObject jsonObject;
        List<List<HashMap<String, String>>> routes = null;

        try{
            jsonObject = new JSONObject(jsonData);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            routes = parser.parse(jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }

        return routes;
    }

    private String getDirectionUrl(Coordinates origin, Coordinates dest){
        // Origin of route
        String str_origin = "origin=" + origin.getY()+ "," + origin.getX();

        // Destination of route
        String str_dest = "destination=" + dest.getY()+ "," + dest.getX();

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        strUrl += "&key="+googleMapsApiKey;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }




}

