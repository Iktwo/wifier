package com.iktwo.wifier.data.source;

import com.iktwo.wifier.data.AccessPoint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NetworksRepository implements NetworksDataSource {
    private static NetworksRepository mInstance = null;
    private final NetworksDataSource mNetworksLocalDataSource;

    private Map<String, AccessPoint> mCachedNetworks;

    private boolean mCacheIsDirty = false;

    private NetworksRepository(NetworksDataSource networksLocalDataSource) {
        mNetworksLocalDataSource = networksLocalDataSource;
    }

    public static NetworksRepository getInstance(NetworksDataSource networksDataSource) {
        if (mInstance == null) {
            mInstance = new NetworksRepository(networksDataSource);
        }

        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    @Override
    public void getNetworks(LoadNetworksCallback callback) {
        if (mCachedNetworks != null && !mCacheIsDirty) {
            callback.onNetworksLoaded(new ArrayList<>(mCachedNetworks.values()));
            return;
        }

        getNetworksFromRemoteDataSource(callback);
    }

    private void refreshCache(List<AccessPoint> networks) {
        if (mCachedNetworks == null) {
            mCachedNetworks = new LinkedHashMap<>();
        }

        mCachedNetworks.clear();

        for (AccessPoint network : networks) {
            mCachedNetworks.put(network.getSsid(), network);
        }

        mCacheIsDirty = false;
    }

    private void getNetworksFromRemoteDataSource(final LoadNetworksCallback callback) {
        mNetworksLocalDataSource.getNetworks(new LoadNetworksCallback() {
            @Override
            public void onNetworksLoaded(List<AccessPoint> networks) {
                refreshCache(networks);
                callback.onNetworksLoaded(new ArrayList<>(mCachedNetworks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void refreshNetworks() {
        mNetworksLocalDataSource.refreshNetworks();
        mCacheIsDirty = true;
    }
}

