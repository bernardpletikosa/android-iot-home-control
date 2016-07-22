package com.bernardpletikosa.hc.handler;

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.bernardpletikosa.hc.MainActivity;
import com.bernardpletikosa.hc.storage.Constants;

public class ListenerService extends WearableListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            String path = event.getDataItem().getUri().getPath();
            if (Constants.WEAR_UPDATE_PATH.equals(path)) {
                Intent startIntent = new Intent(this, MainActivity.class);
                DataMapItem dataItem = DataMapItem.fromDataItem(event.getDataItem());
                String[] controls = dataItem.getDataMap().getStringArray(Constants.WEAR_UPDATE_DATA);

                Constants.updateControls(getApplicationContext(), controls);

                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
            }
        }
    }
}
