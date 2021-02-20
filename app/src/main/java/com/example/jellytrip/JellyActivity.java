package com.example.jellytrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.jellytrip.geo.Calculator;
import com.example.jellytrip.geo.Coordinates;
import com.example.jellytrip.tasks.BaseTask;
import com.example.jellytrip.tasks.TaskRunner;
import com.example.jellytrip.tasks.iOnDataFetched;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;


class JellyActivity extends AppCompatActivity implements OnMapReadyCallback, iOnDataFetched {

    private static final String TAG = JellyActivity.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location currentLocation;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // [END maps_current_place_state_keys]


    private LatLng goToPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main);


        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize the AutocompleteFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO add reaction
                Log.i(TAG, "Place : "+place.getName()+ " Coors "+place.getLatLng() );

                setRoute(place.getLatLng());
            }

            @Override
            public void onError(@NonNull Status status) {
                // Todo add error handling
                Log.e(TAG, "Error : "+ status);
            }
        });

    }

   // @Override
 //   public boolean onCreateOptionsMenu(Menu menu) {
  //      getMenuInflater().inflate(R.menu.menu_main, menu);
 //       return true;
 //   }

 /*   @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_exit){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            System.exit(1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, currentLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;

        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        final JellyActivity normalActivity = this;

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // user select point
                setRoute(latLng);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                locationPermissionGranted = true;
            }
        }

        updateLocationUI();
    }
    private void setRoute(LatLng destination){
        goToPoint = destination;
        if (goToPoint != null) {
            map.clear();
        }
        MarkerOptions options = new MarkerOptions();
        // setting posssition of the marker
        options.position(goToPoint);

        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        map.addMarker(options);

        // Todo current location can be null
        // We need this to deal with our own transactions
        if(currentLocation != null) {
            LatLng currPoint = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Coordinates currentCoor = new Coordinates(currPoint);
            Coordinates goToCoor = new Coordinates(goToPoint);

            TaskRunner runner = new TaskRunner();

            runner.executeAsync(new RouteTask(this, currentCoor, goToCoor));
        }else{
            Log.e(TAG, "Current location is NULL !!!");
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_DENIED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setAllGesturesEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(false);
                currentLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            currentLocation = task.getResult();
                            if (currentLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(currentLocation.getLatitude(),
                                                currentLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

//                Locator locator = new Locator(fusedLocationProviderClient, this);
//                Coordinates locationCoors = null;
//                try {
//                    locationCoors = locator.getCurrentLocation();
//                } catch (SecurityException e) {
//                    Log.d(TAG, "Current location is null. Using defaults.");
//                    Log.e(TAG, "Exception: %s", e);
//                } catch (Exception e) {
//                    Log.e(TAG, "Exception: %s", e);
//                }
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                        new LatLng(locationCoors.getY(),
//                                                locationCoors.getX()), DEFAULT_ZOOM));


            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void showProgressBar() {
        // do nothing
    }

    @Override
    public void hideProgressBar() {
        // again do nothing
    }

    @Override
    public void setDataInPageWithResult(Object result) {
        Route route = (Route) result;
        List<LatLng> steps = route.getInLatLng();

        PolylineOptions lineOptions = new PolylineOptions();
        MarkerOptions markerOptions = new MarkerOptions();

        lineOptions.addAll(steps);
        lineOptions.width(12);
        lineOptions.color(Color.RED);
        lineOptions.geodesic(true);

        map.addPolyline(lineOptions);

    }

    private class RouteTask extends BaseTask {
        private final iOnDataFetched listener;
        private final Coordinates from;
        private final Coordinates dest;

        public RouteTask(iOnDataFetched onDataFetched, Coordinates from, Coordinates dest) {
            listener = onDataFetched;
            this.from = from;
            this.dest = dest;
        }

        @Override
        public Route call() throws Exception {
            Calculator route = new Router(getString(R.string.google_maps_key));
            Route result = route.paveRoute(from, dest);
            return result;
        }

        @Override
        public void setUiForLoading() {
            listener.showProgressBar();
        }

        @Override
        public void setDataAfterLoading(Object result) {

            listener.setDataInPageWithResult(result);
            listener.hideProgressBar();
        }
    }
}