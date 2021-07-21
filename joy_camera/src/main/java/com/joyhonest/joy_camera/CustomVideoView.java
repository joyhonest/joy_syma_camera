package com.joyhonest.joy_camera;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.VideoView;

/**
 * Created by lexinxingye_mini_1 on 2018/5/21.
 * 在使用VideoView播放视频文件时，如果视频的分辨率较小（640X480）就会出现显示位置不在屏幕中央等情况
 * 可以通过重写VideoView设置VideoView的setMeasureDimension()方法来解决该问题
 */

public class CustomVideoView extends VideoView {

    public CustomVideoView(Context context) {
        super(context);

    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        super.setOnPreparedListener(l);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


}
