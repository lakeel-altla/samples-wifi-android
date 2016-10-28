package com.lakeel.altla.sample.wifibatterydrain;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;

public final class PermissionManager {

    private final Activity mActivity;

    private final String[] mPermissions;

    private PermissionManager(Activity activity, String[] permissions) {
        mActivity = activity;
        mPermissions = permissions;
    }

    public boolean isPermissionsGranted() {
        for (String permission : mPermissions) {
            if (!isPermissionGranted(mActivity, permission)) {
                return false;
            }
        }
        return true;
    }

    public void requestPermissions(int requestCode) {
        ActivityCompat.requestPermissions(mActivity, mPermissions, requestCode);
    }

    public boolean isRequestedPermissionsGranted(@NonNull int[] grantResults) {
        if (grantResults.length != mPermissions.length) {
            return false;
        }

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private static boolean isPermissionGranted(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static class Builder {

        private final Activity mActivity;

        private final Set<String> mPermissions = new HashSet<>();

        public Builder(@NonNull Activity activity) {
            mActivity = activity;
        }

        public Builder addPermission(@NonNull String permission) {
            mPermissions.add(permission);
            return this;
        }

        public PermissionManager build() {
            String[] array = new String[mPermissions.size()];
            mPermissions.toArray(array);
            return new PermissionManager(mActivity, array);
        }
    }
}
