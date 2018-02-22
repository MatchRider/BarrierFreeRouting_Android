package com.disablerouting.map_base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.disablerouting.R;
import com.disablerouting.application.AppData;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.common.PolylineDecoder;
import com.disablerouting.utils.PermissionUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

public abstract class MapBaseActivity extends BaseActivityImpl {

    private MapView mMapView = null;
    private MyLocationNewOverlay mLocationOverlay;

    final String[] locationPermissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE};

    private double mLatitude = 0;
    private double mLongitude = 0;

    private static final int REQUEST_CHECK_SETTINGS = 500;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    public LatLng mCurrentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationStatus();
        createLocationRequest();
        addLocationCallback();
        initializeMap();

    }

    /**
     * Check location services status
     */
    protected void checkLocationStatus() {
        if (!PermissionUtils.isPermissionAllowed(this, android.Manifest.permission_group.LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, locationPermissions, AppConstant.REQUEST_CODE);
            }
        }
    }

    private void initializeMap() {
        mMapView = findViewById(com.disablerouting.R.id.map_view);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);

        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mMapView.getOverlays().add(myScaleBarOverlay);
        setProvider();
    }

    private void setProvider() {
        if (mMapView != null) {
            GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(getApplicationContext());
            gpsMyLocationProvider.addLocationSource(LocationManager.GPS_PROVIDER);
            gpsMyLocationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);

            mLocationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, mMapView);
            mLocationOverlay.enableMyLocation();
            mMapView.getOverlays().add(mLocationOverlay);
        }
    }

    /**
     * Add path between two points
     *
     * @param encodedGeoPoints plot encoded points
     */
    public void plotDataOfSourceDestination(String encodedGeoPoints) {
        if (encodedGeoPoints != null) {
            List<GeoPoint> geoPointArrayList = PolylineDecoder.decodePoly(encodedGeoPoints);
            addPolyLine(geoPointArrayList);
            GeoPoint geoPointStart = null, geoPointEnd = null;
            if (geoPointArrayList != null && geoPointArrayList.size() != 0) {
                geoPointStart = geoPointArrayList.get(0);
                geoPointEnd = geoPointArrayList.get(geoPointArrayList.size() - 1);
                addMarkers(geoPointStart, geoPointEnd);
            }
        } else {
            addCurrentLocation();
        }
    }

    /**
     * Add geo points to map
     *
     * @param geoPointList list of geo points
     */
    private void addPolyLine(List<GeoPoint> geoPointList) {
        //add your points here
        Polyline line = new Polyline();   //see note below!
        line.setPoints(geoPointList);
        line.setColor(getResources().getColor(R.color.colorPrimary));
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        if (mMapView != null) {
            mMapView.getOverlayManager().add(line);
        }

    }

    /**
     * Add cureent location
     */
    private void addCurrentLocation() {
        if (mMapView != null) {
            GeoPoint currentGeoPoints = new GeoPoint(mLatitude, mLongitude);
            Marker currentMarker = new Marker(mMapView);
            currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            currentMarker.setPosition(currentGeoPoints);
            currentMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
            currentMarker.setTitle("Current");

            MapController myMapController = (MapController) mMapView.getController();
            myMapController.setZoom(15);
            myMapController.setCenter(currentGeoPoints);
        }

    }

    /**
     * Add Markers between source to destination
     *
     * @param start start geo point
     * @param end   end geo point
     */
    private void addMarkers(GeoPoint start, GeoPoint end) {
        if (mMapView != null) {
            MapController myMapController = (MapController) mMapView.getController();
            myMapController.setZoom(14);
            myMapController.setCenter(start);

            if (start != null) {
                GeoPoint startPoint = new GeoPoint(start.getLatitude(), start.getLongitude());
                Marker startMarker = new Marker(mMapView);
                startMarker.setPosition(startPoint);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(startMarker);
                startMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                startMarker.setTitle("Start point");
            }
            if (end != null) {
                GeoPoint endPoint = new GeoPoint(end.getLatitude(), end.getLongitude());
                Marker endMarker = new Marker(mMapView);
                endMarker.setPosition(endPoint);
                endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(endMarker);
                endMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                endMarker.setTitle("End point");
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    protected void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Creating location request to get last known or latest location of user
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MapBaseActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won'ic_time_bid show the dialog.
                        break;
                }
            }
        });

    }

    /**
     * method to add location updates
     */
    private void addLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (mMapView != null) {
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();
                        plotDataOfSourceDestination(null);
                        onUpdateLocation(location);
                        AppData.getNewInstance().setCurrentLoc(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }
            }
        };
    }

    protected abstract int getView();

    protected abstract void onUpdateLocation(Location location);

    /**
     * Open setting activity
     */
    protected void startSettingActivity() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, AppConstant.SETTING_REQUEST_CODE);
    }

    /**
     * Result when user give permission or not
     *
     * @param requestCode  Request code
     * @param permissions  Type of permission
     * @param grantResults Results of permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstant.REQUEST_CODE: {
                for (String permission : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        finish();
                        break;
                    } else {
                        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                            Log.e("allowed", permission);
                        } else {
                            openSettingDialog();
                            break;
                        }
                    }
                }
            }
        }
    }

    protected void openSettingDialog() {
        //Build the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_dialog_message_enable_location);
        builder.setMessage(R.string.message_gps);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSettingActivity();
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.SETTING_REQUEST_CODE) {
            checkLocationStatus();
        }
    }


}
