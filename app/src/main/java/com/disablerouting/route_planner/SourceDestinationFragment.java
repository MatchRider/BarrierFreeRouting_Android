package com.disablerouting.route_planner;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseFragmentImpl;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.adapter.CustomListAdapter;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.presenter.ISourceDestinationScreenPresenter;
import com.disablerouting.route_planner.presenter.SourceDestinationScreenPresenter;
import com.disablerouting.route_planner.view.ISourceDestinationViewFragment;
import com.disablerouting.route_planner.view.OnSourceDestinationListener;
import com.disablerouting.utils.Utility;
import com.disablerouting.widget.CustomAutoCompleteTextView;
import org.osmdroid.util.GeoPoint;

import java.util.List;

public class SourceDestinationFragment extends BaseFragmentImpl implements ISourceDestinationViewFragment,
        TextView.OnEditorActionListener , AdapterView.OnItemClickListener{

    @BindView(R.id.edt_source_add)
    CustomAutoCompleteTextView mEditTextSource;

    @BindView(R.id.edt_dest_add)
    CustomAutoCompleteTextView mEditTextDestination;

    @BindView(R.id.rel_source_destination)
    RelativeLayout mRelativeLayoutSourceDestination;

    private static final int SEARCH_TEXT_CHANGED = 1000;

    private ISourceDestinationScreenPresenter mISourceDestinationScreenPresenter;
    private String mCoordinates = null;
    private String mProfileType = "driving-car";
    private GeoPoint mGeoPointSource;
    private GeoPoint mGeoPointDestination;
    private static OnSourceDestinationListener mOnSourceDestinationListener;

    private CustomListAdapter mAddressListAdapter;
    private List<Features> mFeaturesResultSearch;
    private ListPopupWindow mListPopupWindow;
    private Features mFeaturesSource;
    private Features mFeaturesDestination;

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mEditTextSource.hasFocus() && mEditTextSource!=null && !mEditTextSource.getText().toString().equalsIgnoreCase("")){
                mISourceDestinationScreenPresenter.getCoordinatesData(mEditTextSource.getText().toString());
            }
            if(mEditTextDestination.hasFocus() && mEditTextDestination!=null && !mEditTextDestination.getText().toString().equalsIgnoreCase("")){
                mISourceDestinationScreenPresenter.getCoordinatesData(mEditTextDestination.getText().toString());
            }
            Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        }
    };


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
        mISourceDestinationScreenPresenter = new SourceDestinationScreenPresenter(this, new DirectionsManager(), new GeoCodingManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_source_destination, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        addListener();
    }

    @OnClick(R.id.edt_source_add)
    public void onSourceTap() {
        addListener();
    }

    @OnClick(R.id.edt_dest_add)
    public void onDestinationTap() {
        addListener();
    }

    public void callForDestination(GeoPoint geoPointSource, GeoPoint geoPointDestination){
        mGeoPointSource = geoPointSource;
        mGeoPointDestination= geoPointDestination;
        if(mEditTextSource!=null && !mEditTextSource.getText().toString().equalsIgnoreCase("") &&
                mEditTextDestination!=null && !mEditTextDestination.getText().toString().equalsIgnoreCase("")){

            mCoordinates = mGeoPointSource+"|"+mGeoPointDestination;
            mISourceDestinationScreenPresenter.getDestinationsData(mCoordinates,mProfileType);
        }
        handler.removeMessages(SEARCH_TEXT_CHANGED);
    }

    @OnClick(R.id.txv_go)
    public void onGoClick() {
        mOnSourceDestinationListener.onSourceDestinationSelected(mFeaturesSource,mFeaturesDestination);
        callForDestination(mGeoPointSource,mGeoPointDestination);
    }

    /**
     * Set Text change listener for edit text
     */
    private void addListener() {
        mEditTextSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                /*if (str.length() != 0) {
                    mDestinationAddressClear.setVisibility(View.VISIBLE);
                } else {
                    mDestinationAddressClear.setVisibility(View.GONE);
                }*/
                if (str.toString().length() > 2) {
                    handler.removeMessages(SEARCH_TEXT_CHANGED);
                    handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, str.toString()), 500);
                } else {
                    handler.removeMessages(SEARCH_TEXT_CHANGED);
                    handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, ""), 500);
                }
            }
        });

        mEditTextDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                /*if (str.length() != 0) {
                    mSourceAddressClear.setVisibility(View.VISIBLE);
                } else {
                    mSourceAddressClear.setVisibility(View.GONE);
                }*/
                if (str.toString().length() > 3) {
                    handler.removeMessages(SEARCH_TEXT_CHANGED);
                    handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, str.toString()), 500);
                } else {
                    handler.removeMessages(SEARCH_TEXT_CHANGED);
                    handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, ""), 500);
                }
            }
        });
    }


    @OnClick(R.id.img_back)
    public void onBackClick() {
        hideLoader();
        mOnSourceDestinationListener.onBackPress();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        clearSourceComplete();
        clearDestinationComplete();
    }


    @Override
    public void showLoader() {
        if(getActivity()!=null && !getActivity().isFinishing()) {
            showProgress();
        }
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
       /*     mEditTextSource.setFocusableInTouchMode(true);
            mEditTextSource.requestFocus();
            mEditTextDestination.setFocusableInTouchMode(true);
            mEditTextDestination.requestFocus();
*/
        }
    }

    @Override
    public void onFailureDirection(int error) {
        showSnackBar(error);
    }

    @Override
    public void onGeoDataDataReceived(GeoCodingResponse data) {
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        if(data!=null && data.getFeatures()!=null && data.getFeatures().size()!=0) {
            mFeaturesResultSearch= data.getFeatures();
            mAddressListAdapter = new CustomListAdapter(getContext(), R.layout.address_item_view, data.getFeatures());
            setListPopUp(mRelativeLayoutSourceDestination);
        }
    }

    @Override
    public void onFailureGeoCoding(int error) {
        showSnackBar(error);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        mISourceDestinationScreenPresenter.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
    }

    public void clearSourceComplete() {
        mEditTextSource.setText("");
    }

    public void clearDestinationComplete() {
        mEditTextDestination.setText("");
    }

    @OnClick(R.id.img_swap)
    public void swapDataOfViews() {
        handler.removeMessages(SEARCH_TEXT_CHANGED);
        /*mEditTextSource.clearFocus();
        mEditTextDestination.clearFocus();

        mEditTextSource.setFocusableInTouchMode(false);
        mEditTextDestination.setFocusableInTouchMode(false);*/
        changeAddress();
    }

    /**
     * Swap address when toggle
     */
    public void changeAddress() {
        String sourceData = mEditTextSource.getText().toString();
        mEditTextSource.setText((mEditTextDestination.getText().toString()));
        mEditTextDestination.setText(sourceData);

        if(mEditTextSource!=null && !mEditTextSource.getText().toString().equalsIgnoreCase("") &&
                mEditTextDestination!=null && !mEditTextDestination.getText().toString().equalsIgnoreCase("")){

            mCoordinates = mGeoPointDestination+"|"+mGeoPointSource;
            mISourceDestinationScreenPresenter.getDestinationsData(mCoordinates,mProfileType);
        }
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnSourceDestinationListener = null;
    }

    /**
     * Show data of search result in pop up
     * @param anchor view below which placed  result
     */
    private void setListPopUp(View anchor){
        mListPopupWindow = new ListPopupWindow(getContext());
        mListPopupWindow.setAnchorView(anchor);
        mListPopupWindow.setAnimationStyle(R.style.popup_window_animation);
        int height = Utility.calculatePopUpHeight(getContext());
        mListPopupWindow.setHeight(height/2);
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setAdapter(mAddressListAdapter);
        mListPopupWindow.setOnItemClickListener(this);
        mListPopupWindow.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mListPopupWindow.dismiss();
        if(mEditTextSource.hasFocus()) {
            mEditTextSource.setText(mFeaturesResultSearch.get(i).getProperties().toString());
            Toast.makeText(getContext(), mFeaturesResultSearch.get(i).getProperties().toString(), Toast.LENGTH_SHORT).show();
            mGeoPointSource = new GeoPoint(mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(0),
                    mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(1));
            mFeaturesSource = mFeaturesResultSearch.get(0);

        }else if(mEditTextDestination.hasFocus()){
            mEditTextDestination.setText(mFeaturesResultSearch.get(i).getProperties().toString());
            mGeoPointDestination = new GeoPoint(mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(0),
                    mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(1));
            mFeaturesDestination = mFeaturesResultSearch.get(0);

        }

        handler.removeMessages(SEARCH_TEXT_CHANGED);
    }
}
