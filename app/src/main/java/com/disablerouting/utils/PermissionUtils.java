package com.disablerouting.utils;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

public class PermissionUtils {
    
    /**
     * To check permission is allowed or not
     * @param activity App context
     * @param permission Type of permission
     * @return Returns true if allowed otherwise false
     */
    public static boolean isPermissionAllowed(final AppCompatActivity activity, final String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = activity.checkSelfPermission(permission);
            return perm == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
