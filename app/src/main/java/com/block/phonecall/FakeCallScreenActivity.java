package com.block.phonecall;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.internal.telephony.ITelephony;
import com.block.phonecall.preferences.AppSharedPreference;
import com.block.phonecall.retrofit.ApiCall;
import com.block.phonecall.retrofit.IApiCallback;

import java.lang.reflect.Method;

import retrofit2.Response;

public class FakeCallScreenActivity extends AppCompatActivity implements Handler.Callback, IApiCallback {

    ImageView mDeclineBtn;
    RelativeLayout mRelativeCallEnded;
    Handler mHandler;
    Window window;

    private PhoneCallStateReceiver mCallreceiver;

//    LocalBroadcastManager mLocalBroadcastManager;
//    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals("call_disconnected")){
//
//            }
//        }
//    };

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what){
            case 0:
                String queryString = "NUMBER=" + AppSharedPreference.getInstance(getApplicationContext()).getBlockNumber();
                if(checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
                }
                finish();
                break;
            case 1:
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.end_back));
                mRelativeCallEnded.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessageDelayed(0, 3000);
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.switchThumbkNormal));

        this.mCallreceiver = new PhoneCallStateReceiver();
        this.registerReceiver(this.mCallreceiver,new IntentFilter("android.intent.action.PHONE_STATE"));

        String outGoingCall = getIntent().getStringExtra("out_call");
        mRelativeCallEnded = findViewById(R.id.relative_call_end);
        TextView outgoingCallNumber = findViewById(R.id.tv_outgoing_number);
        outgoingCallNumber.setText(outGoingCall);

        TextView endendCallNumber = findViewById(R.id.tv_call_ended_number);
        endendCallNumber.setText(outGoingCall);

//        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
//        IntentFilter mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction("call_disconnected");
//        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

        mDeclineBtn = (ImageView) findViewById(R.id.iv_decline_call);
        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectCall();
            }
        });

        mHandler = new Handler(this);

        sendLastCallNumber(outGoingCall);

    }

    protected void sendLastCallNumber(String number){
        ApiCall.getInstance().sendLastCall(number, AppSharedPreference.getInstance(this).getDeviceId(), this);
    }

    public void disconnectCall(){
        try{
            TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            Class c = Class.forName(manager.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephony = (ITelephony)m.invoke(manager);
            telephony.endCall();
        } catch(Exception e){
            Log.d("",e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mCallreceiver);
    }

    @Override
    public void onBackPressed() {
    }

    class PhoneCallStateReceiver extends BroadcastReceiver {
        private TelephonyManager mTelephonyManager;
        @Override
        public void onReceive(final Context context, Intent intent) {



            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                Toast.makeText(context, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(1, 0);
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
               // Toast.makeText(context, "CALL_STATE_RINGING", Toast.LENGTH_SHORT).show();
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
//                Toast.makeText(context, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();
            }

//            mTelephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

//            PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
//                @Override
//                public void onCallStateChanged(int state, String incomingNumber) {
//                    super.onCallStateChanged(state, incomingNumber);
//
//                    switch (state) {
//                        case TelephonyManager.CALL_STATE_IDLE:
//                            Toast.makeText(context, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
//                            mHandler.sendEmptyMessageDelayed(1, 0);
//                            break;
//                        case TelephonyManager.CALL_STATE_RINGING:
//                            Toast.makeText(context, "CALL_STATE_RINGING : "+ "  "+ incomingNumber, Toast.LENGTH_SHORT).show();
//                            break;
//                        case TelephonyManager.CALL_STATE_OFFHOOK:
//                            Toast.makeText(context, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                }
//
//            };
        }
    }

    @Override
    public void onSuccess(String type, Response response) {

    }

    @Override
    public void onFailure(Object data) {

    }
}
