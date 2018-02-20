package com.disablerouting.location;


import android.location.Location;

public interface LocationTracker {
    public interface LocationUpdateListener{
        public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime);
    }

    public void start(LocationUpdateListener update);

}
