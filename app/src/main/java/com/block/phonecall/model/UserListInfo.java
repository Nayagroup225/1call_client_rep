package com.block.phonecall.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserListInfo {
    @SerializedName("device_id")
    @Expose
    private String deviceId;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    @SerializedName("identity")
    @Expose
    private String identity;

    @SerializedName("longi")
    @Expose
    private String longi;

    @SerializedName("lati")
    @Expose
    private String lati;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("block_number")
    @Expose
    private String blockNumber;

    public String getDeviceId(){
        return deviceId;
    }

    public String getIdentity(){
        return identity;
    }

    public String getState() {
        return state;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getLongi() {
        return longi;
    }

    public String getLati(){
        return lati;
    }

    public String getAddress() {
        return address;
    }

    public String getBlockNumber(){
        return blockNumber;
    }

}
