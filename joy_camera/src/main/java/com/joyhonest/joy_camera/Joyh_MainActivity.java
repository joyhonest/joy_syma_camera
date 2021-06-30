package com.joyhonest.joy_camera;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.joyhonest.joy_camera.databinding.JoyhActivityMainBinding;
import com.joyhonest.wifination.wifination;

import com.joyhonest.joy_camera.R;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class Joyh_MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Joyh_MainActivity";
    private Joyh_PermissionAsker mAsker;
    private int nAsk = -1;

    boolean  bFlip = false;




    //判断视频状态
    public static boolean bisRecording = false;


    //是否开启VR
    private boolean bisVR = false;
    //判断图片是否接受成功
    public static boolean bIsConnect = true;

    AlertDialog alertDialog;



    private JoyhActivityMainBinding binding;
    private  HandlerThread thread1;
    private  Handler H_Send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  JoyhActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_main);
        setContentView(binding.getRoot());

        MyApp.Init(getApplicationContext());

        thread1 = new HandlerThread("planerockerCmdThread");
        thread1.start(); //创建一个HandlerThread并启动它
        H_Send = new Handler(thread1.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程

        initView();
        //拖动条监听
        //seekBarJH();
        //摇杆监听
        //rockerJH();

        MyApp.F_makeFullScreen(this);

        F_DispSpeedIcon();

        //权限请求未通过时再次申请
        alertDialog=new AlertDialog.Builder(this)
                .setTitle("permission")
                .setMessage("The device needs to be allowed permission to function properly")
                .setNegativeButton("OK",(dialog, which) -> {
                    Joyh_PermissionPageUtils joyhPermissionPageUtils = new Joyh_PermissionPageUtils(Joyh_MainActivity.this);
                    joyhPermissionPageUtils.jumpPermissionPage();
                }).create();
        //权限通过执行第一个run方法，权限未通过执行第二个run方法
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

        bDispController =  false; //MyApp.bisShowControler;

        F_DispLeftRightControl();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!MyApp.bisHighLimited)
                {
                    if (!MyApp.bisRightMode) {
                        binding.customRockerLeftJH.bReCenter = false;
                        binding.customRockerRightJH.bReCenter = true;

                        binding.customRockerLeftJH.ResetY();

                    } else {
                        binding.customRockerLeftJH.bReCenter = true;
                        binding.customRockerRightJH.bReCenter = false;
                        binding.customRockerRightJH.ResetY();
                    }
                }
                bDispController = MyApp.bisShowControler;
                F_DispController();
            }
        },100);



        EventBus.getDefault().register(this);

    }




    Handler  OpenCamerapHandler = new Handler();
    Runnable OpenCamerapRunnable = new Runnable() {
        @Override
        public void run() {
            wifination.naSetRevBmp(true);
            wifination.naInit("");
        }
    };

    byte[] cmd = new byte[20];

    private boolean bUp = false;
    private boolean bDn = false;


    private Runnable  SentCmdRunnable = new Runnable() {
        @Override
        public void run() {
            F_SentFlyCmd();
            Log.e(TAG,"Sent Cmd");
            H_Send.postDelayed(this, 20);
        }
    };


    private void F_SentFlyCmd()
    {
        if(!bDispController)
            return;

        int X1 = binding.customRockerLeftJH.GetX();
        int Y1 = binding.customRockerLeftJH.GetY();
        int X2 = binding.customRockerRightJH.GetX();
        int Y2 = binding.customRockerRightJH.GetY();
        int X_ADJ1 = binding.leftSeekBar.F_GetValue();      //旋转微调
        int X_ADJ2 = binding.rightSeekBar.F_GetValue();     //左右微调
        int Y_ADJ2 = binding.topSeekBar.F_GetValue();       //前后微调
        if(MyApp.bisRightMode) //
        {
            X2 = binding.customRockerLeftJH.GetX();
            Y2 = binding.customRockerLeftJH.GetY();
            X1 = binding.customRockerRightJH.GetX();
            Y1 = binding.customRockerRightJH.GetY();
            X_ADJ1 = binding.rightSeekBar.F_GetValue(); //
            X_ADJ2 = binding.leftSeekBar.F_GetValue();
            Y_ADJ2 = binding.topSeekBarA.F_GetValue();
        }

//        Log.e(TAG, "Y1 = "+Y1+" X1 = "+X1+" Y2 = "+Y2+" X2 = "+X2);
//
//        Log.e(TAG,"X1AD ="+X_ADJ1+"  X2ADJ =" +X_ADJ2+"  Y2ADJ =" +Y_ADJ2);

        if (X2 > 0x70 && X2 < 0x90) {
            X2 = 0x80;
        }

        if (Y2 > 0x70 && Y2 < 0x90) {
            Y2 = 0x80;
        }

        if (X1 > (0x80 - 0x25) && X1 < (0x80 + 0x25)) {
            X1 = 0x80;
        }
        int i = 0;


        if (Y2 > 0x80) {
            Y2 -= 0x80;
        } else if (Y2 < 0x80) {
            Y2 = 0x80 - Y2;
            Y2 += 0x80;
            if (Y2 > 0xFF) {
                Y2 = 0xFF;
            }
        }

        if (X1 > 0x80) {

        } else if (X1 < 0x80) {
            X1 = 0x80 - X1;
            if (X1 > 0x7F) {
                X1 = 0x7F;
            }
        }

        if (X2 > 0x80) {
        } else if (X2 < 0x80) {
            X2 = 0x80 - X2;
            if (X2 > 0x7F) {
                X2 = 0x7F;
            }
        }




        cmd[0] = (byte) Y1;   //油门
        cmd[1] = (byte) Y2;
        cmd[2] = (byte) X1;
        cmd[3] = (byte) X2;

        Log.e(TAG,"UP ="+Y1);


        cmd[4] = 0x20;          //油门微调  这里没有。

        int da = Y_ADJ2 - 0x80;
        if (da < 0)               // 后调
        {
            da = 0 - da;
            da += 0x20;
            if (da > 0x3F) {
                da = 0x3F;
            }
        } else if (da > 0) {
            if (da > 0x1F)
                da = 0x1F;
        } else {
            da = 0x00;
        }


        cmd[5] = (byte) da;       //前后微调
        if (MyApp.nSpeed!=0)
            cmd[5] |= 0x80;          //高速模式

        cmd[5] |= 0x40;


        da = X_ADJ1 - 0x80;          //旋转微调
        if (da < 0) {
            da = 0 - da;
            if (da > 0x1F) {
                da = 0x1F;
            }
        } else if (da > 0) {
            da += 0x20;
            if (da > 0x3F)
                da = 0x3F;
        } else {
            da = 0x20;
        }


        cmd[6] = (byte) da;


        da = X_ADJ2 - 0x80;
        if (da < 0) {

            da = 0 - da;
            if (da > 0x1F) {
                da = 0x1F;
            }
        } else if (da > 0) {
            da += 0x20;
            if (da > 0x3F)
                da = 0x3F;
        } else {
            da = 0x20;
        }

        cmd[7] = (byte) da;        //平移

        if (bUp) {
            cmd[7] |= 0x40;
        }
//        if (bHeadLess) {
//            cmd[7] |= 0x80;
//        }

        cmd[8] = 0;

//        if (bStop) {
//            cmd[8] |= 0x10;
//        }
//        if (bAdj) {
//            cmd[8] |= 0x20;
//        }
        if (bDn) {
            cmd[8] |= 0x08;
        }
        cmd[9] = (byte) (((cmd[0] ^ cmd[1] ^ cmd[2] ^ cmd[3] ^ cmd[4] ^ cmd[5] ^ cmd[6] ^ cmd[7] ^ cmd[8]) & 0xFF) + 0x55);
        wifination.naSentCmd(cmd, 10);

    }


    @Override
    protected void onStart() {
        super.onStart();
        OpenCamerapHandler.removeCallbacksAndMessages(null);
        OpenCamerapHandler.post(OpenCamerapRunnable);
        H_Send.removeCallbacksAndMessages(null);
        H_Send.post(SentCmdRunnable);

    }
    @Override
    protected void onStop() {
        super.onStop();
        H_Send.removeCallbacksAndMessages(null);
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        wifination.naStopRecord_All();
        wifination.naStop();
        Hand_DispRec.removeCallbacksAndMessages(null);
    }


    @Subscriber(tag="ExitAPP")
    private  void  ExitAPP(String str)
    {
        finish();
    }





    int nCheckConnected = 0;

    Handler CheckHander = new Handler();
    //在包中得到发布,固定的名称
    Runnable CheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (nCheckConnected < 20) {
                nCheckConnected++;
            } else {
                wifination.naStop();
                SystemClock.sleep(250);
                wifination.naInit("");
                nCheckConnected = 0;
            }
            CheckHander.postDelayed(this, 100);
        }
    };

    @Subscriber(tag = "ReviceBMP")
    private void ReviceBMP(Bitmap bitmap) {
        binding.imageJH.setImageBitmap(bitmap);
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
            if (!bisRecording) {
                bisRecording = true;
                F_DispRecordTime(true);
            }


        } else {
            if (bisRecording) {
                bisRecording = false;
                F_DispRecordTime(false);
            }


        }
    }


    private void F_DispSpeedIcon()
    {
        if(MyApp.nSpeed == 0)
        {
                binding.speed.setBackgroundResource(R.mipmap.speed_l);
        }
        else
        {
            binding.speed.setBackgroundResource(R.mipmap.speed_h);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAsker.onRequestPermissionsResult(grantResults);
    }


    private  void F_DispLeftRightControl()
    {
        if(MyApp.bisRightMode)
        {
            binding.topSeekBar.setVisibility(View.GONE);
            binding.topSeekBarA.setVisibility(View.VISIBLE);
            binding.customRockerLeftJH.setRightMode(true);
            binding.customRockerRightJH.setRightMode(false);
        }
        else
        {
            binding.topSeekBar.setVisibility(View.VISIBLE);
            binding.topSeekBarA.setVisibility(View.GONE);
            binding.customRockerLeftJH.setRightMode(false);
            binding.customRockerRightJH.setRightMode(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApp.PlayBtnVoice();
    }

    boolean  bDispController = false;


    private void F_DispController()
    {
        if(bDispController)
        {
            binding.ConTroller.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.ConTroller.setVisibility(View.INVISIBLE);
        }
        if(MyApp.bisHighLimited) {
                binding.customRockerLeftJH.bReCenter=true;
                binding.customRockerRightJH.bReCenter=true;
        }
        else
        {
            if(!MyApp.bisRightMode) {
                binding.customRockerLeftJH.bReCenter = false;
                binding.customRockerRightJH.bReCenter = true;
            }
            else
            {
                binding.customRockerLeftJH.bReCenter = true;
                binding.customRockerRightJH.bReCenter = false;
            }
        }
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageJH) {//隐藏工具栏
            if (binding.ConTroller.getVisibility() != View.VISIBLE) {
                MyApp.PlayBtnVoice();
                show_hid_toolbar();
            }
        } else if (id == R.id.rollback) {
            onBackPressed();
        } else if (id == R.id.take_photo) {
            F_CheckPermissions(0);
        } else if (id == R.id.record_video) {
            F_CheckPermissions(1);
        } else if (id == R.id.gallery) {
            F_CheckPermissions(2);
        } else if (id == R.id.speed) {
            MyApp.PlayBtnVoice();
            if (MyApp.nSpeed == 0) {
                MyApp.nSpeed = 1;
            } else {
                MyApp.nSpeed = 0;
            }
            F_DispSpeedIcon();
        } else if (id == R.id.flip_screen) {
            MyApp.PlayBtnVoice();
            //bFlip = !bFlip;
            //wifination.naSetFlip(bFlip);
            tigerOrg();
        } else if (id == R.id.controller_show_hide) {
            MyApp.PlayBtnVoice();
            bDispController = !bDispController;
            F_DispController();
        } else if (id == R.id.vr) {
            MyApp.PlayBtnVoice();
            //VR
            bisVR = !bisVR;
            wifination.naSet3D(bisVR);
        } else if (id == R.id.btnDown) {
            MyApp.PlayBtnVoice();
            bDn = true;
            bUp = false;
            binding.btnDown.setEnabled(false);
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.btnDown.setEnabled(true);
                    bDn = false;
                    bUp = false;
                }
            }, 500);
        } else if (id == R.id.btnUP) {
            MyApp.PlayBtnVoice();
            bDn = false;
            bUp = true;
            binding.btnUP.setEnabled(false);
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.btnUP.setEnabled(true);
                    bDn = false;
                    bUp = false;
                }
            }, 500);
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
        MyApp.F_CreateLocalDir();
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
            MyApp.bNormalExit = true;
            Intent mainIntent = new Intent(Joyh_MainActivity.this, BrowSelectActivity.class);
            startActivity(mainIntent);
            overridePendingTransition(0, 0);
        }
        nAsk = -1;

    }

    //可拖动进度条的监听方法
//    private void seekBarJH() {
//        //mLeftSeekBar
//        binding.leftSeekBar.setOnPercentListener(new JH_SeekBar.OnPercentListener() {
//            @Override
//            public void onStart() {
//
//                binding.leftText.setText(null);
//            }
//
//            @Override
//            public void percent(int x, int y) {
//                binding.leftText.setText(x+"%");
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
//        //mRightSeekBar.
//        binding.rightSeekBar.setOnPercentListener(new JH_SeekBar.OnPercentListener() {
//            @Override
//            public void onStart() {
//                binding.rightText.setText(null);
//            }
//
//            @Override
//            public void percent(int x, int y) {
//                binding.rightText.setText(x+"%");
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
//
//        //mTopSeekBar.
//        binding.topSeekBar.setOnPercentListener(new JH_SeekBar.OnPercentListener() {
//            @Override
//            public void onStart() {
//                binding.topText.setText(null);
//            }
//
//            @Override
//            public void percent(int x, int y) {
//                binding.topText.setText(y+"%");
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
//    }



    //显示录像时间
    private void F_DispRecordTime(boolean b) {
        if (b) {
            binding.videoTime.setVisibility(View.VISIBLE);
            Hand_DispRec.removeCallbacksAndMessages(null);
            //开启无限循环
            Hand_DispRec.post(Runnable_DispRec);
        } else {
            binding.videoTime.setVisibility(View.INVISIBLE);
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
            binding.videoTime.setText(str);
            //250毫秒后获取一次录像时间并更新ui
            Hand_DispRec.postDelayed(this, 250);
        }
    };


    //隐藏工具栏
    public void show_hid_toolbar() {
        if (bIsConnect) {
            //mToolBar
            int nHeight = binding.lineraTop.getHeight() + 2;
            if (binding.lineraTop.getTranslationY() < 0) {
                ObjectAnimator.ofFloat(binding.lineraTop, "TranslationY", 0)
                        .setDuration(300).start();
            } else {
                ObjectAnimator.ofFloat(binding.lineraTop, "TranslationY", -nHeight)
                        .setDuration(300).start();
            }
        } else {

            if (binding.lineraTop.getTranslationY() < 0) {
                ObjectAnimator.ofFloat(binding.lineraTop, "TranslationY", 0)
                        .setDuration(300).start();
            }

        }
    }

//    //返回方向
//    private String getDirction(JH_Rocker.Direction direction) {
//        String s;
//        if (direction == JH_Rocker.Direction.DIRECTION_LEFT) {
//            return s = "左";
//        } else if (direction == JH_Rocker.Direction.DIRECTION_DOWN_LEFT) {
//            return s = "左下";
//        } else if (direction == JH_Rocker.Direction.DIRECTION_UP_LEFT) {
//            return s = "左上";
//        } else if (direction == JH_Rocker.Direction.DIRECTION_RIGHT) {
//            return s = "右";
//        } else if (direction == JH_Rocker.Direction.DIRECTION_DOWN_RIGHT) {
//            return s = "右下";
//        } else if (direction == JH_Rocker.Direction.DIRECTION_UP_RIGHT) {
//            return s = "右上";
//        } else if (direction == JH_Rocker.Direction.DIRECTION_UP) {
//            return s = "上";
//
//        } else if (direction == JH_Rocker.Direction.DIRECTION_DOWN) {
//            return s = "下";
//        } else {
//            return s = null;
//        }
//
//    }


    //摇杆监听
    private void rockerJH() {
//        binding.customRockerLeftJH.setCallBackMode(JH_Rocker.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
//        binding.customRockerLeftJH.setOnPercentListener(new JH_Rocker.OnPercentListener() {
//            @Override
//            public void onStart() {
//                binding.leftRocker.setText(null);
//            }
//
//            @Override
//            public void percent(String mPercentX, String mPercentY) {
//                binding.leftRocker.setText(mPercentX+mPercentY);
//
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
//
//
//
//        binding.customRockerRightJH.setCallBackMode(JH_Rocker.CallBackMode.CALL_BACK_MODE_MOVE);
//        binding.customRockerRightJH.setOnShakeListener(JH_Rocker.DirectionMode.DIRECTION_8, new JH_Rocker.OnShakeListener() {
//            @Override
//            public void onStart() {
//                binding.rightRocker.setText(null);
//            }
//
//            @Override
//            public void direction(JH_Rocker.Direction direction) {
//                binding.rightRocker.setText("当前方向是：" + getDirction(direction));
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        });
    }


    //控件初始化
    private void initView() {
//        mRollback = findViewById(R.id.rollback);
//        mTakePhoto = findViewById(R.id.take_photo);
//        mRecordVideo = findViewById(R.id.record_video);
//        mGallery = findViewById(R.id.gallery);
//        mSpeed = findViewById(R.id.speed);
//        mFlipScreen = findViewById(R.id.flip_screen);
//        mUnknown = findViewById(R.id.unknown);
//        mControllerShow = findViewById(R.id.controller_show_hide);
//        mVR = findViewById(R.id.vr);
//        mLeftSeekBar = findViewById(R.id.leftSeekBar);
//        mRightSeekBar = findViewById(R.id.rightSeekBar);
//        mTopSeekBar = findViewById(R.id.topSeekBar);
//        mLeftText = findViewById(R.id.leftText);
//        mRightText = findViewById(R.id.rightText);
//        mTopText = findViewById(R.id.topText);
//        mToolBar = findViewById(R.id.linera_top);
//        mLeftRockerJH = findViewById(R.id.customRocker_leftJH);
//        mRightRockerJH = findViewById(R.id.customRocker_rightJH);
//        mLeftRocker = findViewById(R.id.leftRocker);
//        mRightRocker = findViewById(R.id.rightRocker);
//        mTopArrow = findViewById(R.id.topButton);
//        mBottomArrow = findViewById(R.id.bottomButton);
//        mImageJH = findViewById(R.id.imageJH);
//        mVideoTime=findViewById(R.id.video_time);


        binding.rollback.setOnClickListener(this);
        binding.takePhoto.setOnClickListener(this);
        binding.recordVideo.setOnClickListener(this);
        binding.gallery.setOnClickListener(this);
        binding.speed.setOnClickListener(this);
        binding.flipScreen.setOnClickListener(this);
        binding.controllerShowHide.setOnClickListener(this);
        binding.vr.setOnClickListener(this);
        binding.imageJH.setOnClickListener(this);
        binding.btnDown.setOnClickListener(this);
        binding.btnUP.setOnClickListener(this);
//        mRollback.setOnClickListener(this);
//        mTakePhoto.setOnClickListener(this);
//        mRecordVideo.setOnClickListener(this);
//        mGallery.setOnClickListener(this);
//        mSpeed.setOnClickListener(this);
//        mFlipScreen.setOnClickListener(this);
//        mUnknown.setOnClickListener(this);
//        mControllerShow.setOnClickListener(this);
//        mVR.setOnClickListener(this);
//        mImageJH.setOnClickListener(this);


    }

    private  void tigerOrg()
    {


        this.getDisplay();
        Display display = getDisplay();


        int screenRotation = display.getRotation();
        if(screenRotation == Surface.ROTATION_90)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
    }

}