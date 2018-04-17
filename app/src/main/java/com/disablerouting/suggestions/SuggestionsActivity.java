package com.disablerouting.suggestions;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.utils.Utility;

public class SuggestionsActivity extends MapBaseActivity implements OnSuggestionListener {

    private SuggestionFragment mSuggestionFragment;

    @BindView(R.id.btn_go)
    Button mBtnGo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mSuggestionFragment = SuggestionFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSuggestionFragment, "");
        mBtnGo.setVisibility(View.GONE);

    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }

    @Override
    protected void onUpdateLocation(Location location) {
        addCurrentLocation();
    }

    @OnClick(R.id.img_re_center)
    public void reCenter() {
        addCurrentLocation();
    }

    @Override
    public void onFeedBackClick(double longitude, double latitude) {

    }

    @Override
    public void OnSuggest() {

    }

    @Override
    public void onBackPress() {
        finish();
    }



}
