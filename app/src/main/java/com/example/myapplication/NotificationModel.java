package com.example.myapplication;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Keep
public class NotificationModel implements Serializable {

    @SerializedName("longitude")
    public  String longitude="";

    @Override
    public String toString() {
        return "NotificationModel{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }

    @SerializedName("latitude")
    public  String latitude="";

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


}
