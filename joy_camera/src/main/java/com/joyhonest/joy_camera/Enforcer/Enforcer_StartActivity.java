package com.joyhonest.joy_camera.Enforcer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.joyhonest.joy_camera.R;
import com.joyhonest.joy_camera.databinding.ActivityEnforcerStartBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

public class Enforcer_StartActivity extends AppCompatActivity {


    private ActivityEnforcerStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnforcerStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = "GotoExit_joy")
    private  void GotoExit_joy(String str)
    {
        finish();
        overridePendingTransition(0, 0);
    }
}