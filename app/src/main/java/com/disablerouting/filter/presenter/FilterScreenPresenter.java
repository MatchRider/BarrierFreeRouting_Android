package com.disablerouting.filter.presenter;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.filter.view.IFilterView;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.geo_coding.presenter.IGeoCodingResponseReceiver;

public class FilterScreenPresenter implements IFilterScreenPresenter , IGeoCodingResponseReceiver {

    private IFilterView mIFilterView;
    private GeoCodingManager mGeoCodingManager;
    private boolean isForCurrentLoc;


    public FilterScreenPresenter(IFilterView IFilterView, GeoCodingManager geoCodingManager) {
        mIFilterView = IFilterView;
        mGeoCodingManager = geoCodingManager;
    }

    @Override
    public void getCoordinatesData(String query, String location, int limit) {
        isForCurrentLoc=!TextUtils.isEmpty(location);
        if (mIFilterView != null) {
            mIFilterView.showLoader();
            mGeoCodingManager.getGeoCoding(this, query, location,limit);
        }
    }

    @Override
    public void getGeoCodeDataForward(String query) {
        isForCurrentLoc=false;
        if (mIFilterView != null) {
           // mIFilterView.showLoader();
            mGeoCodingManager.getGeoCodeForward(this, query);
        }
    }

    @Override
    public void getGeoCodeDataReverse(double latitude, double longitude) {
        isForCurrentLoc=true;
        if (mIFilterView != null) {
          //  mIFilterView.showLoader();
            mGeoCodingManager.getGeoCodeReverse(this, latitude, longitude);
        }
    }

    @Override
    public void disconnect() {
        if(mGeoCodingManager!=null){
            mGeoCodingManager.cancel();
        }
    }

    @Override
    public void onSuccessGeoCoding(GeoCodingResponse data) {
        if (mIFilterView != null) {
          //  mIFilterView.hideLoader();
            mIFilterView.onGeoDataDataReceived(data,isForCurrentLoc);
        }

    }

    @Override
    public void onFailureGeoCoding(@NonNull ErrorResponse errorResponse) {
        if (mIFilterView != null) {
           // mIFilterView.hideLoader();
            mIFilterView.onFailureGeoCoding(errorResponse.getErrorMessage());
        }
    }
}
