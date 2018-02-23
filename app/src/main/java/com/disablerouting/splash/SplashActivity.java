package com.disablerouting.splash;

import android.os.Bundle;
import android.view.WindowManager;
import butterknife.ButterKnife;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.tutorial.TutorialActivity;


public class SplashActivity extends BaseActivityImpl implements ISplashView {

    private SplashPresenter mSplashPresenter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSplashPresenter = new SplashPresenter(SplashActivity.this);
        mSplashPresenter.onSplashVisible();

    }

    /**
     * @see ISplashView
     * listen when splash time out
     */
    @Override
    public void onSplashTimeOut() {
        if (!isFinishing() && !isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeOnSplashVisible();
                }
            });

        }
    }

    /**
     * @see ISplashView
     */
    @Override
    public void redirectToTutorialScreen() {
        if (isFinishing()) return;
        launchActivity(this, TutorialActivity.class);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSplashPresenter.onSplashInvisible();
    }

    @Override
    public void showLoader() {
        showProgress();
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }
    public void makeOnSplashVisible() {
        redirectToTutorialScreen();
    }
}
