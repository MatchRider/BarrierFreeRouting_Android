package com.disablerouting.map_base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.disablerouting.R;
import com.disablerouting.application.AppData;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.model.ListWayData;
import com.disablerouting.curd_operations.model.NodeReference;
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
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
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

public abstract class MapBaseActivity extends BaseActivityImpl implements OnFeedBackListener, MapEventsReceiver  {

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
    private Marker mMidMarker = null;
    private Marker mRunningMarker = null;
    private Marker mCurrentMarker = null;
    private static OnFeedBackListener mFeedBackListener;
    private Polyline mPreviousPolyline;
    private boolean mShowFeedbackDialog;
    private ArrayList<Polyline> mPolylineArrayList = new ArrayList<>();
    private ArrayList<Polyline> mPolylineArrayListViaMidPoints = new ArrayList<>();
    protected Handler UI_HANDLER = new Handler();
    private int[] color = {R.color.colorTextGray, R.color.colorGreen,
            R.color.colorRed, android.R.color.holo_blue_bright};
    private int colorIndex = 0;
    private int previousColor = 0;
    private AlertDialog mAlertDialogEnhance;
    private Marker mPreviousNodeMarker;


    //Runnable marker data
    //private int mPolylineIndex = -1;
    //private int mGeoPointIndex = -1;

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

    /**
     * Listener for Map
     *
     * @param feedBackListener Feed back Listener
     */
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

    /**
     * Map Initialize
     */
    protected void initializeMap() {
        mMapView = findViewById(com.disablerouting.R.id.map_view);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);

        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mMapView);
        myScaleBarOverlay.setCentred(true);
        mMapView.getOverlays().add(myScaleBarOverlay);
        setProvider();

        mMapView.getOverlays().clear();
        mStartMarker = new Marker(mMapView);
        mEndMarker = new Marker(mMapView);
        mMidMarker = new Marker(mMapView);
        mRunningMarker = new Marker(mMapView);
        mCurrentMarker = new Marker(mMapView);

    }

    /**
     * Set Provider
     */
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

    /***
     * Plot source to destination
     * @param geoPointList list of geo points
     * @param startAdd start address string
     * @param endAdd end address string
     * @param stepsList step list ways points
     * @param showFeedbackDialog show dialog on click polyline
     */
    public void plotDataOfSourceDestination(List<List<Double>> geoPointList, String startAdd, String endAdd, List<Steps> stepsList, boolean showFeedbackDialog) {
        GeoPoint geoPointStart = null, geoPointEnd = null;
        mShowFeedbackDialog = showFeedbackDialog;
        if (geoPointList != null) {
            //List<GeoPoint> geoPointArrayList = PolylineDecoder.decodePoly(encodedGeoPoints);
            List<GeoPoint> geoPointArrayList = new ArrayList<>();
            for (int i = 0; i < geoPointList.size(); i++) {
                GeoPoint geoPoint = new GeoPoint(Double.parseDouble(geoPointList.get(i).get(1).toString()),
                        Double.parseDouble(geoPointList.get(i).get(0).toString()));
                geoPointArrayList.add(geoPoint);
            }
            addPolyLine(geoPointArrayList, stepsList);
            if (geoPointArrayList.size() != 0) {
                geoPointStart = geoPointArrayList.get(0);
                geoPointEnd = geoPointArrayList.get(geoPointArrayList.size() - 1);
                addMarkers(geoPointStart, startAdd, geoPointEnd, endAdd);
            }
            setBoundingBox(geoPointStart, geoPointEnd);

        } else {
            addCurrentLocation();
        }
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mMapView.getOverlays().add(0, mapEventsOverlay);

    }


    /**
     * Set bounding box between source and destinations
     *
     * @param geoPointStart geo point start
     * @param geoPointEnd   geo point end.
     */
    public void setBoundingBox(GeoPoint geoPointStart, GeoPoint geoPointEnd) {
        if (geoPointStart != null && geoPointEnd != null) {
            BoundingBox boundingBox = new BoundingBox(geoPointStart.getLatitude(), geoPointStart.getLongitude(),
                    geoPointEnd.getLatitude(), geoPointEnd.getLongitude());
            mMapView.getController().setCenter(boundingBox.getCenter());
            mMapView.zoomToBoundingBox(boundingBox, false);
        }
    }

    /**
     * Add geo points to map
     *
     * @param geoPointList list of geo points
     * @param stepsList    way points index
     */
    private void addPolyLine(final List<GeoPoint> geoPointList, final List<Steps> stepsList) {
        //resetRunningMarker();
        mPolylineArrayList.clear();
        if (geoPointList.size() > 1 && stepsList != null && stepsList.size() > 2) {
            for (int i = 0; i < stepsList.size(); i++) {
                int indexFirst = stepsList.get(i).getDoublesWayPoints().get(0);
                int indexLast = stepsList.get(i).getDoublesWayPoints().get(1);

                List<GeoPoint> geoPointsToSet = new ArrayList<>(geoPointList.subList(indexFirst, indexLast + 1));
                final Polyline mPolyline = new Polyline();
                mPolyline.setPoints(geoPointsToSet);
                mPolyline.setWidth(20);
                mPolylineArrayList.add(mPolyline);
                mPolylineArrayListViaMidPoints.add(mPolyline);
                mPolyline.setColor(getResources().getColor(R.color.colorPrimary));

                if (mShowFeedbackDialog) {
                    mPolyline.setOnClickListener(new Polyline.OnClickListener() {
                        @Override
                        public boolean onClick(final Polyline polyline, MapView mapView, GeoPoint eventPos) {
                            // updatePolylineUI(polyline);
                            // showFeedbackDialog(eventPos.getLongitude(), eventPos.getLatitude());
                            return false;
                        }
                    });
                }

            }
            if (mMapView != null) {
                mMapView.getOverlayManager().addAll(mPolylineArrayList);
            }

        }
    }

    private void updatePolylineUI(Polyline polyline) {

        if (mPreviousPolyline != null) {
            mPreviousPolyline.setColor(getResources().getColor(R.color.colorPrimary));
            mPreviousPolyline.setWidth(20);
        }
        polyline.setColor(getResources().getColor(R.color.colorBlack));
        polyline.setWidth(30);
        mPreviousPolyline = polyline;
        mMapView.invalidate();
    }

    /**
     * Update Polyline of ways
     *
     * @param polyline polyline
     * @param valid    valid data or not
     */
    private void updatePolylineUIWays(Polyline polyline, boolean valid) {
        if (mPreviousPolyline != null) {
            if (valid) {
                mPreviousPolyline.setColor(getResources().getColor(R.color.colorGreen));
            } else {
                mPreviousPolyline.setColor(previousColor);
            }
            mPreviousPolyline.setWidth(10);
        }
        previousColor = polyline.getColor();
        polyline.setColor(getResources().getColor(R.color.colorBrown));
        polyline.setWidth(50);
        mPreviousPolyline = polyline;
        mMapView.invalidate();
    }

    private void updateNodeUI(Marker marker, boolean valid) {

        if (mPreviousNodeMarker != null) {
            if (valid) {
                mPreviousNodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_pin_green));
            } else {
                mPreviousNodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_pin_pink));

            }
        }
        marker.setIcon(getResources().getDrawable(R.drawable.ic_pin_green));
        mPreviousNodeMarker = marker;
        mMapView.invalidate();
    }

    /**
     * Add current location
     */
    public void addCurrentLocation() {
        if (mMapView != null) {
            GeoPoint currentGeoPoints = new GeoPoint(mLatitude, mLongitude); //49.3988,8.6724
            mCurrentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mCurrentMarker.setPosition(currentGeoPoints);
            mMapView.getOverlays().add(mCurrentMarker);
            mCurrentMarker.setIcon(getResources().getDrawable(R.drawable.ic_current_loc));
            mCurrentMarker.setTitle("Your Current location");
            MapController myMapController = (MapController) mMapView.getController();
            myMapController.setZoom(14);
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
            myMapController.setZoom(14);
            myMapController.setCenter(start);

            if (start != null) {
                GeoPoint startPoint = new GeoPoint(start.getLatitude(), start.getLongitude());
                mStartMarker.setPosition(startPoint);
                mStartMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(mStartMarker);
                mStartMarker.setIcon(getResources().getDrawable(R.drawable.ic_source_a));
                mStartMarker.setTitle(startAdd);
            }
            if (end != null) {
                GeoPoint endPoint = new GeoPoint(end.getLatitude(), end.getLongitude());
                mEndMarker.setPosition(endPoint);
                mEndMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(mEndMarker);
                mEndMarker.setIcon(getResources().getDrawable(R.drawable.ic_dest_b));
                mEndMarker.setTitle(endAdd);
            }
        }
        mFeedBackListener.onMapPlotted();

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
                        onUpdateLocation(location);
                        AppData.getNewInstance().setCurrentLoc(new LatLng(mLatitude, mLongitude));
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
     * Clear Items from map
     */
    public void clearItemsFromMap() {
        mMapView.getOverlays().clear();
        mMapView.invalidate();
    }

    public void plotDataOfNodes(List<NodeItem> nodeItemList) {
        for (NodeItem nodeItem : nodeItemList) {
            switch (nodeItem.getNodeType().getIdentifier()) {
                case AppConstant.publicTramStop:
                    if (nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicTramStop)) {
                        GeoPoint geoPoint = new GeoPoint(nodeItem.getLatitude(),
                                nodeItem.getLongitude());
                        addMarkerNode(geoPoint, nodeItem.getNodeType().getIdentifier(), nodeItem.getWheelChair().toUpperCase());
                    }
                    break;
                case AppConstant.publicToilets:
                    if (nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicToilets)) {
                        GeoPoint geoPoint = new GeoPoint(nodeItem.getLatitude(),
                                nodeItem.getLongitude());
                        addMarkerNode(geoPoint, nodeItem.getNodeType().getIdentifier(), nodeItem.getWheelChair().toUpperCase());
                    }
                    break;
                case AppConstant.publicBusStop:
                    if (nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicBusStop)) {
                        GeoPoint geoPoint = new GeoPoint(nodeItem.getLatitude(),
                                nodeItem.getLongitude());
                        addMarkerNode(geoPoint, nodeItem.getNodeType().getIdentifier(), nodeItem.getWheelChair().toUpperCase());
                    }
                    break;
                case AppConstant.publicParking:
                    if (nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicParking)) {
                        GeoPoint geoPoint = new GeoPoint(nodeItem.getLatitude(),
                                nodeItem.getLongitude());
                        addMarkerNode(geoPoint, nodeItem.getNodeType().getIdentifier(), nodeItem.getWheelChair().toUpperCase());
                    }
                    break;
            }

        }

    }

    private void addMarkerNode(GeoPoint geoPoint, String category, String wheelChair) {
        String wheelChairAccessible = wheelChair;
        if (wheelChair.equalsIgnoreCase("Yes")) {
            wheelChairAccessible = getResources().getString(R.string.wheelchair_accessible_yes);
        } else if (wheelChair.equalsIgnoreCase("No")) {
            wheelChairAccessible = getResources().getString(R.string.wheelchair_accessible_no);
        } else if (wheelChair.equalsIgnoreCase("Limited")) {
            wheelChairAccessible = getResources().getString(R.string.wheelchair_accessible_limited);
        } else {
            wheelChairAccessible = getResources().getString(R.string.wheelchair_accessible_limited);
        }
        Marker nodeMarker = new Marker(mMapView);
        GeoPoint nodePoints = new GeoPoint(geoPoint.getLatitude(), geoPoint.getLongitude());
        switch (category) {
            case AppConstant.publicTramStop:
                nodeMarker.setPosition(nodePoints);
                nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(nodeMarker);
                nodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_train));
                nodeMarker.setTitle(getResources().getString(R.string.tram_stop_title));
                nodeMarker.setSnippet(getString(R.string.wheelchair_accessible) + wheelChairAccessible);
                break;
            case AppConstant.publicToilets:
                nodeMarker.setPosition(nodePoints);
                nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(nodeMarker);
                nodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_toilet));
                nodeMarker.setTitle(getResources().getString(R.string.toilets_title));
                nodeMarker.setSnippet(getString(R.string.wheelchair_accessible) + wheelChairAccessible);
                break;
            case AppConstant.publicBusStop:
                nodeMarker.setPosition(nodePoints);
                nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(nodeMarker);
                nodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_bus));
                nodeMarker.setTitle(getResources().getString(R.string.bus_stop_title));
                nodeMarker.setSnippet(getString(R.string.wheelchair_accessible) + wheelChairAccessible);
                break;
            case AppConstant.publicParking:
                nodeMarker.setPosition(nodePoints);
                nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(nodeMarker);
                nodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_parking));
                nodeMarker.setTitle(getResources().getString(R.string.parking_title));
                nodeMarker.setSnippet(getString(R.string.wheelchair_accessible) + wheelChairAccessible);
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

    public void addMidWayMarkers(GeoPoint geoPointMid, String geoPointAddress) {
        mMidMarker = new Marker(mMapView);
        if (mMapView != null) {
            if (geoPointMid != null) {
                geoPointMid = new GeoPoint(geoPointMid.getLatitude(), geoPointMid.getLongitude());
                mMidMarker.setPosition(geoPointMid);
                mMidMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(mMidMarker);
                mMidMarker.setIcon(getResources().getDrawable(R.drawable.ic_mid_c));
                mMidMarker.setTitle(geoPointAddress);
            }
        }
    }

    public void addRunningMarker(GeoPoint geoPointRunning) {
        if (mMapView != null) {
            if (mRunningMarker == null) {
                mRunningMarker = new Marker(mMapView);
                mRunningMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mRunningMarker.setIcon(getResources().getDrawable(R.drawable.ic_media_pause_dark));
            }
            mRunningMarker.setPosition(geoPointRunning);
            mMapView.getOverlays().add(mRunningMarker);
        }
    }

    /*public void stopRunningMarker() {
        if (mRunningMarker != null && mMapView != null) {
            mMapView.getOverlays().remove(mRunningMarker);
            mMapView.invalidate();
            UI_HANDLER.removeCallbacks(updateMarker);
        }
    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        // UI_HANDLER.removeCallbacks(updateMarker);
    }


    /**
     * Add polyline for validate / non validate data
     *
     * @param listWayData custom model to send furture  request
     * @param valid       valid data or not
     */
    public void addPolyLineForWays(final ListWayData listWayData, final boolean valid) {
        Polyline polylineWays = new Polyline();
        polylineWays.setPoints(listWayData.getGeoPoints());
        polylineWays.setRelatedObject(listWayData);
        polylineWays.setWidth(20);
        String colorValue;
        if (valid) {
            polylineWays.setColor(getResources().getColor(R.color.colorGreen));
        } else {
            colorValue = listWayData.getColor();
            polylineWays.setColor(Color.parseColor(colorValue));
            //colorIndex=(colorIndex+1)%4;
        }
        polylineWays.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                setBoundingBox(listWayData.getGeoPoints().get(0), listWayData.getGeoPoints().get(listWayData.getGeoPoints().size() - 1));
                ListWayData relatedObject = (ListWayData) polyline.getRelatedObject();
                updatePolylineUIWays(polyline, valid);
                showEnhanceDialog(polyline, valid, relatedObject,true,null);

                return false;
            }
        });
        mMapView.getOverlays().add(polylineWays);

    }

    public void addNodeForWays(final NodeReference nodeReference , final boolean isValid) {
        for (int i = 0; i < nodeReference.getAttributes().size(); i++) {
            if(nodeReference.getAttributes().size()>0) {
                for (int j = 0; j < nodeReference.getAttributes().size(); j++) {
                    if(nodeReference.getAttributes().get(j).getKey().equalsIgnoreCase(AppConstant.KEY_KERB_HEIGHT)) {
                        final Marker mNodeMarker = new Marker(mMapView);
                        if (mMapView != null) {
                            GeoPoint geoPoint = new GeoPoint(Double.parseDouble(nodeReference.getLat()),
                                    Double.parseDouble(nodeReference.getLon()));
                            mNodeMarker.setPosition(geoPoint);
                            mNodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            mMapView.getOverlays().add(mNodeMarker);
                            if(isValid) {
                                mNodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_pin_green));
                            }else {
                                mNodeMarker.setIcon(getResources().getDrawable(R.drawable.ic_pin_pink));
                            }
                            mNodeMarker.setTitle(nodeReference.getAttributes().get(j).getValue());
                            mNodeMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker, MapView mapView) {
                                    GeoPoint geoPointStart= new GeoPoint(Double.parseDouble(nodeReference.getLat()),
                                            Double.parseDouble(nodeReference.getLon()));
                                    GeoPoint geoPointEnd= new GeoPoint(Double.parseDouble(nodeReference.getLat()),
                                            Double.parseDouble(nodeReference.getLon()));
                                    setBoundingBox(geoPointStart,geoPointEnd);

                                    updateNodeUI(marker,isValid);
                                    showEnhanceDialog(null, true, null,false,nodeReference);
                                    return false;
                                }
                            });
                        }
                    }
                }
            }
        }
    }




    public void checkForWay(Polyline polyline, ListWayData way, boolean valid , boolean isForWay,NodeReference nodeReference) {
    }
        /*protected Runnable updateMarker = new Runnable() {
        @Override
        public void run() {
            if (mPolylineArrayListViaMidPoints.size() > 0) {
                if (mPolylineIndex == -1) {
                    //Update polyline index to 0;
                    mPolylineIndex = 0;
                    mGeoPointIndex = 0;
                } else if (mPolylineIndex < mPolylineArrayListViaMidPoints.size()) {
                    if ((mGeoPointIndex + 1) < mPolylineArrayListViaMidPoints.get(mPolylineIndex).getPoints().size()) {
                        //update GeoPint to next for current polyline
                        mGeoPointIndex++;
                    } else {
                        showEnhanceDialog();
                        //Switch to next polyline
                        mPolylineIndex++;
                        mGeoPointIndex = 0;
                        if (mPolylineIndex < mPolylineArrayListViaMidPoints.size()) {
                            GeoPoint start = mPolylineArrayListViaMidPoints.get(mPolylineIndex).getPoints().get(0);
                            GeoPoint end = mPolylineArrayListViaMidPoints.get(mPolylineIndex).getPoints().
                                    get(mPolylineArrayListViaMidPoints.get(mPolylineIndex).getPoints().size() - 1);
                            setBoundingBox(start, end);
                        }
                    }
                }
                if (mPolylineIndex < mPolylineArrayListViaMidPoints.size() && mPolylineIndex != -1 && mGeoPointIndex != -1) {
                    updatePolylineUI(mPolylineArrayListViaMidPoints.get(mPolylineIndex)); // hiding as change update polyline method
                    addRunningMarker(mPolylineArrayListViaMidPoints.get(mPolylineIndex).getPoints().get(mGeoPointIndex));
                    UI_HANDLER.postDelayed(updateMarker, 1000);
                } else {
                    stopRunningMarker();
                    GeoPoint start = mPolylineArrayListViaMidPoints.get(0).getPoints().get(0);
                    GeoPoint end = mPolylineArrayListViaMidPoints.get(mPolylineArrayListViaMidPoints.size() - 1).getPoints().get(mPolylineArrayListViaMidPoints.get(mPolylineArrayListViaMidPoints.size() - 1).getPoints().size() - 1);
                    setBoundingBox(start, end);
                }
            }
        }
    };
*/

          /*private void resetRunningMarker() {
        UI_HANDLER.removeCallbacks(updateMarker);
        mPolylineIndex = -1;
        mGeoPointIndex = -1;
    }*/


    private void showEnhanceDialog(final Polyline polyline, final boolean valid, final ListWayData listWayData,
                                   final boolean isForWay, final NodeReference nodeReference) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.enchance_feedback_pop_up, null);
        Button btnNo = (Button) customView.findViewById(R.id.btn_no);
        Button btnYes = (Button) customView.findViewById(R.id.btn_yes);
        builder.setView(customView);

        if (mAlertDialogEnhance != null && mAlertDialogEnhance.isShowing()) {
            mAlertDialogEnhance.dismiss();
        }
        mAlertDialogEnhance = builder.create();
        mAlertDialogEnhance.show();
        mAlertDialogEnhance.setCanceledOnTouchOutside(true);
        mAlertDialogEnhance.getWindow().setDimAmount(0.0f);
        mAlertDialogEnhance.getWindow().setBackgroundDrawable(new
                ColorDrawable(android.graphics.Color.argb(1, 200, 200, 200)));
        mAlertDialogEnhance.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlertDialogEnhance.getWindow().setGravity(Gravity.BOTTOM);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialogEnhance.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForWay(polyline, listWayData, valid ,isForWay,nodeReference);
                mAlertDialogEnhance.dismiss();
            }
        });
    }

    public void setZoomMap() {
        if (mMapView != null) {
            MapController myMapController = (MapController) mMapView.getController();
            myMapController.setZoom(15);
        }
    }


    public IGeoPoint getMapCenter() {
        IGeoPoint mapViewMapCenter = null;
        if (mMapView != null) {
            mapViewMapCenter = mMapView.getMapCenter();
        }
        return mapViewMapCenter;
    }



}



