package com.bernardpletikosa.hc.handler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Vibrator;

import com.bernardpletikosa.hc.R;
import com.bernardpletikosa.hc.storage.Storage;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

public class NotificationHandler {

    private static Vibrator vibrator;

    public static void notify(Context context, Boolean result) {
        if (!Storage.instance(context).notification()) return;

        final Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_control_notif)
                .setDefaults(Notification.PRIORITY_LOW)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(result ? R.string.notif_cmd_sent : R.string.notif_cmd_fail));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(76228, builder.build());
    }

    public static void vibrate(Context context) {
        if (!Storage.instance(context).vibration()) return;

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                if (vibrator != null) vibrator = null;
                System.gc();
            }
        }).delaySubscription(2000, TimeUnit.MILLISECONDS).subscribe();
    }

    public static Notification notifyService(Context context, PendingIntent intent) {
        return new Notification.Builder(context)
                .setContentTitle(context.getText(R.string.notificationTitle))
                .setContentText(context.getText(R.string.notificationText))
                .setSmallIcon(R.drawable.ic_control_notif)
                .setContentIntent(intent)
                .build();
    }
}
