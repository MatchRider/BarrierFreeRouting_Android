package com.disablerouting.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.Window;
import com.disablerouting.widget.DRLoader;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;

public class BaseFragmentImpl extends Fragment implements IFragmentBase {

    private DRLoader mLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void showProgress() {
        if (mLoader == null) {
            mLoader = new DRLoader(getActivity());
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
    public void showSnackBar(@StringRes int message) {
        if (message != 0 && getContext() != null) {
            Snackbar.with(getContext()).type(SnackbarType.MULTI_LINE).text(message).show(getActivity());
        }
    }

    @Override
    public void showSnackBar(String message) {
        if(getContext() != null)
        Snackbar.with(getContext()).type(SnackbarType.MULTI_LINE).text(message).show(getActivity());
    }

}
