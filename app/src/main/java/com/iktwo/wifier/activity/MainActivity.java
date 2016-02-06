package com.iktwo.wifier.activity;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.iktwo.wifier.R;
import com.iktwo.wifier.broadcasts.WifiReceiver;
import com.iktwo.wifier.data.WifiNetwork;
import com.iktwo.wifier.fragment.MainFragment;

public class MainActivity extends AppCompatActivity implements WifiReceiver.WifiBroadcastListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final int PERMISSION_REQUEST_COARSE_LOCATION = 128;

    private WifiManager mWifiManager;
    private WifiReceiver mWifiReceiver;
    private View mLayout;
    private View mViewNoPermission;
    private Snackbar mSnackbarNoPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiReceiver(this, mWifiManager);

        setContentView(R.layout.activity_main);

        mViewNoPermission = findViewById(R.id.text_view_no_permission);

        mLayout = findViewById(R.id.coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /// TODO: ask if enable
        if (!mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(true);
    }

    private void hideNoPermission() {
        mViewNoPermission.setVisibility(View.GONE);

        if (mSnackbarNoPermission != null)
            mSnackbarNoPermission.dismiss();
    }

    private void showNoPermission() {
        mViewNoPermission.setVisibility(View.VISIBLE);

        if (mSnackbarNoPermission == null) {
            mSnackbarNoPermission = Snackbar.make(mLayout, R.string.location_permission_required,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
        }

        mSnackbarNoPermission.show();
    }

    private void scanWifiNetworks() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mWifiManager.startScan();
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(mLayout, R.string.location_permission_required,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }
            }).show();

        } else {
            Snackbar.make(mLayout, R.string.location_permission_not_available,
                    Snackbar.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hideNoPermission();
                    mWifiManager.startScan();
                } else {
                    Snackbar.make(mLayout, R.string.location_permission_denied,
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mWifiReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            hideNoPermission();

            if (mWifiManager.isWifiEnabled())
                scanWifiNetworks();
        } else {
            showNoPermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void newNetwork(WifiNetwork wifiNetwork) {
        // Log.d(TAG, "Got new network: " + wifiNetwork);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_main);

        if (f instanceof MainFragment) {
            ((MainFragment) f).addWifiNetwork(wifiNetwork);
        } else {
            Log.e(TAG, "Error with fragment");
        }
    }

    @Override
    public void gotResults() {
        // Log.d(TAG, "Got new network: " + wifiNetwork);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_main);

        if (f instanceof MainFragment) {
            ((MainFragment) f).clearNetworks();
        } else {
            Log.e(TAG, "Error with fragment");
        }
    }
}