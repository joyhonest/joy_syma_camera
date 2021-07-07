package com.joyhonest.joy_camera.Ultradrone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.joyhonest.joy_camera.BrowSelectActivity;
import com.joyhonest.joy_camera.Joyh_MainActivity;
import com.joyhonest.joy_camera.Joyh_PermissionAsker;
import com.joyhonest.joy_camera.Joyh_PermissionPageUtils;
import com.joyhonest.joy_camera.MyApp;
import com.joyhonest.joy_camera.R;
import com.joyhonest.joy_camera.databinding.ActivityJhUltradroneBinding;
import com.joyhonest.wifination.wifination;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class JH_UltradroneActivity extends AppCompatActivity implements View.OnClickListener
{



    WifiManager wifiManager;// = (WifiManager) getSystemService(WIFI_SERVICE)

    private String TAG = "UltradroneActivity";

    private boolean bRecording;
    private boolean b3D;


    private int nAsk=-1;

    private ActivityJhUltradroneBinding  binding;


    private Joyh_PermissionAsker mAsker;
    private AlertDialog alertDialog;

    Handler OpenCamerapHandler;
    Handler DispWifiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_jh_ultradrone);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        binding = ActivityJhUltradroneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MyApp.Init(getApplicationContext());
        OpenCamerapHandler = new Handler(getMainLooper());
        DispWifiHandler  = new Handler(getMainLooper());

        b3D = false;
        MyApp.bIsConnect = false;
        bRecording = false;
        binding.RectimeView.setVisibility(View.INVISIBLE);

        binding.butVr.setOnClickListener(this);
        binding.butSnap.setOnClickListener(this);
        binding.butRecord.setOnClickListener(this);
        binding.butBrow.setOnClickListener(this);
        binding.butReturn.setOnClickListener(this);
        MyApp.F_makeFullScreen(this);

        //权限请求未通过时再次申请
        alertDialog=new AlertDialog.Builder(this)
                .setTitle("permission")
                .setMessage("The device needs to be allowed permission to function properly")
                .setNegativeButton("OK",(dialog, which) -> {
                    Joyh_PermissionPageUtils joyhPermissionPageUtils = new Joyh_PermissionPageUtils(JH_UltradroneActivity.this);
                    joyhPermissionPageUtils.jumpPermissionPage();
                }).create();

        mAsker=new Joyh_PermissionAsker(10, new Runnable() {
            @Override
            public void run() {
                F_GetPermissions();
            }
        },new Runnable(){
            @Override
            public void run() {
                alertDialog.show();

            }
        });

        DispWifiHandler.post(DispWifiRunnable);

        EventBus.getDefault().register(this);


    }


    int bmp[] = {R.mipmap.wifi01_icon_jh01,R.mipmap.wifi02_icon_jh01,R.mipmap.wifi03_icon_jh01,R.mipmap.wifi04_icon_jh01};
    Runnable DispWifiRunnable = new Runnable() {
        @Override
        public void run() {
            if(MyApp.bIsConnect) {
                WifiInfo info = wifiManager.getConnectionInfo();
                if (info != null) {
                    int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
                    if(strength>4)
                        strength = 4;
                    if(strength<1)
                        strength = 1;
                    strength--;
                    binding.butSigne.setBackgroundResource(bmp[strength]);
                }
            }
            else
            {
                binding.butSigne.setBackgroundResource(R.mipmap.wifi00_icon_jh01);
            }
            DispWifiHandler.postDelayed(this,500);
        }
    };

    Runnable OpenCamerapRunnable = new Runnable() {
        @Override
        public void run() {
            wifination.naSetRevBmp(true);
            wifination.naInit("");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        OpenCamerapHandler.removeCallbacksAndMessages(null);
        OpenCamerapHandler.post(OpenCamerapRunnable);

    }
    @Override
    protected void onStop() {
        super.onStop();
        OpenCamerapHandler.removeCallbacksAndMessages(null);
        wifination.naStopRecord_All();
        wifination.naStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DispWifiHandler.removeCallbacksAndMessages(null);
        OpenCamerapHandler.removeCallbacksAndMessages(null);
        wifination.naStopRecord_All();
        wifination.naStop();
        Log.e(TAG,"ULtradrone destroy");
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.but_vr)
        {
            MyApp.PlayBtnVoice();
            b3D = !b3D;
            wifination.naSet3D(b3D);
        }

        if(id == R.id.but_snap)
        {
            if(MyApp.bIsConnect)
            {
                F_CheckPermissions(0);
            }

        }
        if(id == R.id.but_record)
        {
            if(MyApp.bIsConnect)
            {
                F_CheckPermissions(1);
            }
        }
        if(id == R.id.but_brow)
        {
            //if(MyApp.bIsConnect)
            {
                F_CheckPermissions(2);
            }
        }

        if(id == R.id.but_return)
        {
            MyApp.PlayBtnVoice();
            finish();
        }
        if (id == R.id.DispImageView) {//隐藏工具栏
            if (binding.toolBar.getVisibility() != View.VISIBLE) {
                MyApp.PlayBtnVoice();
                show_hid_toolbar();
            }
        }


    }


    void F_CheckPermissions(int nA)
    {
        nAsk = nA;
        if(!MyApp.isAndroidQ()) {
            mAsker.askPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        else
        {
            F_GetPermissions();
        }
    }
    void F_GetPermissions()
    {
        MyApp.F_CreateLocalDir("Ultradrone");
        if(nAsk==0)
        {
            if(MyApp.bIsConnect) {
                MyApp.PlayPhotoMusic();
                String sName = MyApp.getFileNameFromDate(false, true);
                wifination.naSnapPhoto(sName, wifination.TYPE_ONLY_PHONE);
            }

        }
        else if(nAsk == 1)
        {
            if(MyApp.bIsConnect) {
                MyApp.PlayBtnVoice();
                if (wifination.isPhoneRecording()) {
                    wifination.naStopRecord_All();
                } else {
                    String sName = MyApp.getFileNameFromDate(true, true);
                    wifination.naStartRecord(sName, wifination.TYPE_ONLY_PHONE);
                }
            }

        }
        else if(nAsk == 2)
        {
            MyApp.PlayBtnVoice();
       //     MyApp.bNormalExit = true;
            Intent mainIntent = new Intent(JH_UltradroneActivity.this, BrowSelectActivity.class);
            startActivity(mainIntent);
            overridePendingTransition(0, 0);
        }
        nAsk = -1;

    }


    @Subscriber(tag = "ReviceBMP")
    private void ReviceBMP(Bitmap bitmap) {
        binding.DispImageView.setImageBitmap(bitmap);
        MyApp.bIsConnect = true;

    }

    @Subscriber(tag = "SavePhotoOK")
    private void SavePhotoOK(String Sn) {
        if (Sn.length() < 5) {
            return;
        }
        String sType = Sn.substring(0, 2);
        String sName = Sn.substring(2, Sn.length());
        int nPhoto = Integer.parseInt(sType);
        if (nPhoto == 0) {
            MyApp.F_Save2ToGallery(sName, true);

        } else {
            MyApp.F_Save2ToGallery(sName, false);
        }
    }

    //设置视频的计时器开启和关闭
    @Subscriber(tag = "SDStatus_Changed")
    private void _OnStatusChanged(int nStatus) {
        //#define  bit0_OnLine            1
        //#define  bit1_LocalRecording    2
        //#define  SD_Ready               4
        //#define  SD_Recroding           8
        //#define  SD_Photo               0x10
        Log.d("   Status", "" + nStatus);
        if ((nStatus & 0x02) != 0) {
            if (!bRecording) {
                bRecording = true;
                F_DispRecordTime(true);
            }


        } else {
            if (bRecording) {
                bRecording = false;
                F_DispRecordTime(false);
            }


        }
    }


    private void F_DispRecordTime(boolean b) {
        if (b) {
            binding.RectimeView.setVisibility(View.VISIBLE);
            Hand_DispRec.removeCallbacksAndMessages(null);
            //开启无限循环
            Hand_DispRec.post(Runnable_DispRec);
        } else {
            binding.RectimeView.setVisibility(View.INVISIBLE);
            Hand_DispRec.removeCallbacksAndMessages(null);
        }
    }

    Handler Hand_DispRec = new Handler();
    //发布后，每250毫秒获取一次录像时间
    Runnable Runnable_DispRec = new Runnable() {
        @Override
        public void run() {
            //获取录像时间的毫秒转换成秒
            int nSec = wifination.naGetRecordTime() / 1000;
            //转换成分
            int nMin = nSec / 60;
            //对总秒数进行取整，80秒，1.20，取后面的20秒，
            nSec = nSec % 60;
            //转换成时
            int nHour = nMin / 60;
            //对总分数进行取整，80分钟，1:20，取后面20分钟
            nMin = nMin % 60;
            String str = "";
            if (nHour == 0)
                //格式化字符串，把nMin和nSeC格式化为两位数，5显示为05
                str = String.format("%02d:%02d", nMin, nSec);
            else
                str = String.format("%02d:%02d:%02d", nHour, nMin, nSec);
            binding.RectimeView.setText(str);
            //250毫秒后获取一次录像时间并更新ui
            Hand_DispRec.postDelayed(this, 250);
        }
    };

    //隐藏工具栏
    public void show_hid_toolbar() {
        if (MyApp.bIsConnect) {
            //mToolBar
            int nHeight = binding.toolBar.getHeight() + 2;
            if (binding.toolBar.getTranslationY() < 0) {
                ObjectAnimator.ofFloat(binding.toolBar, "TranslationY", 0)
                        .setDuration(300).start();
            } else {
                ObjectAnimator.ofFloat(binding.toolBar, "TranslationY", -nHeight)
                        .setDuration(300).start();
            }
        } else {

            if (binding.toolBar.getTranslationY() < 0) {
                ObjectAnimator.ofFloat(binding.toolBar, "TranslationY", 0)
                        .setDuration(300).start();
            }

        }
    }

}