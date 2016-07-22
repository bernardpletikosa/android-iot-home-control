package com.bernardpletikosa.hc.ui.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.bernardpletikosa.hc.R;
import com.bernardpletikosa.hc.handler.ControlServiceUtil;
import com.bernardpletikosa.hc.storage.Storage;
import com.bernardpletikosa.hc.handler.PermissionChecker;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private PermissionChecker mPermissionChecker;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionChecker = new PermissionChecker(getActivity());

        mSharedPreferences = getPreferenceManager().getSharedPreferences();

        if (!mPermissionChecker.isRequiredPermissionGranted()) {
            enableControlService(false);
            Intent intent = mPermissionChecker.createRequiredPermissionIntent();
            startActivityForResult(intent, Storage.REQUIRED_PERMISSION_REQUEST_CODE);
        } else {
            enableControlService(true);
            if (mSharedPreferences.getBoolean(Storage.SERVICE_ENABLED_KEY, false)
                    && !ControlServiceUtil.isRunning(getActivity()))
                ControlServiceUtil.startService(getActivity());
        }

        setUpNotification();
    }

    @Override public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }

    @TargetApi(Build.VERSION_CODES.M) @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != Storage.REQUIRED_PERMISSION_REQUEST_CODE) return;

        if (mPermissionChecker.isRequiredPermissionGranted()) enableControlService(true);
        else if (getView() != null)
            Snackbar.make(getView(), R.string.fragment_settings_permission, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (Storage.SERVICE_ENABLED_KEY.equals(key)) {
            boolean enabled = prefs.getBoolean(key, false);
            if (enabled && !ControlServiceUtil.isRunning(getActivity()))
                ControlServiceUtil.startService(getActivity());
            else
                ControlServiceUtil.stopService(getActivity());
        } else if (Storage.VIBRATION_ENABLED_KEY.equals(key)) {
            Storage.instance(getActivity()).vibration(prefs.getBoolean(key, false));
        } else if (Storage.NOTIFICATION_ENABLED_KEY.equals(key)) {
            Storage.instance(getActivity()).notification(prefs.getBoolean(key, false));
        } else if (Storage.SECURITY_KEY.equals(key)) {
            Storage.instance(getActivity()).auth(prefs.getString(key, ""));
        }
    }

    private void enableControlService(boolean enabled) {
        getPreferenceScreen().findPreference(Storage.SERVICE_ENABLED_KEY).setEnabled(enabled);
    }

    private void setUpNotification() {
        ((CheckBoxPreference) getPreferenceScreen().findPreference(Storage.VIBRATION_ENABLED_KEY))
                .setChecked(Storage.instance(getActivity()).vibration());
        ((CheckBoxPreference) getPreferenceScreen().findPreference(Storage.NOTIFICATION_ENABLED_KEY))
                .setChecked(Storage.instance(getActivity()).notification());
    }
}
