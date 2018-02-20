package com.disablerouting.common;

public class AlertDialogModel {

    private int icon;
    private int mTitle;
    private int mMsg;
    private int mYesBtn;
    private String mNoBtn;
    private int mAction;
    private boolean isCancelable;

    public boolean isCancelable() {
        return isCancelable;
    }

    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTitle() {
        return mTitle;
    }

    public void setTitle(int title) {
        this.mTitle = title;
    }

    public int getMsg() {
        return mMsg;
    }

    public void setMsg(int msg) {
        this.mMsg = msg;
    }

    public int getYesBtn() {
        return mYesBtn;
    }

    public void setYesBtn(int yesBtn) {
        this.mYesBtn = yesBtn;
    }

    public String getNoBtn() {
        return mNoBtn;
    }

    public void setNoBtn(String noBtn) {
        this.mNoBtn = noBtn;
    }

    public int getAction() {
        return mAction;
    }

    public void setAction(int mAction) {
        this.mAction = mAction;
    }
}
