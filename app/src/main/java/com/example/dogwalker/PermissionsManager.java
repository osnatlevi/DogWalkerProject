package com.example.dogwalker;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import android.app.Activity;

import androidx.core.app.ActivityCompat;

public class PermissionsManager {

    public static boolean askPermissions(Activity activity, String permission) {
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), permission) == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
            return false;
        } else
            return true;
    }

    public static boolean askPermissions(Activity activity, String[] permission) {
        boolean noPerms = false;
        for (String perm : permission) {
            if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), perm) == PERMISSION_DENIED) {
                noPerms = true;
            }
        }
        if (noPerms) {
            ActivityCompat.requestPermissions(activity, permission, 0);
            return false;
        } else
            return true;
    }
}
