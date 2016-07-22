package com.bernardpletikosa.hc.handler.widget;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

public class WidgetUtil {

    public static void startService(Context context) {
        context.startService(new Intent(context, WidgetService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, WidgetService.class));
    }

    public static boolean isRunning(Context context) {
        return isRunning(context, WidgetService.class);
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
        if (!WidgetUtil.isRunning(context)) return;
        stopService(context);
        startService(context);
    }
}
