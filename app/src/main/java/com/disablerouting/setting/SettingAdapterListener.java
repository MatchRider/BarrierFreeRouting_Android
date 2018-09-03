package com.disablerouting.setting;

import android.view.View;

public interface SettingAdapterListener {

    void OnIconEditViewOnClick(View v, int position);

    void OnIconCheckBoxOnClick(View v, int position, boolean isChecked);

}