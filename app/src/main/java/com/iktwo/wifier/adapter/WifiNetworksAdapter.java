package com.iktwo.wifier.adapter;

import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iktwo.wifier.R;
import com.iktwo.wifier.data.WifiNetwork;

import java.util.List;

public class WifiNetworksAdapter extends RecyclerView.Adapter<WifiNetworksAdapter.ViewHolder> {
    private List<WifiNetwork> items;
    private int mItemLayout;

    public WifiNetworksAdapter(List<WifiNetwork> items, int itemLayout) {
        this.items = items;
        mItemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(mItemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WifiNetwork network = items.get(position);

        holder.ssid.setText(String.format("%s  -  %s  -  CH %s", network.getBssid(), WifiNetwork.getSecurity(network.getCapabilities()), WifiNetwork.frequencyToChannel(network.getFrequency())));
        holder.bssid.setVisibility(View.GONE);

        if (!network.getManufacturer().isEmpty()) {
            holder.manufacturer.setVisibility(View.VISIBLE);
            holder.manufacturer.setText(network.getManufacturer());
        } else {
            holder.manufacturer.setVisibility(View.GONE);
        }

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.innerCardViewBackground));
        holder.itemView.setTag(network);

        holder.strength.setImageLevel(WifiManager.calculateSignalLevel(network.getLevel(), WifiNetwork.LEVELS));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        int size = items.size();
        if (size > 0) {
            for (int i = 0; i < size; i++)
                items.remove(0);

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ssid;
        public TextView bssid;
        public TextView manufacturer;
        public ImageView strength;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            ssid = (TextView) view.findViewById(R.id.text_view_ssid);
            bssid = (TextView) view.findViewById(R.id.text_view_bssid);
            manufacturer = (TextView) view.findViewById(R.id.text_view_manufacturer);
            cardView = (CardView) view.findViewById(R.id.card_view);
            strength = (ImageView) view.findViewById(R.id.image_strength);
        }
    }
}

