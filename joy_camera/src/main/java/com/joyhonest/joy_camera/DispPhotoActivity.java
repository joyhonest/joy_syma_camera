package com.joyhonest.joy_camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.FileDescriptor;
import java.util.List;

public class DispPhotoActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{


    private Button btn_back;
    private ViewPager photo_vp;
    private TextView index_view;

    private List<String> nodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joyh_activity_disp_photo_jh);
        MyApp.F_makeFullScreen(this);
        btn_back = findViewById(R.id.btn_back);
        photo_vp = findViewById(R.id.photo_vp);
        index_view =findViewById(R.id.index_view);
        index_view.setText((MyApp.dispListInx+1)+"/"+MyApp.dispList.size());
        nodes = MyApp.dispList;

        photo_vp.setAdapter(adapter);

        photo_vp.addOnPageChangeListener(this);
        photo_vp.setCurrentItem(MyApp.dispListInx);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.PlayBtnVoice();
                onBackPressed();
            }
        });

        EventBus.getDefault().register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(!MyApp.bNormalExit)
//            EventBus.getDefault().post("a","GotoExit");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
   //     MyApp.bNormalExit = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
     //   MyApp.bNormalExit = false;
        MyApp.F_makeFullScreen(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override

    public void onPageScrollStateChanged(int arg0) {


    }


    @Override

    public void onPageScrolled(int arg0, float arg1, int arg2) {


    }


    @Override

    public void onPageSelected(int arg0) {
        //fileInx_label.setText("" + (arg0 + 1) + "/" + nodes.size());
        index_view.setText((arg0+1)+"/"+MyApp.dispList.size());
    }

    private PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
//                            return imagePathArray.size();
            return nodes.size();
            //return 4;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        /**
         * ????????????page?????????2??????2????????????item?????????
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object o) {
            container.removeView((View) o);
        }

        //??????ViewPager????????????????????????view
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final String strPath = nodes.get(position);

            MyImageView im = new MyImageView(DispPhotoActivity.this);
            im.bCanScal = true;
            im.setMaxZoom(3.5f);
            im.setScaleType(ImageView.ScaleType.FIT_XY);
            im.setImageDrawable(new BitmapDrawable(getResources(), LoadBitmap(strPath)));
            container.addView(im);
            return im;
        }


    };

    private Bitmap LoadBitmap(String sPath) {

        Uri uri = Uri.parse(sPath);

        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);

            options.inJustDecodeBounds = false;
            //???????????????
            int be = (int) (options.outHeight / (float) 1280);
            if (be <= 0)
                be = 1;
            options.inSampleSize = be;
            //???????????????????????????????????????options.inJustDecodeBounds ?????? false???
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
            parcelFileDescriptor.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        // ??????????????????????????????
//        Bitmap bitmap = BitmapFactory.decodeFile(sPath, options); //????????????bm??????
//        options.inJustDecodeBounds = false;
//        //???????????????
//        int be = (int) (options.outHeight / (float) 720);
//        if (be <= 0)
//            be = 1;
//        options.inSampleSize = be;
//        //???????????????????????????????????????options.inJustDecodeBounds ?????? false???
//        bitmap = BitmapFactory.decodeFile(sPath, options);
//        return bitmap;
    }

    @Subscriber(tag = "GotoExit_joy")
    private  void GotoExit_joy(String str)
    {
        finish();
        overridePendingTransition(0, 0);
    }



}
