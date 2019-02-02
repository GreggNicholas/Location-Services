package com.example.locationservices;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.locationservices.MainActivity.REQUEST_LOCATION_PERMISSION;

public class PermissionChecker {

    /**
     * Line 17 checks if user has granted the app the specific permission passsed in.
     * Line 20 checks if user permission is has  granted permission, if not, android asks to show permission dialog
     * imported PackageManager.PERMISSION_GRANTED & MainActivity.REQUEST_LOCATION_PERMISSION
     * @param activity
     * @param permission
     */
    public boolean check(Activity activity, String permission) {
        if (ActivityCompat.checkSelfPermission(activity, permission) == PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_LOCATION_PERMISSION);
        return false;
    }
}

