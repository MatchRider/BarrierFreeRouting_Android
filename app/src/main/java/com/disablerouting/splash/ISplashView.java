package com.disablerouting.splash;


import com.disablerouting.common.ILoader;

public interface ISplashView extends ILoader {

    /**
     * call by presenter when timer expire.
     */
    void onSplashTimeOut();

    /**
     * To redirect to welcome screen
     */
    void redirectToTutorialScreen();


}
