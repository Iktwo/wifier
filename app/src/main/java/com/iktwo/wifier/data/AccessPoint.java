package com.iktwo.wifier.data;

import java.util.ArrayList;

public class AccessPoint {
    private ArrayList<WifiNetwork> mWifiNetworks = new ArrayList<>();

    public AccessPoint() {
    }

    public AccessPoint(WifiNetwork wifiNetwork) {
        mWifiNetworks.add(wifiNetwork);
    }

    public String getSsid() {
        if (mWifiNetworks.isEmpty()) {
            return "";
        } else {
            return mWifiNetworks.get(0).getSsid();
        }
    }

    public WifiNetwork getNetworkAt(int i) {
        return mWifiNetworks.get(i);
    }

    public int length() {
        return mWifiNetworks.size();
    }

    public void addNetwork(WifiNetwork network) {
        mWifiNetworks.add(network);
    }

    public ArrayList<WifiNetwork> getWifiNetworks() {
        return mWifiNetworks;
    }
}
