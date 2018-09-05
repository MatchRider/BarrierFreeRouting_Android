package com.disablerouting.setting;

import android.view.View;
import com.disablerouting.curd_operations.model.Attributes;

public interface SettingAdapterListener {

    void OnIconEditViewOnClick(View v, int position);

    void OnIconCheckBoxOnClick(View v, int position, boolean isChecked, Attributes attributes);

}