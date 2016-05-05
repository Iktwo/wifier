package com.iktwo.wifier.adapter;

import android.net.wifi.WifiManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iktwo.wifier.R;
import com.iktwo.wifier.data.AccessPoint;
import com.iktwo.wifier.data.WifiNetwork;
import com.iktwo.wifier.utils.RecyclerItemClickListener;

import java.util.List;

public class AccessPointsAdapter extends RecyclerView.Adapter<AccessPointsAdapter.ViewHolder> {
    private static final int SINGLE = 0;
    private static final int GROUP = 1;
    private static final String TAG = AccessPointsAdapter.class.getSimpleName();

    private List<AccessPoint> items;
    private int mItemLayout;
    private int mGroupLayout;
    private int mExpandedIndex = -1;

    public AccessPointsAdapter(List<AccessPoint> items, int itemLayout, int groupLayout) {
        this.items = items;
        mItemLayout = itemLayout;
        mGroupLayout = groupLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).length() > 1 ? GROUP : SINGLE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;

        switch (viewType) {
            case SINGLE:
                v = LayoutInflater.from(parent.getContext()).inflate(mItemLayout, parent, false);
                break;
            case GROUP:
                v = LayoutInflater.from(parent.getContext()).inflate(mGroupLayout, parent, false);
                break;
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AccessPoint ap = items.get(position);
        WifiNetwork firstNetwork = ap.getNetworkAt(0);

        if (firstNetwork.getSsid().isEmpty()) {
            holder.ssid.setText(holder.itemView.getContext().getString(R.string.hidden_network));
        } else {
            holder.ssid.setText(firstNetwork.getSsid());
        }

        if (ap.length() > 1) {
            if (position == mExpandedIndex) {
                holder.innerRecyclerView.setVisibility(View.VISIBLE);
            } else {
                holder.innerRecyclerView.setVisibility(View.GONE);
            }

            holder.innerRecyclerView.setHasFixedSize(true);

            holder.innerRecyclerView.setLayoutManager(
                    new LinearLayoutManager(holder.itemView.getContext()));

            holder.innerRecyclerView.setItemAnimator(new DefaultItemAnimator());

            holder.innerRecyclerView.setAdapter(
                    new WifiNetworksAdapter(ap.getWifiNetworks(), R.layout.delegate_wifi_network));

            holder.innerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(holder.itemView.getContext(),
                    holder.innerRecyclerView,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Log.d(TAG, "Inner clicked " + view.toString().substring(0, 30) + "  -  " + position);
                        }

                        @Override
                        public void onItemLongPress(View view, int position) {
                            Log.d(TAG, "Inner onItemLongPress " + view.toString().substring(0, 30) + " - " + position);
                        }
                    }));
        } else {
            holder.bssid.setText(String.format("%s  -  %s  -  CH %s", firstNetwork.getBssid(), WifiNetwork.getSecurity(firstNetwork.getCapabilities()), WifiNetwork.frequencyToChannel(firstNetwork.getFrequency())));

            if (!firstNetwork.getManufacturer().isEmpty()) {
                holder.manufacturer.setVisibility(View.VISIBLE);
                holder.manufacturer.setText(firstNetwork.getManufacturer());
            } else {
                holder.manufacturer.setVisibility(View.GONE);
            }

            holder.strength.setImageLevel(WifiManager.calculateSignalLevel(firstNetwork.getLevel(), WifiNetwork.LEVELS));
        }

        holder.itemView.setTag(ap);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void toggleExpanded(int position) {
        // Log.d(TAG, "toggleExpanded: " + position + " previouslyExpanded: " + mExpandedIndex);
        int previouslyExpanded = mExpandedIndex;

        if (mExpandedIndex != position)
            mExpandedIndex = position;
        else
            mExpandedIndex = -1;

        if (previouslyExpanded != -1) {
            notifyItemChanged(previouslyExpanded);
        }

        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final public CardView cardView;
        final public TextView ssid;
        final public TextView bssid;
        final public TextView manufacturer;
        final public ImageView strength;
        final public RecyclerView innerRecyclerView;

        public ViewHolder(View view) {
            super(view);

            ssid = (TextView) view.findViewById(R.id.text_view_ssid);
            bssid = (TextView) view.findViewById(R.id.text_view_bssid);
            manufacturer = (TextView) view.findViewById(R.id.text_view_manufacturer);
            innerRecyclerView = (RecyclerView) view.findViewById(R.id.inner_recycler_view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            strength = (ImageView) view.findViewById(R.id.image_strength);
        }
    }
}

