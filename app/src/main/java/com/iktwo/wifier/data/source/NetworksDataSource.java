package com.iktwo.wifier.data.source;

import com.iktwo.wifier.data.AccessPoint;

import java.util.List;

public interface NetworksDataSource {
    void getNetworks(LoadNetworksCallback callback);

    void refreshNetworks();

    interface LoadNetworksCallback {
        void onNetworksLoaded(List<AccessPoint> networks);

        void onDataNotAvailable();
    }
}
