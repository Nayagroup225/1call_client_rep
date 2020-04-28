package com.block.phonecall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.block.phonecall.MainActivity;
import com.block.phonecall.service.MainService;

public class RestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
//        Toast.makeText(context, "Restart Received : "+ intent.getAction(), Toast.LENGTH_LONG).show();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") || intent.getAction().equals("SERVICE_RESTART")) {
            //start your sercice
            context.startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}