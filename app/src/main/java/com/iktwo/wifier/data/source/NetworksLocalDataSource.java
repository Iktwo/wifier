package com.iktwo.wifier.data.source;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.iktwo.wifier.WiFierApplication;
import com.iktwo.wifier.data.AccessPoint;
import com.iktwo.wifier.data.WifiNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworksLocalDataSource implements NetworksDataSource {
    @SuppressWarnings("unused")
    private static final String TAG = NetworksLocalDataSource.class.getSimpleName();
    private static NetworksLocalDataSource mInstance = null;
    private WifiManager mWifiManager;

    private NetworksLocalDataSource(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static NetworksLocalDataSource getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworksLocalDataSource(context);
        }

        return mInstance;
    }

    @Override
    public void getNetworks(LoadNetworksCallback callback) {
        // Log.d(TAG, "getNetworks");

        List<ScanResult> wifiList = mWifiManager.getScanResults();
        List<AccessPoint> networks = new ArrayList<>();
        Map<String, Integer> networksNames = new HashMap<>();
        Map<String, Boolean> networksAddress = new HashMap<>();

        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult result = wifiList.get(i);
            String manufacturer = WiFierApplication.manufacturers.get(result.BSSID.toUpperCase().substring(0, 8));

            if (manufacturer == null)
                manufacturer = "";

            // Ignore hidden networks that are visible to you
            if (result.SSID.isEmpty() && networksAddress.get(result.BSSID) != null) {
                continue;
            }

            if (networksNames.get(result.SSID) == null) {
                networks.add(new AccessPoint(new WifiNetwork(result.SSID,
                        result.BSSID, result.capabilities, manufacturer, result.level,
                        result.frequency)));

                networksNames.put(result.SSID, networks.size() - 1);
            } else {
                networks.get(networksNames.get(result.SSID)).addNetwork(
                        new WifiNetwork(result.SSID,
                                result.BSSID, result.capabilities, manufacturer, result.level,
                                result.frequency));

            }

            networksAddress.put(result.BSSID, true);
        }

        callback.onNetworksLoaded(networks);
    }

    @Override
    public void refreshNetworks() {
        mWifiManager.startScan();
    }
}
