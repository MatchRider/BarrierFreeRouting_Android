package com.disablerouting.sidemenu.model;


import com.disablerouting.R;

public enum SideMenuData {

    ACKNOWLEDGEMENTS(R.string.ACKNOWLEDGEMENTS, R.mipmap.ic_launcher),
    LOGOUT(R.string.LOGOUT, R.mipmap.ic_launcher),
    CONTACT(R.string.CONTACT, R.mipmap.ic_launcher),
    DISCLAIMER(R.string.DISCLAIMER, R.mipmap.ic_launcher),
    LEGAL(R.string.LEGAL, R.mipmap.ic_launcher);


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
