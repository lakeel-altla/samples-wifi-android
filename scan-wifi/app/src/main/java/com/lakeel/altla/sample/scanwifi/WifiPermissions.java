package com.lakeel.altla.sample.scanwifi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import com.lakeel.altla.sample.android.content.ContextExtensions;

/**
 * Created by nabeta-a on 2016/06/10.
 */
public final class WifiPermissions {

	private static final String[] PERMISSIONS = {
			Manifest.permission.ACCESS_WIFI_STATE,
			Manifest.permission.CHANGE_WIFI_STATE
	};

	public static boolean isPermissionsGranted(@NonNull Context context) {
		return ContextExtensions.isPermissionGranted(
				context,
				PERMISSIONS);
	}

	public static void requestPermissions(@NonNull Activity activity, int requestCode) {
		ActivityCompat.requestPermissions(
				activity,
				PERMISSIONS,
				requestCode);
	}

	public static boolean isRequestedPermissionsGranted(@NonNull int[] grantResults) {
		if (grantResults.length != PERMISSIONS.length) {
			return false;
		}

		for (int grantResult : grantResults) {
			if (grantResult != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}

		return true;
	}

	private WifiPermissions() {
	}
}
