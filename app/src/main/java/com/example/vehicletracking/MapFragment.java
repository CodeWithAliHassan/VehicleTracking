package com.example.vehicletracking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_BUS_LOCATION = "bus_location";
    private GoogleMap myMap;
    private String busLocation;

    public static MapFragment newInstance(String busLocation) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUS_LOCATION, busLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_frag, container, false);

        if (getArguments() != null) {
            busLocation = getArguments().getString(ARG_BUS_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // Enable user's current location if needed
        enableMyLocation();

        // Convert bus location address to LatLng
        LatLng busLatLng = getLocationFromAddress(busLocation);

        if (busLatLng != null) {
            // Add marker for bus location
            MarkerOptions busMarker = new MarkerOptions().position(busLatLng).title("Bus Location");
            myMap.addMarker(busMarker);

            // Move camera to bus location
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(busLatLng, 15));

            // Draw route from user's location to bus location
            LatLng myLocation = getCurrentLocation();
            if (myLocation != null) {
                drawRoute(myLocation, busLatLng);
            }
        } else {
            Toast.makeText(getContext(), "Bus location not found: " + busLocation, Toast.LENGTH_SHORT).show();
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        myMap.setMyLocationEnabled(true);
    }

    private LatLng getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return null;
        }
        Geocoder geocoder = new Geocoder(requireContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName("My Address", 1);
            if (addresses.isEmpty()) {
                return null;
            }
            Address location = addresses.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private LatLng getLocationFromAddress(String locationName) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        // Set up GeoApiContext for Directions API
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key)) // Replace with your own API key
                .build();

        // Make Directions API request
        DirectionsApiRequest req = DirectionsApi.newRequest(context)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .mode(TravelMode.DRIVING); // Can change TravelMode if needed

        // Handle the Directions API response
        req.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                // Process result and draw polyline
                if (result.routes != null && result.routes.length > 0) {
                    com.google.maps.model.LatLng[] path = result.routes[0].overviewPolyline.decodePath().toArray(new com.google.maps.model.LatLng[0]);
                    PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE);
                    for (com.google.maps.model.LatLng latLng : path) {
                        options.add(new LatLng(latLng.lat, latLng.lng));
                    }
                    myMap.addPolyline(options);
                } else {
                    Toast.makeText(getContext(), "No route found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getContext(), "Failed to get directions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
