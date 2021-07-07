package com.joyhonest.joy_camera;

import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.joyhonest.joy_camera.databinding.JoyhActivityHelpBinding;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;


public class HelpActivity extends AppCompatActivity {
    private JoyhActivityHelpBinding binding;
    private int[] image={R.drawable.joyh_help_info,R.drawable.joyh_helf_info2};
    private int index;
    private GestureDetector gestureDetector;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=JoyhActivityHelpBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_help);
        setContentView(binding.getRoot());
        MyApp.F_makeFullScreen(this);

        gestureDetector=new GestureDetector((GestureDetector.OnGestureListener) onGestureListener);




        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        EventBus.getDefault().register(this);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private GestureDetector.OnContextClickListener onGestureListener=new GestureDetector.SimpleOnGestureListener() {

           public boolean onFling(MotionEvent e1,MotionEvent e2,float x,float y){

               float x1=e2.getX()-e1.getX();
               float y1=e2.getY()-e2.getY();
               if (x1>0){
                   index++;
                   if (index>1){
                       index=1;
                   }

               }else if (x1<0){
                   index--;
                   if (index<0){
                       index=0;
                   }

               }
               binding.imageHelp.setBackgroundResource(image[index]);

                return true;
            }



        };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApp.PlayBtnVoice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }


    @Subscriber(tag = "GotoExit")
    private  void GotoExit(String str)
    {
        finish();
        overridePendingTransition(0, 0);
    }
}