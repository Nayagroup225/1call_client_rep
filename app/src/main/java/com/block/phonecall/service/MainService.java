package com.block.phonecall.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.block.phonecall.FakeCallScreenActivity;
import com.block.phonecall.R;
import com.block.phonecall.model.BaseRes;
import com.block.phonecall.preferences.AppSharedPreference;
import com.block.phonecall.receiver.RestartReceiver;
import com.block.phonecall.retrofit.ApiCall;
import com.block.phonecall.retrofit.IApiCallback;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Response;

public class MainService extends Service implements IApiCallback<BaseRes>, Handler.Callback{

    private static final String ACTION="android.intent.action.NEW_OUTGOING_CALL";
    private static final String ACTION_RESTART = "SERVICE_RESTART";
    private OutGoingCallReceiver outGoingCallReceiver;
    private RestartReceiver restartReceiver;

    public Handler mHandler;
    public boolean isWaitingResponse = false;
    LocalBroadcastManager mLocalBroadcastManager;

    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();
    // Define the code block to be executed
//    private Runnable runnableCode = new Runnable() {
//        @Override
//        public void run() {
//            // Do something here on the main thread
//            if(!isWaitingResponse) {
//                ApiCall.getInstance().checkCurrentState(preferences.getDeviceId(), MainService.this);
//                isWaitingResponse = true;
//            }
//            Log.d("Handlers", "Called on main thread");
//            // Repeat this the same runnable code block again another 2 seconds
//            handler.postDelayed(runnableCode, 5000);
//        }
//    };

    AppSharedPreference preferences;

    String[] mPhoneNumbers = {};

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(ACTION);
        this.outGoingCallReceiver = new OutGoingCallReceiver();
        this.registerReceiver(this.outGoingCallReceiver, theFilter);
        mHandler = new Handler(this);
        preferences =  AppSharedPreference.getInstance(this);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, new Notification());
        }

//        mPhoneNumbers = getApplication().getResources().getStringArray(R.array.phone_numbers);
        mPhoneNumbers = toStringArray(preferences.getLimitNumberArray());
        ApiCall.getInstance().getLimitNumbers(this);

//        handler.post(runnableCode);
        Runnable r = new Runnable() {
            public void run() {
                while (true) {
                    if (!isWaitingResponse) {
                        ApiCall.getInstance().checkCurrentState(preferences.getDeviceId(), MainService.this);
                        isWaitingResponse = true;
                    }
                    try {
                        Thread.sleep(10000);
                    }catch (Exception e){
                        Log.e("Error", e.toString());
                    }
                }
            }
        };
        new Thread(r).start();
        return START_STICKY;
    }

    public static String[] toStringArray(String jsonArray) {
        try {
            JSONArray array = new JSONArray(jsonArray);
            if(array==null)
                return new String[]{};

            String[] arr=new String[array.length()];
            for(int i=0; i<arr.length; i++) {
                arr[i]=array.getString(i);
            }
            return arr;
        }catch (Exception e){
            return new String[]{};
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Do not forget to unregister the receiver!!!
        this.unregisterReceiver(this.outGoingCallReceiver);
//        Intent i=new Intent("SERVICE_RESTART");
//        i.setClass(this, RestartReceiver.class);
//        this.sendBroadcast(i);

    }

    @Override
    public void onSuccess(String type, Response<BaseRes> response) {
        if(response.isSuccessful()){
            if(type.equals("state")) {
                if (!response.body().getErrorCode().equals("1")) {
                    AppSharedPreference.getInstance(this).setState(response.body().getErrorMsg());
                    AppSharedPreference.getInstance(this).setBlockNumber(response.body().getErrorCode());
                }
            }else if(type.equals("limit_numbers")){
                if (!response.body().getErrorCode().equals("1")) {
                    AppSharedPreference.getInstance(this).setLimitNumberArray(response.body().getErrorMsg());
                    mPhoneNumbers = toStringArray(preferences.getLimitNumberArray());
                }
            }
        }
        isWaitingResponse = false;
    }

    @Override
    public void onFailure(Object data) {
        isWaitingResponse = false;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
//                Intent intentKeypad = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:123456789"));
//                intentKeypad.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getApplicationContext().startActivity(intentKeypad);

//                Intent i1 = new Intent(getApplicationContext(), SplashActivity.class);
//                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                ActivityManager localActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//                for (String str = ((ActivityManager.RunningTaskInfo) localActivityManager.getRunningTasks(1).get(0)).topActivity.flattenToString(); ; str = ((ActivityManager.RunningTaskInfo) localActivityManager.getRunningTasks(1).get(0)).topActivity.flattenToString()) {
//                    if ((!str.contains("com.android.phone.incallui")))
//                        continue;
//                    Log.d("IncomingCallPlus", "*****************************************************");
//                    getApplicationContext().startActivity(i1);
//                    break;
//                }
                break;
            case 1:
                Intent intentActivity = new Intent(getApplicationContext(), FakeCallScreenActivity.class);
                intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentActivity.putExtra("out_call", msg.obj.toString());
                getApplicationContext().startActivity(intentActivity);
                break;

//            case 2:
//                String queryString = "NUMBER=" + AppSharedPreference.getInstance(getApplicationContext()).getBlockNumber();
//                if(checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
//                    this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
//                }
//                mLocalBroadcastManager.sendBroadcast(new Intent("call_disconnected"));
//                break;
        }
        return false;
    }

    public class OutGoingCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            // for outgoing call
            String outgoingPhoneNo = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER).toString();
//            Toast.makeText(context, outgoingPhoneNo, Toast.LENGTH_SHORT).show();

            if(AppSharedPreference.getInstance(context).getState().equals("1")){
                // prevent outgoing call
                for(String phone : mPhoneNumbers){
                    if(outgoingPhoneNo.equals(phone)){
                        setResultData(AppSharedPreference.getInstance(context).getBlockNumber());
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(1, outgoingPhoneNo),500);
                        return;
                    }
                }
            }
        }
    }

    class CheckStateThread extends Thread{

        @Override
        public void run() {
            while (true) {
                if (!isWaitingResponse) {
                    ApiCall.getInstance().checkCurrentState(preferences.getDeviceId(), MainService.this);
                    isWaitingResponse = true;
                }
                try {
                    Thread.sleep(10000);
                }catch (Exception e){
                    Log.e("Error", e.toString());
                }
            }
        }
    }
}
