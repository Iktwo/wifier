package com.iktwo.wifier.networks;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.iktwo.wifier.data.AccessPoint;
import com.iktwo.wifier.data.WifiNetwork;
import com.iktwo.wifier.data.source.NetworksDataSource;
import com.iktwo.wifier.data.source.NetworksRepository;

import java.util.ArrayList;
import java.util.List;

public class NetworksPresenter implements NetworksContract.Presenter {
    @SuppressWarnings("unused")
    private static final String TAG = NetworksPresenter.class.getSimpleName();
    private final NetworksRepository mNetworksRepository;
    private final NetworksContract.View mNetworksView;

    private boolean mFirstLoad = true;

    public NetworksPresenter(NetworksRepository networksRepository, NetworksContract.View networksView) {
        mNetworksRepository = networksRepository;
        mNetworksView = networksView;

        mNetworksView.setPresenter(this);
    }

    @Override
    public void start() {
        loadNetworks(false);
    }

    @Override
    public void loadNetworks(boolean forceUpdate) {
        loadNetworks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public boolean hasPermission(Fragment fragment, String permission) {
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = fragment.getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return result;
    }

    @Override
    public void setPermissionToScanNetworks(boolean hasPermission) {
        if (hasPermission) {
            mNetworksView.hideNoPermissionToScanNetworksError();
        } else {
            mNetworksView.showNoPermissionToScanNetworksError();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(Fragment fragment, String permission) {
        return fragment.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void setDisplayPermissionToScanNetworksRationale(boolean display) {
        if (display) {
            mNetworksView.showPermissionToScanNetworksRationale();
        } else {
            mNetworksView.hidePermissionToScanNetworksRationale();
        }
    }

    @Override
    public void requestPermissions(Fragment fragment, String[] permissions, int requestCode) {
        fragment.requestPermissions(permissions, requestCode);
    }

    private void loadNetworks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mNetworksView.setLoadingIndicator(true);
        }

        if (forceUpdate) {
            mNetworksRepository.refreshNetworks();
        }

        mNetworksRepository.getNetworks(new NetworksDataSource.LoadNetworksCallback() {
            @Override
            public void onNetworksLoaded(List<AccessPoint> networks) {
                List<AccessPoint> networksToShow = new ArrayList<>();

                for (AccessPoint network : networks) {
                    networksToShow.add(network);
                }

                if (!mNetworksView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mNetworksView.setLoadingIndicator(false);
                }

                processNetworks(networksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                if (!mNetworksView.isActive()) {
                    return;
                }

                mNetworksView.showNoNetworks();
            }
        });
    }

    private void processNetworks(List<AccessPoint> networks) {
        if (networks.isEmpty()) {
            processEmptyNetworks();
        } else {
            mNetworksView.showNetworks(networks);
        }
    }

    private void processEmptyNetworks() {
        mNetworksView.showNoNetworks();
    }
}
