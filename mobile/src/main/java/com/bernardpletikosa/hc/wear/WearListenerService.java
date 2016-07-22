package com.bernardpletikosa.hc.wear;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.bernardpletikosa.hc.handler.HomeApiTask;
import com.bernardpletikosa.hc.storage.Storage;

public class WearListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (Storage.WEAR_COMMAND.equals(messageEvent.getPath())) {
            int mLatestCommand = byteArrayToInt(messageEvent.getData());
            new HomeApiTask(getApplicationContext()).execute(mLatestCommand);
        }
    }

    private int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
}
