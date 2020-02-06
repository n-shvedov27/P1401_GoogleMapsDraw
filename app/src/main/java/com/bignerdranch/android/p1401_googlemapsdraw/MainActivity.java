package com.bignerdranch.android.p1401_googlemapsdraw;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap map;
    Circle mCircle;

    LocationManager mLocationManager;

    private android.location.LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onProviderEnabled(String provider) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocation = mLocationManager.getLastKnownLocation(provider);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                },
                1
        );

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(locationListener);
    }

    public void onClickTest(View view) {
        LatLng circleCenter = mCircle.getCenter();
        float[] distance = new float[2];

        Location.distanceBetween(
                circleCenter.latitude,
                circleCenter.longitude,
                mLocation.getLatitude(),
                mLocation.getLongitude(),
                distance
        );

        if (distance[0] > mCircle.getRadius()) {
            Toast.makeText(
                    getApplicationContext(),
                    "Вы вне области",
                    Toast.LENGTH_SHORT
            ).show();

        } else if (distance[0] < mCircle.getRadius()) {
            Toast.makeText(
                    getApplicationContext(),
                    "Вы в области",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void addNewCircle(View view) {
        CircleOptions circleOptions = new CircleOptions()
                .fillColor(Color.parseColor("#50ff9999"))
                .center(new LatLng(58, 61))
                .radius(200000)
                .strokeWidth(5);

        mCircle = map.addCircle(circleOptions);
    }

    public void moveCircle(View view) {
        mCircle.setCenter(new LatLng(50, 61));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
    }
}