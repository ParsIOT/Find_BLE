package com.parsin.bletool.internal.wifi;

import android.os.Parcel;
import android.os.Parcelable;



public class WifiObject implements Parcelable {

    public String wifiName;
    public String grpName;
    public String userName;

    // constructor
    public WifiObject(String wifi, String group, String user) {
        this.wifiName = wifi;
        this.grpName = group;
        this.userName = user;
    }

    protected WifiObject(Parcel in) {
        wifiName = in.readString();
        grpName = in.readString();
        userName = in.readString();
    }

    public static final Creator<WifiObject> CREATOR = new Creator<WifiObject>() {
        @Override
        public WifiObject createFromParcel(Parcel in) {
            return new WifiObject(in);
        }

        @Override
        public WifiObject[] newArray(int size) {
            return new WifiObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(wifiName);
        dest.writeString(grpName);
        dest.writeString(userName);
    }
}
