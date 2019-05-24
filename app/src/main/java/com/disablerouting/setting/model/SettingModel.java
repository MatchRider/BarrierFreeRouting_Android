package com.disablerouting.setting.model;

public class SettingModel {

    private String keyString;

    private int keyPosition;

    public SettingModel(int keyPosition,String keyString) {
        this.keyPosition = keyPosition;
        this.keyString = keyString;
    }

    public String getKeyString() {
        return keyString;
    }

    public int getKeyPosition() {
        return keyPosition;
    }
}
