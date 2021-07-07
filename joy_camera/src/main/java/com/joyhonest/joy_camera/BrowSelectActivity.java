package com.joyhonest.joy_camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.joyhonest.joy_camera.databinding.ActivityJhUltradroneBinding;
import com.joyhonest.joy_camera.databinding.JoyhActivityBrowSelectBinding;
import com.joyhonest.wifination.wifination;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class BrowSelectActivity extends AppCompatActivity {




    private JoyhActivityBrowSelectBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = JoyhActivityBrowSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_brow_select);
        EventBus.getDefault().register(this);
        MyApp.F_makeFullScreen(this);
        binding.butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.butPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                GotoGridAcitivty(MyApp.Brow_Photo);
            }
        });
        binding.butVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                //wifination.naStopRecord(wifination.TYPE_ONLY_PHONE);
                GotoGridAcitivty(MyApp.Brow_Video);
            }
        });
    }

    private  void GotoGridAcitivty( int nBrow)
    {
        MyApp.BROW_TYPE = nBrow;
        Intent mainIntent = new Intent(this, GridActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(0, 0);

    }

    @Subscriber(tag = "GotoExit_joy")
    private  void GotoExit_joy(String str)
    {
        finish();
        overridePendingTransition(0, 0);
    }


    @Override
    public void onBackPressed() {
      //  MyApp.bNormalExit = true;
        super.onBackPressed();
        MyApp.PlayBtnVoice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(!MyApp.bNormalExit)
//            EventBus.getDefault().post("","GotoExit");
    }

    @Override
    protected void onResume() {
        super.onResume();
     //   MyApp.bNormalExit = false;
        MyApp.F_makeFullScreen(this);
    }
}