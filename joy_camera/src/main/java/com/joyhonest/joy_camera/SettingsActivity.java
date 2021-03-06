package com.joyhonest.joy_camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.joyhonest.joy_camera.databinding.JoyhActivitySettingBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class SettingsActivity extends AppCompatActivity {
    private JoyhActivitySettingBinding binding;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= JoyhActivitySettingBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_setting);

        editor=getSharedPreferences("setting",MODE_PRIVATE).edit();

        setContentView(binding.getRoot());
        MyApp.F_makeFullScreen(this);

        //应用程序退出后，设置不会保存
        init(this);

        EventBus.getDefault().register(this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init(Context context){
        SharedPreferences setting=context.getSharedPreferences("setting",MODE_PRIVATE);
        MyApp.bisHighLimited= setting.getBoolean("HighLimited",true);
        MyApp.bisRightMode= setting.getBoolean("RightMode",false);
        MyApp.bisShowControler=setting.getBoolean("ShowControler",true);
        MyApp.bisAutoSave=setting.getBoolean("AutoSave",false);
        MyApp.bisResetTuneData=setting.getBoolean("Reset",false);


        //判断是ON还是OFF显示按钮
        if (MyApp.bisHighLimited){
            binding.btnSupportHigh.setBackgroundResource(R.mipmap.switch_on_jh);
        }
        if(MyApp.bisRightMode){
            binding.btnRightHandMode.setBackgroundResource(R.mipmap.switch_on_jh);
        }
        if (MyApp.bisShowControler){
            binding.btnShowInterface.setBackgroundResource(R.mipmap.switch_on_jh);
        }
        if (MyApp.bisAutoSave){
            binding.btnAutoSaveFineTune.setBackgroundResource(R.mipmap.switch_on_jh);
        }
        if (MyApp.bisResetTuneData){
            binding.btnPreview.setBackgroundResource(R.mipmap.switch_on_jh);
        }


        binding.butSetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                onBackPressed();
            }
        });


        binding.btnSupportHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                if (!MyApp.bisHighLimited) {
                    binding.btnSupportHigh.setBackgroundResource(R.mipmap.switch_on_jh);
                    MyApp.bisHighLimited=true;
                    editor.putBoolean("HighLimited",MyApp.bisHighLimited);

                }else {
                    binding.btnSupportHigh.setBackgroundResource(R.mipmap.switch_off_jh);
                    MyApp.bisHighLimited=false;
                    editor.putBoolean("HighLimited",MyApp.bisHighLimited);
                }
                editor.apply();
            }
        });

        binding.btnRightHandMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                if (!MyApp.bisRightMode) {
                    binding.btnRightHandMode.setBackgroundResource(R.mipmap.switch_on_jh);
                    MyApp.bisRightMode=true;
                    editor.putBoolean("RightMode",MyApp.bisRightMode);
                }else {
                    binding.btnRightHandMode.setBackgroundResource(R.mipmap.switch_off_jh);
                    MyApp.bisRightMode=false;
                    editor.putBoolean("RightMode",MyApp.bisRightMode);

                }
                editor.apply();
            }
        });

        binding.btnShowInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                if (!MyApp.bisShowControler){
                    binding.btnShowInterface.setBackgroundResource(R.mipmap.switch_on_jh);
                    MyApp.bisShowControler=true;
                    editor.putBoolean("ShowControler",MyApp.bisShowControler);
                }else {
                    binding.btnShowInterface.setBackgroundResource(R.mipmap.switch_off_jh);
                    MyApp.bisShowControler=false;
                    editor.putBoolean("ShowControler",MyApp.bisShowControler);
                }
                editor.apply();
            }
        });

        binding.btnAutoSaveFineTune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                if (!MyApp.bisAutoSave){
                    binding.btnAutoSaveFineTune.setBackgroundResource(R.mipmap.switch_on_jh);
                    MyApp.bisAutoSave=true;
                    editor.putBoolean("AutoSave",MyApp.bisAutoSave);
                }else {
                    binding.btnAutoSaveFineTune.setBackgroundResource(R.mipmap.switch_off_jh);
                    MyApp.bisAutoSave=false;
                    editor.putBoolean("AutoSave",MyApp.bisAutoSave);
                }
                editor.apply();
            }
        });

        binding.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                if (!MyApp.bisResetTuneData){
                    binding.btnPreview.setBackgroundResource(R.mipmap.switch_on_jh);
                    MyApp.bisResetTuneData=true;
                    editor.putBoolean("Reset",MyApp.bisResetTuneData);
                }else {
                    binding.btnPreview.setBackgroundResource(R.mipmap.switch_off_jh);
                    MyApp.bisResetTuneData=false;
                    editor.putBoolean("Reset",MyApp.bisResetTuneData);
                }
                editor.apply();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }


    @Subscriber(tag = "GotoExit_joy")
    private  void GotoExit_joy(String str)
    {
        finish();
        overridePendingTransition(0, 0);
    }
}