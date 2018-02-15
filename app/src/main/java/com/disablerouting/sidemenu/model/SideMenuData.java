package com.disablerouting.sidemenu.model;


import com.disablerouting.R;

public enum SideMenuData {

    ITEM_ONE(R.string.ITEM_ONE, R.mipmap.ic_launcher),
    ITEM_TWO(R.string.ITEM_TWO, R.mipmap.ic_launcher);

    private int iconId;
    private int titleID;

    SideMenuData(int titleID, int iconId) {
        this.titleID = titleID;
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

    public int getTitleID() {
        return titleID;
    }

}
