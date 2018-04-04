package com.disablerouting.map_base;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.disablerouting.R;
import com.disablerouting.application.AppData;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.common.PolylineDecoder;
import com.disablerouting.route_planner.model.NodeItem;
import com.disablerouting.route_planner.model.Steps;
import com.disablerouting.utils.PermissionUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public abstract class MapBaseActivity extends BaseActivityImpl implements OnFeedBackListener , MapEventsReceiver {

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
    private Marker mStartMarker = null;
    private Marker mEndMarker = null;
    private Marker mCurrentMarker = null;
    private String mStartAddress;
    private String mEndAddress;
    private static OnFeedBackListener mFeedBackListener;
    private Polyline mPreviousPolyline;
    private Marker mNodeMarker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setOnFeedBackListener(this);
        checkLocationStatus();
        addLocationCallback();
        createLocationRequest();
        initializeMap();

    }

    public void setOnFeedBackListener(OnFeedBackListener feedBackListener) {
        mFeedBackListener = feedBackListener;
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

    protected void initializeMap() {
        mMapView = findViewById(com.disablerouting.R.id.map_view);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);
        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mMapView);
        myScaleBarOverlay.setCentred(true);
        mMapView.getOverlays().add(myScaleBarOverlay);
        setProvider();

        mMapView.getOverlays().clear();

        mStartMarker = new Marker(mMapView);
        mEndMarker = new Marker(mMapView);
        mCurrentMarker = new Marker(mMapView);

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
     * @param stepsList
     */
    public void plotDataOfSourceDestination(String encodedGeoPoints, String startAdd, String endAdd, List<Steps> stepsList) {
        clearItemsFromMap();
        GeoPoint geoPointStart = null, geoPointEnd = null;
        if (encodedGeoPoints != null) {
            List<GeoPoint> geoPointArrayList = PolylineDecoder.decodePoly(encodedGeoPoints);
            addPolyLine(geoPointArrayList, stepsList);
            if (geoPointArrayList != null && geoPointArrayList.size() != 0) {
                geoPointStart = geoPointArrayList.get(0);
                geoPointEnd = geoPointArrayList.get(geoPointArrayList.size() - 1);
                mStartAddress = startAdd;
                mEndAddress = endAdd;
                addMarkers(geoPointStart, startAdd, geoPointEnd, endAdd);
            }
            if (geoPointStart != null && geoPointEnd != null) {
                BoundingBox boundingBox = new BoundingBox(geoPointStart.getLatitude(), geoPointStart.getLongitude(),
                        geoPointEnd.getLatitude(), geoPointEnd.getLongitude());
                mMapView.getController().setCenter(boundingBox.getCenter());
                mMapView.zoomToBoundingBox(boundingBox, false);
            }

        } else {
            addCurrentLocation();
        }
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mMapView.getOverlays().add(0, mapEventsOverlay);

    }

    /**
     * Add geo points to map
     *
     * @param geoPointList list of geo points
     * @param stepsList    way points index
     */
    private void addPolyLine(final List<GeoPoint> geoPointList, final List<Steps> stepsList) {
        clearItemsFromMap();
        final ArrayList<Polyline> polylineArrayList = new ArrayList<>();
        if (geoPointList.size()>1 && stepsList != null && stepsList.size()>2) {
            for (int i = 0; i < stepsList.size() ; i++) {
                int indexFirst = stepsList.get(i).getDoublesWayPoints().get(0);
                int indexLast = stepsList.get(i).getDoublesWayPoints().get(1);

                List<GeoPoint> geoPointsToSet = new ArrayList<>(geoPointList.subList(indexFirst, indexLast + 1));
                final Polyline mPolyline = new Polyline();
                mPolyline.setPoints(geoPointsToSet);
                mPolyline.setWidth(20);
                polylineArrayList.add(mPolyline);
                mPolyline.setColor(getResources().getColor(R.color.colorPrimary));

                mPolyline.setOnClickListener(new Polyline.OnClickListener() {
                    @Override
                    public boolean onClick(final Polyline polyline, MapView mapView, GeoPoint eventPos) {
                        if (mPreviousPolyline != null) {
                            mPreviousPolyline.setColor(getResources().getColor(R.color.colorPrimary));
                            mPreviousPolyline.setWidth(20);
                        }
                        polyline.setColor(getResources().getColor(R.color.colorGreen));
                        polyline.setWidth(30);
                        mPreviousPolyline = polyline;
                        mMapView.invalidate();
                        showFeedbackDialog(eventPos.getLongitude() ,eventPos.getLatitude());
                        return false;
                    }
                });

            }
            if (mMapView != null) {
                mMapView.getOverlayManager().addAll(polylineArrayList);
            }

        }
    }

    /**
     * Add current location
     */
    private void addCurrentLocation() {
        if (mMapView != null) {
            GeoPoint currentGeoPoints = new GeoPoint(mLatitude, mLongitude);
            mCurrentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mCurrentMarker.setPosition(currentGeoPoints);
            mMapView.getOverlays().add(mCurrentMarker);
            mCurrentMarker.setIcon(getResources().getDrawable(R.drawable.ic_current_loc));
            mCurrentMarker.setTitle("Your Current location");
            MapController myMapController = (MapController) mMapView.getController();
            myMapController.setZoom(12);
            myMapController.setCenter(currentGeoPoints);
        }

    }

    /**
     * Add markers to map
     *
     * @param start    start geo points
     * @param startAdd start add
     * @param end      end geo points
     * @param endAdd   end address
     */
    private void addMarkers(GeoPoint start, String startAdd, GeoPoint end, String endAdd) {

        if (mMapView != null) {
            MapController myMapController = (MapController) mMapView.getController();
            myMapController.setZoom(10);
            myMapController.setCenter(start);

            if (start != null) {
                GeoPoint startPoint = new GeoPoint(start.getLatitude(), start.getLongitude());
                mStartMarker.setPosition(startPoint);
                mStartMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(mStartMarker);
                mStartMarker.setIcon(getResources().getDrawable(R.drawable.ic_location_source));
                mStartMarker.setTitle(startAdd);
            }
            if (end != null) {
                GeoPoint endPoint = new GeoPoint(end.getLatitude(), end.getLongitude());
                mEndMarker.setPosition(endPoint);
                mEndMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(mEndMarker);
                mEndMarker.setIcon(getResources().getDrawable(R.drawable.ic_location_destination));
                mEndMarker.setTitle(endAdd);
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
        if ((ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
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
    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);
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
                        Log.e("latlngfromupdate", String.valueOf(mLatitude + mLongitude));
                        //mMapView.getOverlays().clear();
                        //mMapView.invalidate();
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

    /**
     * Show setting dialog
     */
    protected void openSettingDialog() {
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
            addLocationCallback();
            createLocationRequest();
            initializeMap();
        }
    }

    /**
     * Sho feed back dialog
     * @param longitude double longitude
     * @param latitude double latitude
     */
    private void showFeedbackDialog(final double longitude, final double latitude) {
        String description = String.valueOf((latitude + " " + longitude));
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.feedback_pop_up, null);
        TextView textViewDescription = (TextView) customView.findViewById(R.id.txv_description);
        Button btnFeedback = (Button) customView.findViewById(R.id.btn_feedback);
        Button btnCancel = (Button) customView.findViewById(R.id.btn_cancel);
        textViewDescription.setText(description);
        builder.setView(customView);

        final Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFeedBackListener.onFeedBackClick(longitude,latitude);
                alertDialog.dismiss();
            }
        });
    }

    /**
     * Clear Items from map
     */
    public void clearItemsFromMap() {
        mMapView.getOverlays().clear();
        mMapView.invalidate();
    }

    public void plotDataOfNodes(List<NodeItem> nodeItemList) {
       // clearItemsFromMap();
        for (NodeItem nodeItem : nodeItemList){
            switch (nodeItem.getNodeType().getIdentifier()){
                case AppConstant.publicTramStop:
                    if(nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicTramStop)){
                        GeoPoint geoPoint = new GeoPoint(nodeItem.getLatitude(),
                                nodeItem.getLongitude());
                        addMarkerNode(geoPoint,nodeItem.getNodeType().getIdentifier(),nodeItem.getWheelChair().toUpperCase());
                    }
                    break;
                case AppConstant.publicToilets:
                    if(nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicToilets)){
                        GeoPoint geoPoint = new GeoPoint(nodeItem.getLatitude(),
                                nodeItem.getLongitude());
                        addMarkerNode(geoPoint,nodeItem.getNodeType().getIdentifier(),nodeItem.getWheelChair().toUpperCase());
                    }
                    break;
                case AppConstant.publicBusStop:
                    if(nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicBusStop)){
                        GeoPoint geoPoint = new GeoPoint(nodeItem.getLatitude(),
                                nodeItem.getLongitude());
                        addMarkerNode(geoPoint,nodeItem.getNodeType().getIdentifier(),nodeItem.getWheelChair().toUpperCase());
                    }
                    break;
            }

        }

    }

    private void addMarkerNode(GeoPoint geoPoint, String category, String wheelChairAccessible){
        mNodeMarker = new Marker(mMapView);
        GeoPoint nodePoints = new GeoPoint(geoPoint.getLatitude(), geoPoint.getLongitude());
        switch (category){
            case AppConstant.publicTramStop:
                mNodeMarker.setPosition(nodePoints);
                mNodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(mNodeMarker);
                mNodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_train));
                mNodeMarker.setTitle(getResources().getString(R.string.tram_stop_title));
                mNodeMarker.setSnippet(getString(R.string.wheelchair_accessible) + wheelChairAccessible);
                break;
            case AppConstant.publicToilets:
                mNodeMarker.setPosition(nodePoints);
                mNodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(mNodeMarker);
                mNodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_toilet));
                mNodeMarker.setTitle(getResources().getString(R.string.toilets_title));
                mNodeMarker.setSnippet(getString(R.string.wheelchair_accessible) + wheelChairAccessible);
                break;
            case AppConstant.publicBusStop:
                mNodeMarker.setPosition(nodePoints);
                mNodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(mNodeMarker);
                mNodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_bus));
                mNodeMarker.setTitle(getResources().getString(R.string.bus_stop_title));
                mNodeMarker.setSnippet(getString(R.string.wheelchair_accessible) + wheelChairAccessible);
                break;

        }
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mMapView.getOverlays().add(0, mapEventsOverlay);

    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(mMapView);
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }
}



