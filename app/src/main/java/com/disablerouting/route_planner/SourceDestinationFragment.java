package com.disablerouting.route_planner;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseFragmentImpl;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.presenter.ISourceDestinationScreenPresenter;
import com.disablerouting.route_planner.presenter.SourceDestinationScreenPresenter;
import com.disablerouting.route_planner.view.IDirectionsViewFragment;
import com.disablerouting.route_planner.view.OnSourceDestinationListener;
import com.disablerouting.utils.Utility;
import com.disablerouting.widget.CustomAutoCompleteTextView;
import org.osmdroid.util.GeoPoint;

public class SourceDestinationFragment extends BaseFragmentImpl implements IDirectionsViewFragment {

    @BindView(R.id.edt_source_add)
    CustomAutoCompleteTextView mEditTextSource;

    @BindView(R.id.edt_dest_add)
    CustomAutoCompleteTextView mEditTextDestination;

    private ISourceDestinationScreenPresenter mISourceDestinationScreenPresenter;
    private String mCoordinates = null;
    private String mProfileType = null;
    private static OnSourceDestinationListener mOnSourceDestinationListener;
    private static int ANIMATION_DURATION = 200;
    TranslateAnimation mTranslateAnimationSource;
    TranslateAnimation mTranslateAnimationDestination;

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
        mISourceDestinationScreenPresenter = new SourceDestinationScreenPresenter(this, new DirectionsManager());

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
        mISourceDestinationScreenPresenter.getDestinationsData(mCoordinates, mProfileType);

    }

    private void initView() {

    }

    @OnClick(R.id.txv_go)
    public void onGoClick() {
        //TODO pass source destination points
        GeoPoint geoPointStart = null, geoPointEnd = null;
        mOnSourceDestinationListener.onGoClick(geoPointStart, geoPointEnd);
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        mOnSourceDestinationListener.onBackPress();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity(), mEditTextSource);
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity(), mEditTextDestination);
        mOnSourceDestinationListener.onSourceCompleted(null);
        mOnSourceDestinationListener.onDestinationCompleted(null);
        clearSourceComplete();
        clearDestinationComplete();

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
        if (data != null && data.getRoutesList() != null && data.getRoutesList().size() != 0
                && data.getRoutesList().get(0).getGeometry() != null) {
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
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity(), mEditTextSource);
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity(), mEditTextDestination);
        mISourceDestinationScreenPresenter.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity(), mEditTextSource);
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity(), mEditTextDestination);
    }

    public void clearSourceComplete() {
        mEditTextSource.setText("");
    }

    public void clearDestinationComplete() {
        mEditTextDestination.setText("");
    }

    @OnClick(R.id.img_swap)
    public void swapDataOfViews() {
        toggle();
        GeoPoint geoPointStart = null, geoPointEnd = null;
        mOnSourceDestinationListener.onGoSwapView(geoPointStart, geoPointEnd);

    }

    public void toggle() {
        mTranslateAnimationSource = Utility.translate(0, 0, 0, mEditTextSource.getHeight(), ANIMATION_DURATION);
        mTranslateAnimationDestination = Utility.translate(0, 0, 0, -mEditTextDestination.getHeight(), ANIMATION_DURATION);
        mTranslateAnimationSource.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Grab the text that is held by viewToMove's EditText
                String temp = mEditTextSource.getText().toString();
                // Set viewToMove's EditText to show contents of viewToReplace's EditText
                mEditTextSource.setText((mEditTextDestination.getText().toString()));
                mEditTextDestination.setText(temp);

                mEditTextSource.setAnimation(null);
                mEditTextDestination.setAnimation(null);

            }
        });
        // Start animations
        mEditTextSource.startAnimation(mTranslateAnimationSource);
        mEditTextDestination.startAnimation(mTranslateAnimationDestination);
    }


}
