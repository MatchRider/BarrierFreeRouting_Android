package com.disablerouting.instructions;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.route_planner.model.Steps;

import java.util.ArrayList;
import java.util.List;

public class InstructionsActivity extends BaseActivityImpl  implements InstructionsAdapter.OnInstructionsClickListener {


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private InstructionsAdapter mInstructionsAdapter;
    private List<Steps> mStepsList= new ArrayList<Steps>();
    private int mCoordinateSize=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        ButterKnife.bind(this);

        if(getIntent().hasExtra(AppConstant.STEP_DATA)){
            mStepsList = getIntent().getParcelableArrayListExtra(AppConstant.STEP_DATA);
        }
        if(getIntent().hasExtra(AppConstant.COORDINATE_LIST)){
            mCoordinateSize = Integer.parseInt(getIntent().getStringExtra(AppConstant.COORDINATE_LIST));
        }
        for (int i=0;i< mStepsList.size();i++){
            if(mStepsList.get(i).getType()==14){
                mStepsList.remove(i+1);
                mStepsList.remove(i-1);
            }
        }
        mInstructionsAdapter =new InstructionsAdapter(this,mStepsList,this,mCoordinateSize);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mInstructionsAdapter);
        mInstructionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInstructionClick(Steps steps) {
        //TODO

    }

    @OnClick(R.id.img_back)
    public void onBackClick(){
        finish();
    }
}
