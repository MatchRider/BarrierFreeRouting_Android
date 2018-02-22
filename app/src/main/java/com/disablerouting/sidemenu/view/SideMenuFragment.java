package com.disablerouting.sidemenu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.disablerouting.R;
import com.disablerouting.sidemenu.adapter.SideMenuAdapter;
import com.disablerouting.sidemenu.model.SideMenuData;
import com.disablerouting.sidemenu.presenter.ISideMenuViewListener;
import com.disablerouting.sidemenu.presenter.SideMenuPresenter;

import java.util.List;

public class SideMenuFragment extends Fragment implements ISideMenuView,
        ISideMenuViewListener {


    @BindView(R.id.list_view)
    ListView mListView;

    private SideMenuAdapter mSideMenuAdapter;
    private ISideMenuFragmentCallback mSideMenuFragmentCallback;
    private SideMenuPresenter mSideMenuPresenter;
    private String image;

    public static SideMenuFragment newInstance() {

        Bundle args = new Bundle();

        SideMenuFragment fragment = new SideMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSideMenuPresenter = new SideMenuPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.side_menu_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mSideMenuAdapter = new SideMenuAdapter(getActivity(), this, R.layout.side_menu_row_item_view);
        initializeView();
        mSideMenuPresenter.onViewBeingCreated(this);
    }

    public void notifyList() {
        initializeView();
        mSideMenuAdapter.notifyDataSetChanged();
    }

    /**
     * View initialize
     */
    private void initializeView() {

    }

    public void setClickListener(ISideMenuFragmentCallback sideMenuFragmentCallback) {
        mSideMenuFragmentCallback = sideMenuFragmentCallback;
    }

    @Override
    public void setSideMenuListToView(List<SideMenuData> list) {
        mSideMenuAdapter.addAll(list);
        mListView.setAdapter(mSideMenuAdapter);
        mSideMenuAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSideMenuItemClick(int message) {
        Intent newActivityIntent;
        mSideMenuFragmentCallback.onClick(R.string.close_drawer);

        switch (message) {
            case R.string.ACKNOWLEDGEMENTS:
                break;
            case R.string.FEEDBACK:
                break;
            case R.string.CONTACT:
                break;
            case R.string.DISCLAIMER:
                break;
            case R.string.LEGAL:
                break;

        }
    }

}
