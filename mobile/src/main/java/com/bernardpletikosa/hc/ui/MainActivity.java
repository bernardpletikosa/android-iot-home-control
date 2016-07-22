package com.bernardpletikosa.hc.ui;

import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;

import com.bernardpletikosa.hc.R;
import com.bernardpletikosa.hc.storage.Storage;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.bernardpletikosa.hc.handler.ControlServiceUtil;
import com.bernardpletikosa.hc.ui.fragment.MainFragment;
import com.bernardpletikosa.hc.ui.fragment.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback,
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.tabs) TabLayout mTabView;
    @BindView(R.id.viewpager) ViewPager mViewPager;

    private Fragment[] mFragments = new Fragment[2];
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupViewPager(state);
        setUpTabs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        createClient();
    }

    @Override public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen pref) {
        if (mFragments[1] == null) return false;
        ((SettingsFragment) mFragments[1]).setPreferenceScreen(pref);
        return true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mResolvingError = false;
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (mResolvingError) return;
        if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, Storage.REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            mResolvingError = false;
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
        }
    }

    @Override public void onDataChanged(DataEventBuffer dataEventBuffer) {}

    public void updateAndRestart() {
        ControlServiceUtil.restartService(this);

        DataMap dataMap = new DataMap();
        dataMap.putStringArray(Storage.WEAR_UPDATE_DATA, Storage.instance(this).getAllControlsAsArray());
        new SendToDataLayerThread(Storage.WEAR_UPDATE_PATH, dataMap).start();
    }

    private void setupViewPager(Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                getSupportFragmentManager() != null &&
                getSupportFragmentManager().getFragments() != null) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof SettingsFragment)
                    mFragments[0] = fragment;
                else if (fragment instanceof MainFragment)
                    mFragments[1] = fragment;
            }
        }
        if (mFragments[0] == null) mFragments[0] = new SettingsFragment();
        if (mFragments[1] == null) mFragments[1] = new MainFragment();

        if (getSupportFragmentManager() == null) return;
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFrag(mFragments[0], getString(R.string.fragment_settings_title));
        mViewPagerAdapter.addFrag(mFragments[1], getString(R.string.fragment_main_title));

        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private void setUpTabs() {
        mTabView.setupWithViewPager(mViewPager);
        mTabView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                final int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void createClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (!mResolvingError) mGoogleApiClient.connect();
    }

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            if (mGoogleApiClient == null) return;
            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
        }
    }
}
