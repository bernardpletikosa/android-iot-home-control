package com.bernardpletikosa.hc.handler;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.bernardpletikosa.hc.ui.MainActivity;
import com.bernardpletikosa.hc.ui.widget.ControlWidget;

public class ControlService extends Service {

    private final static int FOREGROUND_ID = 999;

    private ControlWidget mWidget;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mWidget = new ControlWidget(this);

        startForeground(FOREGROUND_ID, NotificationHandler.notifyService(getApplicationContext(), createIntent()));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyHeadLayer();
        stopForeground(true);
    }

    private void destroyHeadLayer() {
        if (mWidget != null) mWidget.destroy();
        mWidget = null;
    }

    private PendingIntent createIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
