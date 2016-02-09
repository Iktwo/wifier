package com.iktwo.wifier.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iktwo.wifier.R;
import com.iktwo.wifier.adapter.AccessPointsAdapter;
import com.iktwo.wifier.data.AccessPoint;
import com.iktwo.wifier.data.WifiNetwork;
import com.iktwo.wifier.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    private AccessPointsAdapter mAccessPointsAdapter;
    private RecyclerView mRecyclerView;
    private List<AccessPoint> mAccessPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccessPoints = new ArrayList<>();

//        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRecyclerView.setAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(false);

        /// TODO: Here check if tablet
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAccessPointsAdapter = new AccessPointsAdapter(mAccessPoints,
                R.layout.delegate_wifi_network,
                R.layout.delegate_wifi_networks, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "clicked " + view);
            }
        });

        mRecyclerView.setAdapter(mAccessPointsAdapter);

        return view;
    }

    public void addWifiNetwork(WifiNetwork network) {
        mAccessPointsAdapter.addWifiNetwork(network);
    }

    public void clearNetworks() {
        mAccessPointsAdapter.clear();
    }
}
