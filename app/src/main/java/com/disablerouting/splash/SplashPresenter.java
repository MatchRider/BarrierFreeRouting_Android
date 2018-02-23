package com.disablerouting.splash;


import java.util.Timer;
import java.util.TimerTask;

public class SplashPresenter {

    private ISplashView mISplashView;
    public static final int DURATION_OF_SPLASH = 3000;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public SplashPresenter(ISplashView iSplashView) {
        mISplashView = iSplashView;
    }

    /**
     * When splash screen is visible
     */
    public void onSplashVisible() {
        stopTimer();
        createTimer();
        mTimer.schedule(mTimerTask, DURATION_OF_SPLASH);
    }

    /**
     * When user destroy the app then automatically stop the timer
     */
    public void onSplashInvisible() {
        stopTimer();
    }

    /**
     * Stop an already started Timer and TimerTasks.
     */
    private void stopTimer() {
        // cancels the existing timer tasks
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        // cancels the existing Timer
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * Creates a new instances of the Timer and TimerTasks.
     */
    private void createTimer() {
        mTimer = new Timer();
        mTimerTask = new SplashTimer();
    }

    /**
     * Cancel Timer after some some duration which was specified.
     */
    private class SplashTimer extends TimerTask {
        @Override
        public void run() {
            stopTimer();
            mISplashView.onSplashTimeOut();
        }
    }


}
