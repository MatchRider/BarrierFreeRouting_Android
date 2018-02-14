package com.disablerouting.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import com.disablerouting.R;

public class DRLoader extends AlertDialog {

    public DRLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();

        setContentView(R.layout.fragment_loader);
    }
}
