package com.block.phonecall.retrofit;

import com.block.phonecall.model.BaseRes;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface APIService {
    @FormUrlEncoded
    @POST("registerWithDeviceId")
    Call<BaseRes> register(@Field("device_id") String deviceId,
                           @Field("phone_number") String phone,
                           @Field("identity") String identity);

    @FormUrlEncoded
    @POST("check_current_state")
    Call<BaseRes> checkCurrentState(@Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("sendLastCall")
    Call<BaseRes> sendLastCall(@Field("last_call") String lastCall,
                               @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("getBlockNumbers")
    Call<BaseRes> getLimitNumbers(@Field("last_call") String lastCall);

}