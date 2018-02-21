package com.disablerouting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.common.PolylineDecoder;
import com.disablerouting.location.GPSTracker;
import com.disablerouting.utils.PermissionUtils;
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

public abstract class MapBaseActivity extends BaseActivityImpl implements GPSTracker.onUpdateLocation{

    private MapView mMapView = null;
    private MyLocationNewOverlay mLocationOverlay;
    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    final String[] locationPermissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE};
    private LocationManager  mLocationManager;
    private GPSTracker mGPSTracker;

    private double mLatitude=0;
    private double mLongitude=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mGPSTracker = new GPSTracker(this, this);

        if(mGPSTracker.canGetLocation()){
            mLatitude = mGPSTracker.getLatitude();
            mLongitude = mGPSTracker.getLongitude();
            checkLocationStatus();
        }
        else {
            openSettingDialog();
        }
    }


    /**
     * Result when user give permission or not
     *
     * @param requestCode  Request code
     * @param permissions  Type of permission
     * @param grantResults Results of permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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
                        Toast.makeText(this, R.string.error_load_maps, Toast.LENGTH_SHORT).show();
                    } else {
                        if(mLocationManager!=null && !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            openSettingDialog();
                        }else {
                            initializeMap();

                        }
                    }
                } else {
                    Toast.makeText(this, R.string.error_load_maps, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    protected void openSettingDialog() {
        assert mLocationManager != null;
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.gps_setting);
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
        }else {
            initializeMap();
        }

    }

    /**
     * Open setting activity
     */
    protected void startSettingActivity() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, AppConstant.SETTING_REQUEST_CODE);
    }


    /*
    * * Check location services status
     */
    protected void checkLocationStatus() {
        if (!PermissionUtils.isPermissionAllowed(this, android.Manifest.permission_group.LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, locationPermissions, MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }else {
            if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                openSettingDialog();
            }else {
                initializeMap();
            }
        }
    }

    private void initializeMap() {
        mMapView = findViewById(R.id.map_view);

        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        List<GeoPoint> geoPointArrayList ;
        geoPointArrayList = PolylineDecoder.decodePoly("mtkeHuv|q@~@VLHz@\\\\PR|@hBt@j@^n@L\\\\NjALv@Jh@NXi@zBm@jCKTy@z@qAhBa@\\\\[Ne@DgCc@i@?[Ty@hAi@zASRi@R}@H_@N[b@kAdCy@`Au@d@eA|@q@h@WRe@PYHYBqADgAAcAL_A^w@~@q@`@w@Zw@Cm@K[PeA|Aa@p@g@fAiAhBuAv@]VU^k@xAUXe@TqATy@V}@f@_@VO\\\\Mb@[fBe@|@Mp@WbCgClKSdAq@Rm@?g@WYg@G[[}Bk@qBy@wDUm@w@}@q@}A]o@k@y@kAjC_AjC_ApCe@z@i@j@q@f@[NsAp@u@T}A\\\\wATU?WCeBm@q@MwAGUCg@SMaAi@mDQm@K}@Mq@u@mAc@i@c@Ys@[WW_@q@e@a@cA_@w@E{BHmBXqBkBsA}@{Ao@iAB{@QYSi@qCUy@Ee@@SDWbA_BLKLAVNb@r@J@HEHK?]k@iDe@w@COAWBUh@qBDc@?c@Q{BGa@MQKCOBgA\\\\{@AKEs@Wq@i@q@{@s@gAk@kA]g@g@_@I]??k@i@yBkEa@}@W}@WkCUqC?_@Hg@ZqABg@Gm@YoAEgAMq@@jAB|CC`@{@rACH");
        addPolyLine(geoPointArrayList);
        GeoPoint geoPointStart = null, geoPointEnd =null;
        if(geoPointArrayList!=null && geoPointArrayList.size()!=0){
            geoPointStart = geoPointArrayList.get(0);
            geoPointEnd = geoPointArrayList.get(geoPointArrayList.size()-1);
            addMarkers(geoPointStart,geoPointEnd);
        }


        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);

        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mMapView.getOverlays().add(myScaleBarOverlay);


        setProvider();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.SETTING_REQUEST_CODE) {
            if(mGPSTracker.canGetLocation()) {
                mLatitude = mGPSTracker.getLatitude();
                mLongitude = mGPSTracker.getLongitude();
            }
            initializeMap();
        }else {
            checkLocationStatus();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGPSTracker.stopUsingGPS();
    }

    private void setProvider() {
        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(getApplicationContext());
        gpsMyLocationProvider.addLocationSource(LocationManager.GPS_PROVIDER);
        gpsMyLocationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);

        mLocationOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mLocationOverlay);
    }

    public void addPolyLine(List<GeoPoint> geoPointList){
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
        mMapView.getOverlayManager().add(line);

    }

    private void addMarkers(GeoPoint start , GeoPoint end){

        MapController myMapController = (MapController) mMapView.getController();
        myMapController.setZoom(15);
        myMapController.setCenter(start);


        GeoPoint startPoint = new GeoPoint(start.getLatitude(),start.getLongitude());
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        startMarker.setTitle("Start point");


        GeoPoint endPoint = new GeoPoint(end.getLatitude(),end.getLongitude());
        Marker endMarker = new Marker(mMapView);
        endMarker.setPosition(endPoint);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(endMarker);
        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        endMarker.setTitle("End point");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected abstract int getView();

}
