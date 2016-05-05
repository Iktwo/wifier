package com.iktwo.wifier.networks;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.iktwo.wifier.R;
import com.iktwo.wifier.adapter.AccessPointsAdapter;
import com.iktwo.wifier.data.AccessPoint;
import com.iktwo.wifier.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class NetworksFragment extends Fragment implements NetworksContract.View {
    private static final String TAG = NetworksFragment.class.getSimpleName();
    private final int PERMISSION_REQUEST_COARSE_LOCATION = 128;
    private NetworksContract.Presenter mPresenter;
    private View mLayout;
    private Snackbar mSnackBackPermissionRationale;
    private View mViewNoPermission;
    private RecyclerView mRecyclerView;
    private AccessPointsAdapter mAccessPointsAdapter;
    private List<AccessPoint> mAccessPoints;
    private ProgressBar mBusyIndicator;

    public NetworksFragment() {

    }

    public static NetworksFragment newInstance() {
        return new NetworksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccessPoints = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();

        boolean hasPermission =
                mPresenter.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        mPresenter.setPermissionToScanNetworks(hasPermission);

        if (!hasPermission) {
            if (mPresenter.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                mPresenter.setDisplayPermissionToScanNetworksRationale(true);
            } else {
                mPresenter.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        } else {
            mPresenter.loadNetworks(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRecyclerView.setAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.networks_frag, container, false);

        mViewNoPermission = view.findViewById(R.id.text_view_no_permission);

        mLayout = view;

        mBusyIndicator = (ProgressBar) view.findViewById(R.id.busy_indicator);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);

        /// TODO: Here check if tablet
        // mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAccessPointsAdapter = new AccessPointsAdapter(mAccessPoints,
                R.layout.delegate_wifi_network,
                R.layout.delegate_wifi_networks);

        mRecyclerView.setAdapter(mAccessPointsAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                mRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(TAG, "Outer clicked " + view.toString().substring(0, 30) + "  -  " + position);

                        if (view.getTag() instanceof AccessPoint) {
                            AccessPoint ap = (AccessPoint) view.getTag();

                            if (ap.length() > 1) {
                                mAccessPointsAdapter.toggleExpanded(position);
                            }
                        }
                    }

                    @Override
                    public void onItemLongPress(View view, int position) {
                        Log.d(TAG, "Outer onItemLongPress " + view.toString().substring(0, 30) + " - " + position);
                    }
                }));

        return view;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showNoPermissionToScanNetworksError() {
        mViewNoPermission.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoPermissionToScanNetworksError() {
        mViewNoPermission.setVisibility(View.GONE);
    }

    @Override
    public void showPermissionToScanNetworksRationale() {
        mSnackBackPermissionRationale = Snackbar.make(mLayout,
                R.string.location_permission_required,
                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.requestPermissions(NetworksFragment.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });

        mSnackBackPermissionRationale.show();
    }

    @Override
    public void hidePermissionToScanNetworksRationale() {
        if (mSnackBackPermissionRationale != null) {
            mSnackBackPermissionRationale.dismiss();
        }
    }

    @Override
    public void showNoNetworks() {
        Log.d(TAG, "showNoNetworks");
    }

    @Override
    public void showNetworks(List<AccessPoint> networks) {
        // Log.d(TAG, "showNetworks");

        mAccessPointsAdapter.clear();
        mAccessPoints.clear();

        for (int i = 0; i < networks.size(); ++i) {
            mAccessPoints.add(networks.get(i));
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        // Log.d(TAG, "setLoadingIndicator: " + active);

        if (active) {
            mBusyIndicator.setVisibility(View.VISIBLE);
        } else {
            mBusyIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPresenter(NetworksContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Log.d(TAG, "onRequestPermissionsResult: " + requestCode);

        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPresenter.loadNetworks(true);
        }
    }
}
