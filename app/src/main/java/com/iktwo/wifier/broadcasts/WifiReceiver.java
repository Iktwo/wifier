package com.iktwo.wifier.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.iktwo.wifier.R;
import com.iktwo.wifier.WiFierApplication;
import com.iktwo.wifier.data.WifiNetwork;

import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    private WifiBroadcastListener mListener;
    private WifiManager mWifiManager;

    public WifiReceiver(WifiBroadcastListener listener, WifiManager wifiManager) {
        mListener = listener;
        mWifiManager = wifiManager;
    }

    public void onReceive(Context c, Intent intent) {
        mListener.gotResults();

        List<ScanResult> wifiList = mWifiManager.getScanResults();

        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult result = wifiList.get(i);
            String manufacturer = WiFierApplication.manufacturers.get(result.BSSID.toUpperCase().substring(0, 8));

            if (manufacturer == null)
                manufacturer = "";

            mListener.newNetwork(new WifiNetwork(result.SSID,
                    result.BSSID, result.capabilities, manufacturer, result.level, result.frequency));
        }
    }

    public interface WifiBroadcastListener {
        void gotResults();
        void newNetwork(WifiNetwork wifiNetwork);
    }
}
