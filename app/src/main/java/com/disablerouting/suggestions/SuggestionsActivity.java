package com.disablerouting.suggestions;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.google.android.gms.location.LocationListener;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class SuggestionsActivity extends BaseActivityImpl implements LocationListener{

    MapView mapView = null;
    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    MyLocationNewOverlay locationNewOverlay;
    private MapController myMapController;
    ArrayList<OverlayItem> anotherOverlayItemArray;
    GpsMyLocationProvider mGpsMyLocationProvider;
    LocationManager  mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_planner);
        checkPermissionsState();
    }

    private void checkPermissionsState() {
        int internetPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int networkStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE);

        int writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE);

        if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                networkStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                wifiStatePermissionCheck == PackageManager.PERMISSION_GRANTED) {

            setupMap();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE},
                    MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    private void setupMap() {
        mapView = findViewById(R.id.map_view);
        initMyLocation();

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15); //set initial zoom-level, depends on your need


        myMapController = (MapController) mapView.getController();
        myMapController.setZoom(9);

/*
        double latitude = locationNewOverlay.getMyLocation().getLatitude();
        double longitude = locationNewOverlay.getMyLocation().getLongitude();
*/
        double lat = 28.613333;
        double lng = 77.208333;
//
        GeoPoint startPoint = new GeoPoint(lat,lng);
        myMapController.setCenter(startPoint);

        /*//--- Create Another Overlay for multi marker
        anotherOverlayItemArray = new ArrayList<>();
        anotherOverlayItemArray.add(new OverlayItem(
                "0, 0", "0, 0", new GeoPoint(0, 0)));
        anotherOverlayItemArray.add(new OverlayItem(
                "US", "US", new GeoPoint(38.883333, -77.016667)));
        anotherOverlayItemArray.add(new OverlayItem(
                "China", "China", new GeoPoint(39.916667, 116.383333)));
        anotherOverlayItemArray.add(new OverlayItem(
                "United Kingdom", "United Kingdom", new GeoPoint(51.5, -0.116667)));
        anotherOverlayItemArray.add(new OverlayItem(
                "Germany", "Germany", new GeoPoint(52.516667, 13.383333)));
        anotherOverlayItemArray.add(new OverlayItem(
                "Korea", "Korea", new GeoPoint(38.316667, 127.233333)));
        anotherOverlayItemArray.add(new OverlayItem(
                "India", "India", new GeoPoint(28.613333, 77.208333)));
*/
/*        ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay
                = new ItemizedIconOverlay<OverlayItem>(
                this, anotherOverlayItemArray, null);
        mapView.getOverlays().add(anotherItemizedIconOverlay);
        //---*/

        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mapView);
        mapView.getOverlays().add(myScaleBarOverlay);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean somePermissionWasDenied = false;
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            somePermissionWasDenied = true;
                        }
                    }
                    if (somePermissionWasDenied) {
                        Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                        setupMap();
                    }
                } else {
                    Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }



    public void initMyLocation(){
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        assert mLocationManager != null;
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            //Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Something.");
            builder.setMessage("activate GPS.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }else {
            setProvider();
        }


    }

    private void setProvider() {
        mGpsMyLocationProvider = new GpsMyLocationProvider(getApplicationContext());
        mGpsMyLocationProvider.addLocationSource(LocationManager.GPS_PROVIDER);
        mGpsMyLocationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);

        locationNewOverlay = new MyLocationNewOverlay(mGpsMyLocationProvider, mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(locationNewOverlay);
    }


    @Override
    public void onLocationChanged(Location location) {
        myMapController.setCenter(new GeoPoint(location.getLatitude(),location.getLongitude()));
    }
}
