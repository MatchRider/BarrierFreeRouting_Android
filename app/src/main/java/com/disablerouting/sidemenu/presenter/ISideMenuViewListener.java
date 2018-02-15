package com.disablerouting.sidemenu.presenter;

public interface ISideMenuViewListener {

    /**
     * to call when navigation menu row clicked
     *
     * @param message hold row data model
     */
    void onSideMenuItemClick(int message);
}
