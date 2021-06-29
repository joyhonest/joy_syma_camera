package com.joyhonest.joy_camera;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by aivenlau on 2017/2/16.
 */

public class WaitView extends LinearLayout {

    private TextView   TitleViewA;
    private ImageView  spaceshipImage;
    Animation hyperspaceJumpAnimation;
    public WaitView(Context context, AttributeSet attrs) {
         super(context, attrs);
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.joyh_loading_dialog, this);
        // 获取控件
        TitleViewA = (TextView)findViewById(R.id.tipTextView);
        spaceshipImage = (ImageView)findViewById(R.id.img);
        hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.joyh_loading_animation);
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        }

    private   void setTitleView(String str)
    {
        TitleViewA.setText(str);
    }
    private  void setTitleView(int idStr)
    {
        TitleViewA.setText(idStr);
    }
}
