package com.disablerouting.route_planner;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseFragmentImpl;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.presenter.DirectionsPresenter;
import com.disablerouting.route_planner.presenter.IDirectionsScreenPresenter;
import com.disablerouting.route_planner.view.IDirectionsViewFragment;
import org.osmdroid.util.GeoPoint;

public class SourceDestinationFragment extends BaseFragmentImpl implements IDirectionsViewFragment {

    @BindView(R.id.edt_source_add)
    EditText mEditTextSource;

    @BindView(R.id.edt_dest_add)
    EditText mEditTextDestination;

    private GeoPoint mGeoPointSource=null;
    private GeoPoint mGeoPointDestination=null;
    private IDirectionsScreenPresenter mIDirectionsScreenPresenter;
    private String mCoordinates=null;
    private String mProfileType=null;
    private static OnSourceDestinationListener mOnSourceDestinationListener;


    public static SourceDestinationFragment newInstance(OnSourceDestinationListener onSourceDestinationListener) {
        mOnSourceDestinationListener = onSourceDestinationListener;
        Bundle args = new Bundle();
        SourceDestinationFragment fragment = new SourceDestinationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIDirectionsScreenPresenter = new DirectionsPresenter(this, new DirectionsManager());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_source_destination, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();

        mCoordinates = "8.34234,48.23424|8.34423,48.26424";
        mProfileType = "driving-car";
        mIDirectionsScreenPresenter.getDestinationsData(mCoordinates,mProfileType);

    }

    private void initView() {

    }

    @OnClick(R.id.txv_go)
    public void onGoClick(){
        //TODO pass source destination points
        GeoPoint geoPointStart=null, geoPointEnd=null;
        mOnSourceDestinationListener.onGoClick(geoPointStart,geoPointEnd);
    }

    @OnClick(R.id.img_back)
    public void onBackClick(){
        mOnSourceDestinationListener.onBackPress();
    }


    @Override
    public void showLoader() {
        showProgress();
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }

    @Override
    public void onDirectionDataReceived(DirectionsResponse data) {
        if(data!=null && data.getRoutesList()!=null && data.getRoutesList().size()!=0
                && data.getRoutesList().get(0).getGeometry()!=null){
            mOnSourceDestinationListener.plotDataOnMap(data.getRoutesList().get(0).getGeometry());
        }
    }

    @Override
    public void onFailure(int error) {
        showSnackBar(error);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIDirectionsScreenPresenter.disconnect();
    }
}
