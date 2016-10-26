package com.lakeel.altla.sample.android.content.pm;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

/**
 * Created by nabeta-a on 2016/06/10.
 */
public final class PackageManagerExtensions {

	public static boolean isPermissionGranted(int permission) {
		return PackageManager.PERMISSION_GRANTED == permission;
	}

	public static boolean isSinglePermissionGranted(@NonNull int[] permissions) {
		return 0 < permissions.length && isPermissionGranted(permissions[0]);
	}

	private PackageManagerExtensions() {
	}
}
