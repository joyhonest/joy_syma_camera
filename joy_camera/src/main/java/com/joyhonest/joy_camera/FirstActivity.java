package com.joyhonest.joy_camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.joyhonest.joy_camera.databinding.JoyhActivityFirstBinding;


public class FirstActivity extends AppCompatActivity {
    private JoyhActivityFirstBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=JoyhActivityFirstBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_first);
        setContentView(binding.getRoot());
        MyApp.F_makeFullScreen(this);

        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyApp.PlayBtnVoice();
                if(MyApp.CheckConnectedDevice())
                {
                    Intent mainIntent = new Intent(FirstActivity.this, Joyh_TerraCopter.class);
                    startActivity(mainIntent);
                }
                else
                {
                    F_DispDialog();
                }
            }
        });

        binding.btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
               if(MyApp.CheckConnectedDevice())
               {
                  //  wifination.naStartRead20000_20001();
                    Intent mainIntent = new Intent(FirstActivity.this, SettingsActivity.class);
                    startActivity(mainIntent);
                    overridePendingTransition(0, 0);
                }
                else
                {
                    F_DispDialog();
                }

            }
        });

        binding.btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.PlayBtnVoice();
                Intent mainIntent = new Intent(FirstActivity.this, HelpActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(0, 0);

            }
        });

    }

    private  void F_DispDialog()
    {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Please connect to Tweeze-it or FPV-WiFi first")
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        TextView title = new TextView(this);
        title.setText("Tweeze-it not found");
        title.setPadding(0, 25, 0, 0);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        dialog.setCustomTitle(title);



        //dialog.setCustomTitle();

        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        dialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }

        });
        dialog.show();


    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }
}
