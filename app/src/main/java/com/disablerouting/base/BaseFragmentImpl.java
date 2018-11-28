package com.disablerouting.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.Toast;
import com.disablerouting.widget.DRLoader;

import java.util.Objects;

public class BaseFragmentImpl extends Fragment implements IFragmentBase {

    private DRLoader mLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void showProgress() {
        if (mLoader == null) {
            mLoader = new DRLoader(Objects.requireNonNull(getActivity()));
            Window window = mLoader.getWindow();
            if ( window != null ) {
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            mLoader.setCancelable(false);
        }

        if (mLoader != null && !mLoader.isShowing()) {
            mLoader.show();
        }
    }

    @Override
    public void hideProgress() {
        if (mLoader != null && mLoader.isShowing())
            mLoader.dismiss();
    }

    @Override
    public void showToast(@StringRes int message) {
        if (message != 0 && getContext() != null && getActivity()!=null) {
            Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void showToast(String message) {
        if (getContext() != null && getActivity() != null) {
            Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
        }
    }

}

