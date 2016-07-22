package com.bernardpletikosa.hc.handler;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

public class ControlServiceUtil {

    public static void startService(Context context) {
        context.startService(new Intent(context, ControlService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, ControlService.class));
    }

    public static boolean isRunning(Context context) {
        return isRunning(context, ControlService.class);
    }

    public static boolean isRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void restartService(Context context) {
        if (!ControlServiceUtil.isRunning(context)) return;
        stopService(context);
        startService(context);
    }
}
