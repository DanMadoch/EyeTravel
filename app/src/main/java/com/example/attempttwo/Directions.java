package com.example.attempttwo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.Maneuver;
import com.here.sdk.routing.ManeuverAction;
import com.here.sdk.routing.PedestrianOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Section;
import com.here.sdk.routing.Waypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Directions extends Activity {
    private RoutingEngine routingEngine;
    private List<String> maneuverActions;
    private int navigationIndex = 0;
    private double sourceLat, sourceLong;
    private static Context context;
    private static boolean isGpsEnabled, isNetworkLocationEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions_layout);

        Directions.context = getApplicationContext();

        Intent intent = getIntent();
        String inputAddress = intent.getStringExtra("address");

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            TextView editText = findViewById(R.id.textViewDirections);
            String newText = "error";
            editText.setText(newText);
            return;
        }

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }
        /*Route Calculation*/
        LatLng endCoordinates = getLocationFromAddress(inputAddress);

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        Waypoint startWaypoint = new Waypoint(new GeoCoordinates(getLocationWithCheckNetworkAndGPS().getLatitude(), getLocationWithCheckNetworkAndGPS().getLongitude()));
        Waypoint destinationWaypoint = new Waypoint(new GeoCoordinates(endCoordinates.latitude,endCoordinates.longitude));



        List<Waypoint> waypoints =
                new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

        routingEngine.calculateRoute(
                waypoints,
                new PedestrianOptions(),
                new CalculateRouteCallback() {
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                        if (routingError == null) {
                            Route route = routes.get(0);
                            //showRouteDetails(route);
                            //showRouteOnMap(route);
                            /*Maneuver Instructions*/
                            List<Section> sections = route.getSections();
                            for (Section section : sections) {
                                // Section in this loop represents a single section
                                // that can be used to access maneuver instructions
                                getRouting(section);
                            }
                            TextView editText = findViewById(R.id.textViewDirections);
                            String newText = maneuverActions.get(navigationIndex).toString();
                            editText.setText(newText);
                        }

                    }
                });

        final Button button2 = findViewById(R.id.buttonNext);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigationIndex++;
                TextView editText = findViewById(R.id.textViewDirections);
                String newText = maneuverActions.get(navigationIndex).toString();
                editText.setText(newText);
            }
        });
        final Button button3 = findViewById(R.id.buttonPrevious);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigationIndex--;
                TextView editText = findViewById(R.id.textViewDirections);
                String newText = maneuverActions.get(navigationIndex).toString();
                editText.setText(newText);
            }
        });

    }
    private void getRouting(Section section) {
        List<Maneuver> maneuverInstructions = section.getManeuvers();
        List<String> maneuvers = new ArrayList<String>();
        for (Maneuver maneuverInstruction : maneuverInstructions) {
            ManeuverAction maneuverAction = maneuverInstruction.getAction();
            GeoCoordinates maneuverLocation = maneuverInstruction.getCoordinates();

            String maneuverInfo = maneuverInstruction.getText();
            maneuvers.add(maneuverInfo);

        }
        maneuverActions = maneuvers;
    }
    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    private static Location getLocationWithCheckNetworkAndGPS() {
        LocationManager lm = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location networkLoacation = null, gpsLocation = null, finalLoc = null;
        if (isGpsEnabled)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isNetworkLocationEnabled)
            networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null && networkLoacation != null) {

            //smaller the number more accurate result will
            if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy())
                return finalLoc = networkLoacation;
            else
                return finalLoc = gpsLocation;

        } else {

            if (gpsLocation != null) {
                return finalLoc = gpsLocation;
            } else if (networkLoacation != null) {
                return finalLoc = networkLoacation;
            }
        }

        return finalLoc;
    }
}


