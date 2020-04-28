package com.block.phonecall.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.block.phonecall.preferences.AppSharedPreference;

public class OutGoingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        // for outgoing call
        String outgoingPhoneNo = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER).toString();
//        Toast.makeText(context, outgoingPhoneNo, Toast.LENGTH_SHORT).show();

        if(AppSharedPreference.getInstance(context).getState().equals("1")){
            // prevent outgoing call
            setResultData(AppSharedPreference.getInstance(context).getBlockNumber());
        }
    }
}