package com.disablerouting.sidemenu.presenter;

import com.disablerouting.sidemenu.model.SideMenuData;
import com.disablerouting.sidemenu.view.ISideMenuView;

import java.util.ArrayList;
import java.util.List;

public class SideMenuPresenter {
    
    private ISideMenuView mSideMenuView;
    
    public void onViewBeingCreated(ISideMenuView iSideMenuView) {
        this.mSideMenuView = iSideMenuView;
        List<SideMenuData> sideMenuItems = getList();
        if (sideMenuItems != null && sideMenuItems.size() != 0) {
            this.mSideMenuView.setSideMenuListToView(sideMenuItems);
        }
    }
    
    public void onViewBeingDestroyed() {
        this.mSideMenuView = null;
    }
    
    /**
     * @return return array list of side menu data
     */
    private ArrayList<SideMenuData> getList() {
        ArrayList<SideMenuData> slidingMenuItems = new ArrayList<SideMenuData>();
        slidingMenuItems.add(SideMenuData.ACKNOWLEDGEMENTS);
        //slidingMenuItems.add(SideMenuData.FEEDBACK);
        slidingMenuItems.add(SideMenuData.CONTACT);
        slidingMenuItems.add(SideMenuData.DISCLAIMER);
        slidingMenuItems.add(SideMenuData.LEGAL);
        return slidingMenuItems;
    }
}