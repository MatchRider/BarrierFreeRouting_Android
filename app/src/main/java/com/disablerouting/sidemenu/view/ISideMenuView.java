package com.disablerouting.sidemenu.view;

import com.disablerouting.sidemenu.model.SideMenuData;

import java.util.List;

public interface ISideMenuView {

    /**
     * show value to recycler view
     *
     * @param adapter hold adapter
     */
    void setSideMenuListToView(List<SideMenuData> adapter);

}
