package com.bernardpletikosa.hc.storage;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Constants {

    public static final String WEAR_COMMAND = "/control";
    public static final String WEAR_UPDATE_PATH = "/control_update";
    public static final String WEAR_UPDATE_DATA = "control_update_data";

    private static final String CONTROLS = "controls";
    private final static String SHARED_PREFS = "PREFERENCES";

    public static void updateControls(Context context, String[] controls) {
        context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                .edit().putStringSet(CONTROLS, new HashSet<>(Arrays.asList(controls))).apply();
    }

    public static List<Control> getAllControls(Context context) {
        final Set<String> controls = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                .getStringSet(CONTROLS, new HashSet<String>());

        List<Control> deviceControls = new ArrayList<>(controls.size());
        if (!controls.isEmpty()) for (String control : controls) deviceControls.add(new Control(control));

        return deviceControls;
    }
}
