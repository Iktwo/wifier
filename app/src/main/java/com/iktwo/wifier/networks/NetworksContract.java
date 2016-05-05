package com.iktwo.wifier.networks;

import android.support.v4.app.Fragment;

import com.iktwo.wifier.BasePresenter;
import com.iktwo.wifier.BaseView;
import com.iktwo.wifier.data.AccessPoint;

import java.util.List;

public interface NetworksContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showNetworks(List<AccessPoint> networks);

        void showNoNetworks();

        void showNoPermissionToScanNetworksError();

        void hideNoPermissionToScanNetworksError();

        void showPermissionToScanNetworksRationale();

        void hidePermissionToScanNetworksRationale();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {
        void loadNetworks(boolean forceUpdate);

        void setPermissionToScanNetworks(boolean hasPermission);

        void setDisplayPermissionToScanNetworksRationale(boolean display);

        void requestPermissions(Fragment fragment, String[] permissions, int requestCode);

        boolean hasPermission(Fragment fragment, String permission);

        boolean shouldShowRequestPermissionRationale(Fragment fragment, String permission);
    }
}
