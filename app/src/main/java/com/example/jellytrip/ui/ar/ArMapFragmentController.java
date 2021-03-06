package com.example.jellytrip.ui.ar;

import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jellytrip.R;
import com.example.jellytrip.geo.Calculator;
import com.example.jellytrip.geo.Coordinates;
import com.example.jellytrip.geo.CoordinatesImpl;
import com.example.jellytrip.geo.LocationProvider;
import com.example.jellytrip.geo.Locator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.IOException;
import java.util.List;


public class ArMapFragmentController extends Thread {
    private final int len = 30;
    private Session session;
    private Camera camera;
    private ArSceneView view;
    private LocationProvider locationProvider;
    private ModelRenderable lampPostRenderable;
    private ModelRenderable pointer;
    private ArFragment fragment;
    private Anchor anchor;
    private Calculator calculator;

    public ArMapFragmentController(ArFragment fragment) {
        this.fragment = fragment;
        view = fragment.getArSceneView();

        session = view.getSession();
//        camera = view.getArFrame().getCamera();

        makeImages();
        locationProvider = new Locator(fragment.getContext());
    }

    @Override
    public void run() {
        while (true) {
            try {
                update();
                sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Anchor getAnchor(Coordinates coordinates) {
        Pose pose = new Pose(new float[]{(float) coordinates.getX(), (float) coordinates.getY(), 0},
                new float[]{0, 0, 0});
        return session.createAnchor(pose);
    }

    private void makeImages() {
        ModelRenderable.builder()
                .setSource(fragment.getContext(), Uri.parse("свинтус.sfb"))
                .build()
                .thenAccept(renderable -> lampPostRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast toast = Toast.makeText(fragment.getContext(),
                            "Unable to load andy renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });
        ModelRenderable.builder()
                .setSource(fragment.getContext(), Uri.parse("pointer.sfb"))
                .build()
                .thenAccept(renderable -> pointer = renderable)
                .exceptionally(throwable -> {
                    Toast toast = Toast.makeText(fragment.getContext(),
                            "Unable to load andy renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });
    }

    private void update() throws Exception {
        double approach = getApproach();
        double len = dist(locationProvider.getCurrentLocation(), getDest());

        putObject(approach, (int) len);

//        if (this.len > len){
//            double direction = getDirection(locationProvider.getCurrentLocation(),
//                    routeProvider.getCurrentDot(), routeProvider.getNextDot());
//            compas.setRotationX((float) ((float) direction*(1-len/this.len)));
//            putPointer(approach, len, direction);
//            return;
//        }
//        compas.setRotationX(0);
    }


    private Coordinates getDest() {
        return locationProvider.getCarLocation();
    }

//    private double getDirection(Coordinates currentLocation, Coordinates currentDot, Coordinates nextDot) {
//        Coordinates v1 = Coordinates.minus(currentDot, currentLocation);
//        Coordinates v2 = Coordinates.minus(nextDot, currentDot);
//        return Math.acos(-(v1.getX()*v2.getX() + v1.getY()*v2.getY())/
//                (Math.sqrt((v1.getX()*v1.getX() + v1.getY()*v1.getY()) * (v2.getX()*v2.getX() + v2.getY()*v2.getY()))));
//    }

//    private void putPointer(double approach, double dist, double direction) {
//        Coordinates cc = getCameraFlatCoors();
//        Anchor anchor = getAnchor(Coordinates.plus(cc, new Coordinates(Math.sin(approach) * dist,
//                Math.cos(approach) * dist)), direction);
//
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        anchorNode.setParent(fragment.getArSceneView().getScene());
//        TransformableNode lamp = new TransformableNode(fragment.getTransformationSystem());
//        lamp.setParent(anchorNode);
//        lamp.setRenderable(pointer);
//        lamp.select();
//    }

    private double dist(Coordinates currentLocation, Coordinates currentDot) throws IOException {
        return calculator.dist(currentLocation, currentDot);
    }

    private Anchor putObject(double approach, int dist) {
        Coordinates cc = getCameraFlatCoors();
        anchor.detach();
        anchor = getAnchor(CoordinatesImpl.plus(cc, new CoordinatesImpl(Math.sin(approach) * dist,
                Math.cos(approach) * dist)));
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(fragment.getArSceneView().getScene());
        TransformableNode lamp = new TransformableNode(fragment.getTransformationSystem());
        lamp.setParent(anchorNode);
        lamp.setRenderable(lampPostRenderable);
        lamp.select();
        return anchor;
    }

    private double getApproach() throws Exception {
        Coordinates currentLocation = locationProvider.getCurrentLocation();
        double azimuth = locationProvider.getAzimuth();

        Coordinates approachvect = Coordinates.minus(getDest(), currentLocation);
        return Math.atan(approachvect.getY() / approachvect.getX()) - azimuth + camera.getPose().qz();
    }

    private Coordinates getCameraFlatCoors() {
        camera = view.getArFrame().getCamera();
        Pose pose = camera.getPose();
        return new CoordinatesImpl(pose.tx(), pose.ty());
    }

}

