package com.bernardpletikosa.hc.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Storage {

    public final static String CONTROLS_DELIMITER = "##";
    public static final int REQUEST_RESOLVE_ERROR = 2376;
    public final static int REQUIRED_PERMISSION_REQUEST_CODE = 2121;

    //PROPERTIES CONSTANTS
    public final static String SERVICE_ENABLED_KEY = "control_service_key";
    public final static String VIBRATION_ENABLED_KEY = "vibration_key";
    public final static String NOTIFICATION_ENABLED_KEY = "notification_key";
    public final static String SECURITY_KEY = "security_key";

    //WEARABLE CONSTANTS
    public static final String WEAR_UPDATE_PATH = "/control_update";
    public static final String WEAR_UPDATE_DATA = "control_update_data";
    public static final String WEAR_COMMAND = "/control";

    //PREFS CONSTANTS
    private final static String SHARED_PREFS = "PREFERENCES";
    private final static String SHARED_PREFS_VIB = "vibration";
    private final static String SHARED_PREFS_NOTIF = "notification";
    private final static String SHARED_PREFS_AUTH = "authorization";
    private final static String CONTROLS = "controls";


    private static SharedPreferences PREFERENCES;
    private static Storage STORAGE;

    public static Storage instance(Context context) {
        if (STORAGE == null) STORAGE = new Storage(context);
        return STORAGE;
    }

    private Storage(Context context) {
        PREFERENCES = context.getSharedPreferences(Storage.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void vibration(boolean state) {
        PREFERENCES.edit().putBoolean(SHARED_PREFS_VIB, state).apply();
    }

    public boolean vibration() {
        return PREFERENCES.getBoolean(SHARED_PREFS_VIB, false);
    }

    public void notification(boolean state) {
        PREFERENCES.edit().putBoolean(SHARED_PREFS_NOTIF, state).apply();
    }

    public boolean notification() {
        return PREFERENCES.getBoolean(SHARED_PREFS_NOTIF, false);
    }

    public void auth(String string) {
        PREFERENCES.edit().putString(SHARED_PREFS_AUTH, string).apply();
    }

    public String auth() {
        return PREFERENCES.getString(SHARED_PREFS_AUTH, null);
    }

    public void saveControl(String controlName, int on, int off) {
        final Set<String> controls = PREFERENCES.getStringSet(CONTROLS, new HashSet<String>());
        storeControl(controls, controlName, on, off);
    }

    private void storeControl(Set<String> controls, String controlName, int on, int off) {
        controls.add(controlName + CONTROLS_DELIMITER + on + CONTROLS_DELIMITER + off);
        PREFERENCES.edit().putStringSet(CONTROLS, controls).apply();
    }

    public List<Control> getAllControls() {
        final Set<String> controls = PREFERENCES.getStringSet(CONTROLS, new HashSet<String>());
        List<Control> deviceControls = new ArrayList<>(controls.size());
        if (!controls.isEmpty()) for (String control : controls) deviceControls.add(new Control(control));
        return deviceControls;
    }

    public void deleteControl(String name) {
        final Set<String> controls = PREFERENCES.getStringSet(CONTROLS, new HashSet<String>());

        final Set<String> newControls = new HashSet<>();
        for (String control : controls) if (!control.contains(name)) newControls.add(control);

        if (newControls.isEmpty()) PREFERENCES.edit().remove(CONTROLS).apply();
        else PREFERENCES.edit().putStringSet(CONTROLS, newControls).apply();
    }

    public String[] getAllControlsAsArray() {
        final Set<String> controls = PREFERENCES.getStringSet(CONTROLS, new HashSet<String>());
        return controls.toArray(new String[controls.size()]);
    }

    public String getServerUrl(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("home_crtl.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("server_url", null);
        } catch (IOException e) {
            Log.e("Storage", "Server URL not loaded.");
            e.printStackTrace();
        }
        return null;
    }
}
