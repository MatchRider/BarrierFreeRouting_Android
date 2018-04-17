package com.disablerouting.suggestions;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseFragmentImpl;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.route_planner.adapter.CustomListAdapter;
import com.disablerouting.utils.Utility;
import com.disablerouting.widget.CustomAutoCompleteTextView;
import org.osmdroid.util.GeoPoint;

import java.util.List;

public class SuggestionFragment extends BaseFragmentImpl implements ISuggestionFragment, AdapterView.OnItemClickListener {

    @BindView(R.id.edt_source_add)
    CustomAutoCompleteTextView mEditTextSource;

    @BindView(R.id.edt_dest_add)
    CustomAutoCompleteTextView mEditTextDestination;

    @BindView(R.id.clear_source_address)
    ImageView mSourceAddressClear;

    @BindView(R.id.clear_destination_address)
    ImageView mDestinationAddressClear;


    @BindView(R.id.fetch_current_source_address)
    ImageView mSourceAddressFetch;

    @BindView(R.id.fetch_current_destination_address)
    ImageView mDestinationAddressFetch;


    @BindView(R.id.ll_time_distance)
    LinearLayout mLinearLayoutTimeDistance;

    @BindView(R.id.txv_time)
    TextView mTextViewTime;

    @BindView(R.id.txv_km)
    TextView mTextViewKM;

    @BindView(R.id.txv_accent)
    TextView mTextViewAccent;

    @BindView(R.id.txv_decent)
    TextView mTextViewDecent;


    private static final int SEARCH_TEXT_CHANGED = 1000;
    private String mCurrentLocation = null;
    private static OnSuggestionListener mSuggestionListener;
    private GeoPoint mGeoPointSource;
    private GeoPoint mGeoPointDestination;
    private CustomListAdapter mAddressListAdapter;
    private List<Features> mFeaturesResultSearch;
    private ListPopupWindow mListPopupWindow;
    private Features mFeaturesSource;
    private Features mFeaturesDestination;
    private boolean mIsTextInputManually = false;


    public static SuggestionFragment newInstance(OnSuggestionListener onSuggestionListener) {
        mSuggestionListener = onSuggestionListener;
        Bundle args = new Bundle();
        SuggestionFragment fragment = new SuggestionFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!msg.obj.equals("")) {
                if (mListPopupWindow != null && mListPopupWindow.isShowing()) {
                    mListPopupWindow.dismiss();
                }
                if (mEditTextSource.hasFocus() && mEditTextSource != null && !mEditTextSource.getText().toString().equalsIgnoreCase("")) {
                   // mISourceDestinationScreenPresenter.getCoordinatesData(mEditTextSource.getText().toString(), "", 10);
                }
                if (mEditTextDestination.hasFocus() && mEditTextDestination != null && !mEditTextDestination.getText().toString().equalsIgnoreCase("")) {
                   // mISourceDestinationScreenPresenter.getCoordinatesData(mEditTextDestination.getText().toString(), "", 10);
                }
            }

        }
    };

    @OnClick(R.id.fetch_current_source_address)
    public void fetchCurrentSourceAdd() {
        mIsTextInputManually = false;
        if (mEditTextSource.hasFocus() && mEditTextSource != null && mEditTextSource.getText().toString().equalsIgnoreCase("")) {
           // mISourceDestinationScreenPresenter.getCoordinatesData("", mCurrentLocation, 0);
        }

    }

    @OnClick(R.id.fetch_current_destination_address)
    public void fetchCurrentDestinationAdd() {
        mIsTextInputManually = false;
        handler.removeMessages(SEARCH_TEXT_CHANGED);
        if (mEditTextDestination.hasFocus() && mEditTextDestination != null && mEditTextDestination.getText().toString().equalsIgnoreCase("")) {
           // mISourceDestinationScreenPresenter.getCoordinatesData("", mCurrentLocation, 0);
        }
    }
    @OnClick(R.id.clear_source_address)
    public void clearSource() {
        mSourceAddressClear.setVisibility(View.GONE);
        mSourceAddressFetch.setVisibility(View.GONE);
        mEditTextSource.setText("");
    }

    @OnClick(R.id.clear_destination_address)
    public void clearDestination() {
        mDestinationAddressClear.setVisibility(View.GONE);
        mDestinationAddressFetch.setVisibility(View.GONE);
        mEditTextDestination.setText("");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestion, container, false);
    }
    @Override
    public void showLoader() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            showProgress();
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        addFocusChangeListener();
        addListener();
    }
    public void addFocusChangeListener() {
        mEditTextSource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && mEditTextSource.getText().toString().trim().length() == 0) {
                    mSourceAddressFetch.setVisibility(View.VISIBLE);
                } else {
                    mSourceAddressFetch.setVisibility(View.GONE);
                    mDestinationAddressFetch.setVisibility(View.GONE);
                }
            }
        });
        mEditTextDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && mEditTextDestination.getText().toString().trim().length() == 0) {
                    mDestinationAddressFetch.setVisibility(View.VISIBLE);

                } else {
                    mDestinationAddressFetch.setVisibility(View.GONE);
                    mSourceAddressFetch.setVisibility(View.GONE);

                }
            }
        });
    }
    /**
     * Set Text change listener for edit text
     */
    private void addListener() {
        mEditTextSource.addTextChangedListener(mSourceWatcher);
        mEditTextDestination.addTextChangedListener(mDestWatcher);
    }

    private TextWatcher mSourceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable str) {
            updateEditControls(mEditTextSource, mSourceAddressFetch, mSourceAddressClear);
            if (str.toString().length() > 3 && mIsTextInputManually) {
                handler.removeMessages(SEARCH_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, str.toString()), 500);
            } else {
                mIsTextInputManually = true;
                handler.removeMessages(SEARCH_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, ""), 500);
            }

        }
    };

    private TextWatcher mDestWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable str) {
            updateEditControls(mEditTextDestination, mDestinationAddressFetch, mDestinationAddressClear);
            if (str.toString().length() > 3 && mIsTextInputManually) {
                handler.removeMessages(SEARCH_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, str.toString()), 500);
            } else {
                mIsTextInputManually = true;
                handler.removeMessages(SEARCH_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, ""), 500);
            }

        }
    };

    /**
     * Update Controls of edit text
     *
     * @param mTV   TextView
     * @param loc   location fetch image
     * @param clear clear text image
     */
    private void updateEditControls(CustomAutoCompleteTextView mTV, View loc, View clear) {
        if (mTV.hasFocus() && mTV.getText().toString().isEmpty()) {
            clear.setVisibility(View.GONE);
            loc.setVisibility(View.VISIBLE);
        } else if (mTV.hasFocus() && mTV.getText().toString().trim().length() >= 1) {
            loc.setVisibility(View.GONE);
            clear.setVisibility(View.VISIBLE);
        } else {
            loc.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);
        }
    }

    /**
     * Show data of search result in pop up
     *
     * @param anchor view below which placed  result
     */
    private void setListPopUp(View anchor) {
        mListPopupWindow = new ListPopupWindow(getContext());
        mListPopupWindow.setAnchorView(anchor);
        mListPopupWindow.setAnimationStyle(R.style.popup_window_animation);
        int height = Utility.calculatePopUpHeight(getContext());
        mListPopupWindow.setHeight(height / 2);
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setAdapter(mAddressListAdapter);
        mListPopupWindow.setOnItemClickListener(this);
        mListPopupWindow.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        mListPopupWindow.dismiss();
        if (mEditTextSource.hasFocus()) {
            mEditTextSource.setText(mFeaturesResultSearch.get(i).getProperties().toString());
            mGeoPointSource = new GeoPoint(mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(0),
                    mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(1));
            mFeaturesSource = mFeaturesResultSearch.get(0);

        } else if (mEditTextDestination.hasFocus()) {
            mEditTextDestination.setText(mFeaturesResultSearch.get(i).getProperties().toString());
            mGeoPointDestination = new GeoPoint(mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(0),
                    mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(1));
            mFeaturesDestination = mFeaturesResultSearch.get(0);

        }
        handler.removeMessages(SEARCH_TEXT_CHANGED);
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        hideLoader();
        mSuggestionListener.onBackPress();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        mEditTextSource.setText("");
        mEditTextDestination.setText("");
    }

}
