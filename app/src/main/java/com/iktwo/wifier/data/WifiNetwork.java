package com.iktwo.wifier.data;

import android.os.Parcel;
import android.os.Parcelable;

public class WifiNetwork implements Parcelable {
    public static final Parcelable.Creator<WifiNetwork> CREATOR = new Parcelable.Creator<WifiNetwork>() {
        public WifiNetwork createFromParcel(Parcel source) {
            return new WifiNetwork(source);
        }

        public WifiNetwork[] newArray(int size) {
            return new WifiNetwork[size];
        }
    };
    private static final String WPA2 = "WPA2";
    private static final String WPA = "WPA";
    private static final String WEP = "WEP";
    private static final String OPEN = "Open";
    private static final String WPA_EAP = "WPA-EAP";
    private static final String IEEE8021X = "IEEE8021X";
    private String ssid;
    private String bssid;
    private String capabilities;
    private String manufacturer;
    private int level;
    private int frequency;

    public WifiNetwork() {
    }
    public WifiNetwork(String ssid, String bssid, String capabilities, String manufacturer, int level, int frequency) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.manufacturer = manufacturer;
        this.level = level;
        this.frequency = frequency;
    }

    protected WifiNetwork(Parcel in) {
        this.ssid = in.readString();
        this.bssid = in.readString();
        this.capabilities = in.readString();
        this.manufacturer = in.readString();
        this.level = in.readInt();
        this.frequency = in.readInt();
    }

    public static int frequencyToChannel(int freq) {
        if (freq == 2484)
            return 14;
        else if (freq < 2484)
            return (freq - 2407) / 5;
        else if (freq >= 4910 && freq <= 4980)
            return (freq - 4000) / 5;
        else if (freq <= 45000) /* DMG band lower limit */
            return (freq - 5000) / 5;
        else if (freq >= 58320 && freq <= 64800)
            return (freq - 56160) / 2160;
        else
            return 0;
    }

    public static String getSecurity(String capabilities) {
        final String[] securityModes = {WEP, WPA, WPA2, WPA_EAP, IEEE8021X};

        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (capabilities.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return OPEN;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ssid);
        dest.writeString(this.bssid);
        dest.writeString(this.capabilities);
        dest.writeString(this.manufacturer);
        dest.writeInt(this.level);
        dest.writeInt(this.frequency);
    }

    @Override
    public String toString() {
        return ssid + " - " + bssid + " - " + capabilities + " - " + manufacturer + " - " + level + " - " + frequency;
    }
}