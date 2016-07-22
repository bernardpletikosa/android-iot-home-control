package com.bernardpletikosa.hc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.bernardpletikosa.hc.storage.Constants;
import com.bernardpletikosa.hc.storage.Control;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Node mNode;
    private GoogleApiClient mGoogleApiClient;

    private GridViewPager mPager;
    private DotsPageIndicator mPagerIndicator;
    private TextView mPagerWarning;

    private GridAdapter mGridAdapter;
    private List<Control> mControls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mControls.addAll(Constants.getAllControls(this));
        mGridAdapter = new GridAdapter(MainActivity.this, mControls);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mPager = (GridViewPager) findViewById(R.id.pager);
                mPagerWarning = (TextView) findViewById(R.id.pager_warning);
                mPagerIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
                mPagerIndicator.setDotColor(R.color.colorAccent);

                mPager.setAdapter(mGridAdapter);
                mPagerIndicator.setPager(mPager);
                refreshControls();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {}

    private void refreshControls() {
        mControls.clear();
        mControls.addAll(Constants.getAllControls(this));
        if (mGridAdapter != null) mGridAdapter.notifyDataSetChanged();

        if (mPager == null) return;
        mPager.setVisibility(mControls.isEmpty() ? View.GONE : View.VISIBLE);
        mPagerWarning.setVisibility(mControls.isEmpty() ? View.VISIBLE : View.GONE);
        mPagerIndicator.setVisibility(mControls.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void resolveNode() {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) mNode = node;
            }
        });
    }

    public void sendMessage(Integer command) {
        if (mNode != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            final byte[] cmd = intToByteArray(command);
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), Constants.WEAR_COMMAND, cmd).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(@NonNull MessageApi.SendMessageResult result) {
                            if (!result.getStatus().isSuccess())
                                Log.e("MainActivity", "Failed to send: " + result.getStatus().getStatusCode());
                        }
                    }
            );

            mPager.setBackgroundResource(R.color.colorAccent);
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    mPager.setBackgroundResource(R.color.colorPrimaryDark);
                }
            }, 500);
        } else {
            Log.e("MainActivity", "Send message failed");
        }
    }

    private byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
}
