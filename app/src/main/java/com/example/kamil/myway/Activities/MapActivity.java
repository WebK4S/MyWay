package com.example.kamil.myway.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.example.kamil.myway.Database.DBConnection;
import com.example.kamil.myway.Map.Position;
import com.example.kamil.myway.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Kamil on 28.03.2018.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private TextView gpsInfo;
    private Location savedLocation =null;
    private Location currentLocation;
    private Position position;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date date;
    private Timer timer;
    private GoogleMap mMap;
    private DBConnection dbConnection;

    private LocationListener locationListener = new LocationListener() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationChanged(Location location) {
            if (savedLocation == null){
                savedLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderDisabled(String s) {
            position = new Position();
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            position.setLatitude(currentLocation.getLatitude());
            position.setLongitude(currentLocation.getLongitude());
            date = new Date();
            position.setDate(dateFormat.format(date).toString());
            dbConnection.open();
            dbConnection.insertPosition(position);
            dbConnection.close();
        }
    };


    //TODO locationListener

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dbConnection = new DBConnection(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET},
                    1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        setContentView(R.layout.activity_map);
        gpsInfo = findViewById(R.id.gpsInfo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //TODO GPS is turned on
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            gpsInfo.setText("GPS Enabled !!!");
        }else{
            //TODO GPS is turned off
            gpsInfo.setText("GPS Disabled");
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("I am here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.getMaxZoomLevel();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart(){
        super.onStart();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,2,locationListener);
        Timer timer = new Timer();
        LocationSaver locationSaver= new LocationSaver();
        timer.schedule(locationSaver, 0,60000);
    }

    @Override
    protected void onStop(){
        locationManager.removeUpdates(locationListener);
        super.onStop();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class LocationSaver extends TimerTask {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            Position position = new Position();
            dbConnection.open();
            if(savedLocation == null){
                savedLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            position.setLatitude(savedLocation.getLatitude());
            position.setLongitude(savedLocation.getLongitude());
            date = new Date();
            position.setDate(dateFormat.format(date).toString());
            dbConnection.insertPosition(position);
            dbConnection.close();
        }
    }

}
