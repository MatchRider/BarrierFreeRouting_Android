package com.disablerouting.directions;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.route_planner.model.Steps;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends BaseActivityImpl  implements DirectionAdapter.OnInstructionsClickListener {


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private DirectionAdapter mDirectionAdapter;
    private List<Steps> mStepsList= new ArrayList<Steps>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        ButterKnife.bind(this);

        if(getIntent().hasExtra(AppConstant.STEP_DATA)){
            mStepsList = getIntent().getParcelableArrayListExtra(AppConstant.STEP_DATA);
        }
        mDirectionAdapter=new DirectionAdapter(this,mStepsList,this);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mDirectionAdapter);
        mDirectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInstructionClick(Steps steps) {
        //TODO

    }
}
