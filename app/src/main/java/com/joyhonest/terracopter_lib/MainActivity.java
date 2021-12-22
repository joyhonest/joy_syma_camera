package com.joyhonest.terracopter_lib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.joyhonest.joy_camera.Joyh_TerraCopter;
import com.joyhonest.joy_camera.MyApp;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_Start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.joyhonest.joy_camera.MyApp.bisHighLimited = false;
                com.joyhonest.joy_camera.MyApp.bisRightMode = true;
//                com.joyhonest.joy_camera.MyApp.bisShowControler = true;
//                com.joyhonest.joy_camera.MyApp.bisAutoSave = false;
//                com.joyhonest.joy_camera.MyApp.bisResetTuneData = false;
                  startActivity(new Intent(MainActivity.this, Joyh_TerraCopter.class));
               //startActivity(new Intent(MainActivity.this, com.joyhonest.joy_camera.Ultradrone.JH_UltradroneActivity.class));
            }
        });

    }
}