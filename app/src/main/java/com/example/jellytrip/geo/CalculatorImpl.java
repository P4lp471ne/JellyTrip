package com.example.jellytrip.geo;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class CalculatorImpl implements Calculator {

    private String apiKey  = "AIzaSyB7t7US8iz5N7crMWiLVG4eE7a4R07aTDA";

    @Override
    public double dist(Coordinates from, Coordinates to) throws IOException {
        double dist = this.getDistance(from, to);
        return dist;
    }

    private double getDistance(Coordinates from, Coordinates to) throws IOException {
        String url  = getRequestUrl(from, to);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        String bodyString =  response.body().string();

        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(bodyString);
        JsonNode actualObj = mapper.readTree(parser);

        String distance  = actualObj.get("rows").get(0).get("elements").get(0).get("distance").get("value").toString();

        return Double.parseDouble(distance);
    }

    private String getRequestUrl(Coordinates origin, Coordinates dest){
        // Origin of route
        String str_origin = "origins=" + origin.getX()+ "," + origin.getY();

        // Destination of route
        String str_dest = "destinations=" + dest.getX()+ "," + dest.getY();

        String str_api_key = "key="+apiKey;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest ;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric" + "&" + str_origin + "&" + str_dest + "&" + str_api_key;

        return url;
    }


}
