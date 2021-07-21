package com.joyhonest.joy_camera;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

//import cn.jzvd.Jzvd;


public class DispVideoActivity extends AppCompatActivity {

    //private MyVideoView myVideoView;
    //private MyJzvdStd my_jzvdStd;

    private VideoView video_play;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joyh_activity_disp_video_jh);
        MyApp.F_makeFullScreen(this);
        String strPath = MyApp.dispList.get(0);

        video_play = findViewById(R.id.video_play);
        //my_jzvdStd = findViewById(R.id.jz_video);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.PlayBtnVoice();
                onBackPressed();
            }
        });
//        my_jzvdStd.setUp(strPath,"", Jzvd.SCREEN_NORMAL,JZMediaIjk.class);
//        my_jzvdStd.startVideo();


        MediaController mediaController = new MediaController(this);
        video_play.setMediaController(mediaController);
        mediaController.setMediaPlayer(video_play);

        Uri uri = Uri.parse(strPath);
        video_play.setVideoURI(uri);

        video_play.start();
        video_play.requestFocus();

        EventBus.getDefault().register(this);
    }

    @Subscriber(tag="PlayComplete")
    private void PlayComplete(String str)
    {
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(!MyApp.bNormalExit)
//          EventBus.getDefault().post("","GotoExit");
    }


    private  void F_StopPlay()
    {

    }

    @Override
    protected void onResume() {
        super.onResume();
     //   MyApp.bNormalExit = false;
        MyApp.F_makeFullScreen(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        //myVideoView.stopPlayback();
//        if (Jzvd.backPress()) {
//            super.onBackPressed();
//            return;
//        }
       // MyApp.bNormalExit = true;
        //Jzvd.releaseAllVideos();
        super.onBackPressed();
        video_play.stopPlayback();


    }

    @Subscriber(tag = "GotoExit_joy")
    private  void GotoExit_joy(String str)
    {
        video_play.stopPlayback();
        finish();
        overridePendingTransition(0, 0);
    }


}
