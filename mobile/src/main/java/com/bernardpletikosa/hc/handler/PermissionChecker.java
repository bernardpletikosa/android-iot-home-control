package com.bernardpletikosa.hc.handler;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class PermissionChecker {

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isRequiredPermissionGranted(Context context) {
        return !isMarshmallowOrHigher() || Settings.canDrawOverlays(context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static Intent createRequiredPermissionIntent(Context context) {
        if (!isMarshmallowOrHigher()) return null;
        return new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.getPackageName()));
    }

    private static boolean isMarshmallowOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
