package com.joyhonest.joy_camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.WIFI_SERVICE;

public class MyApp  // extends Application
{


    public  static  boolean bBrowPhoto = false;




    public static  int nBattery = 3;
    //按钮声音
    private static int music_photo = -1;
    private static int music_btn = -1;
    public static SoundPool soundPool;

    private static Context singleton=null;
    //本地图片和视频存储路径
    public static String sLocalPath = "";
    public static String sSDPath = "";
    public static boolean bIsConnect = false;
  //  public static  boolean  bNormalExit = false;

    //保存按钮的ON和OFF设置，应用程序退出不会保存
    public static boolean bisHighLimited=true;
    public static boolean bisRightMode=false;
    public static boolean bisShowControler=true;
    public static boolean bisAutoSave=false;
    public static boolean bisResetTuneData=false;


    //判断查看的是图片还是视频
    public static final int Brow_Photo = 0;
    public static final int Brow_Video = 1;
    public static int BROW_TYPE = Brow_Photo;

    public static List<String> dispList=new ArrayList<>();
    public static int dispListInx=0;
    //飞控设定

    public  static  int   nSpeed = 0;  //0 Low  1 H




//    @Override
//    public void onCreate()
    public static void Init( Context  context)
    {
        singleton=context;
//设置音频池对象
        if (Build.VERSION.SDK_INT >= 21) {  //androdi 5.0 以上
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(2);//传入音频数量
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);//设置音频流的合适的属性
            builder.setAudioAttributes(attrBuilder.build());//加载一个AudioAttributes
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        music_photo = soundPool.load(context, R.raw.photo_m_joy, 1);
        music_btn = soundPool.load(context, R.raw.button46_joy, 1);

    }

    //播放点击按钮的音效
    public static void PlayBtnVoice() {

        if (music_btn != -1)
            soundPool.play(music_btn, 1, 1, 0, 0, 1);
    }

    //显示图片的音效
    public static void PlayPhotoMusic() {

        if (music_photo != -1)
            soundPool.play(music_photo, 1, 1, 0, 0, 1);

    }


    public static void F_CreateLocalDir(String str){
        boolean mRemote=false;
        String StroragePath="";
        String sVendor="";
        String sVendor_SD=null;
        sVendor=str;

        if (isAndroidQ()){
            File file = singleton.getExternalFilesDir(sVendor);
            if (file!=null){
                sLocalPath=file.getAbsolutePath();
            }

        }  else {
            try {
                StroragePath = Environment.getExternalStorageDirectory().getPath();
            } catch (Exception e) {
                return;
            }
            String recDir;
            File fdir;
            recDir = String.format("%s/%s", StroragePath, sVendor);
            fdir = new File(recDir);
            boolean re = false;
            if (!fdir.exists()) {
                re = fdir.mkdirs();
            }
            sLocalPath = recDir;

//            recDir = String.format("%s/%s", StroragePath, sVendor_SD);
//            fdir = new File(recDir);
//            if (!fdir.exists()) {
//                re = fdir.mkdirs();
//            }
//            sSDPath = recDir;
        }

    }


    public static void F_Save2ToGallery(String filename, boolean bPhoto) {
        if(singleton == null)
            return;

        if(isAndroidQ())
        {
            File file1 = new File(filename);
            if (!file1.exists()) {
                return;
            }

            String sVedor = file1.getParent();
            sVedor = sVedor.substring(sVedor.lastIndexOf("/") + 1);

            String slocal = "";



            String sfile = filename.substring(filename.lastIndexOf("/") + 1);
            String stype = filename.substring(filename.lastIndexOf(".") + 1);
            ContentResolver contentResolver = singleton.getContentResolver();
            ContentValues values = new ContentValues();

            slocal = Environment.DIRECTORY_DCIM + File.separator + sVedor;

            Uri uri = null;
            if(F_CheckIsExit(slocal,sfile,bPhoto))
                return;
            if (bPhoto) {
                values.put(MediaStore.Images.Media.DISPLAY_NAME, sfile);
                if (stype.equalsIgnoreCase("png")) {
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                } else {
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                }
                values.put(MediaStore.Images.Media.RELATIVE_PATH, slocal);
                uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);
            } else {
                File file = new File(filename);
                values.put(MediaStore.Video.Media.DISPLAY_NAME, sfile);
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.Media.RELATIVE_PATH, slocal);
                uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        values);
            }

            if (uri != null) {
                try {
                    OutputStream outputStream = contentResolver.openOutputStream(uri);
                    File file = new File(filename);
                    //2、建立数据通道
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buf = new byte[1024 * 500];
                    int length = 0;
                    while ((length = fileInputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, length);
                    }
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                    File file = new File(filename);
//                    if (file.isFile() && file.exists()) {
//                        file.delete();
//                    }

            }
        }
        else {
            try {
                ContentResolver contentResolver = singleton.getContentResolver();
                final ContentValues values = new ContentValues();
                if (bPhoto) {
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.DATA, filename);
                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
                } else {
                    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                    values.put(MediaStore.Video.Media.DATA, filename);
                    Uri uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            values);
                }
                Uri uri1 = Uri.parse("file://" + filename);
                singleton.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri1));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public  static  boolean F_CheckIsExit(String slocal,String sfile,boolean bPhoto)
    {
        if (singleton == null)
            return false;

        ContentResolver resolver = singleton.getContentResolver();
        Cursor cursor =null;

        if(!slocal.endsWith("/"))
        {
            slocal+="/";
        }

        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        if(!bPhoto)
        {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        if (bPhoto)
        {
            cursor = resolver.query(contentUri, new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.RELATIVE_PATH + "=? and " + MediaStore.Images.Media.DISPLAY_NAME + "=?",
                    new String[]{slocal, sfile}, null);

        } else {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            cursor = resolver.query(contentUri, new String[]{MediaStore.Video.Media._ID},
                    MediaStore.Video.Media.RELATIVE_PATH + "=? and " + MediaStore.Video.Media.DISPLAY_NAME + "=?",
                    new String[]{slocal, sfile}, null);

        }
        if(cursor!=null)
        {
            int a = cursor.getCount();
            cursor.close();
            return a>0;
        }
        return  false;
    }

    private static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    //删除图片或者视频，并且把它从系统图库中清除
    public static void DeleteImage(String imgPath)
    {
        if(singleton == null)
            return;
        if(isAndroidQ())
        {
            String stype = imgPath.substring(imgPath.lastIndexOf(".") + 1);
            Cursor cursor;
            ContentResolver resolver = singleton.getContentResolver();
            String sfile = imgPath.substring(imgPath.lastIndexOf("/") + 1);

            File file1 = new File(imgPath);
            if (!file1.exists()) {
                return;
            }

            String sVedor = file1.getParent();
            sVedor = sVedor.substring(sVedor.lastIndexOf("/") + 1);

            String slocal = "";
            boolean bPhoto = false;
            if (stype.equalsIgnoreCase("jpg") || stype.equalsIgnoreCase("png"))
            {
                bPhoto = true;
            }
//            if (bPhoto) {
//                slocal = Environment.DIRECTORY_DCIM + File.separator + sVedor;
//            } else {
//                slocal = Environment.DIRECTORY_DCIM + File.separator + sVedor;
//            }
            slocal = Environment.DIRECTORY_DCIM + File.separator + sVedor;
            if(!slocal.endsWith("/"))
            {
                slocal+="/";
            }

            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            if (bPhoto)
            {
                cursor = resolver.query(contentUri, new String[]{MediaStore.Images.Media._ID},
                        MediaStore.Images.Media.RELATIVE_PATH + "=? and " + MediaStore.Images.Media.DISPLAY_NAME + "=?",
                        new String[]{slocal, sfile}, null);

            } else {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                cursor = resolver.query(contentUri, new String[]{MediaStore.Video.Media._ID},
                        MediaStore.Video.Media.RELATIVE_PATH + "=? and " + MediaStore.Video.Media.DISPLAY_NAME + "=?",
                        new String[]{slocal, sfile}, null);

            }

            boolean result = false;
            if (cursor != null) {
                try {
                    while (cursor.moveToFirst()) {
                        long id = cursor.getLong(0);
                        //Uri contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                        String[] columnNames = cursor.getColumnNames();
//                        for (String columnName : columnNames) {
//                            String s = cursor.getString(cursor.getColumnIndex(columnName));
//                            Log.e("TAG",s);
//                        }
                        Uri uri = ContentUris.withAppendedId(contentUri, id);
                        int count = resolver.delete(uri, null, null);
                        result = count == 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(cursor!=null)
                {
                    cursor.close();
                }
                {
                    File file = new File(imgPath);
                    if (file.isFile() && file.exists()) {
                        file.delete();
                    }
                }
            }


        }
        else {

            //Context mContext =  singleton.getApplicationContext();
            String stype = imgPath.substring(imgPath.lastIndexOf(".") + 1);
            ;//imgPath.substring(imgPath.length()-3,imgPath.length());
            ContentResolver resolver = singleton.getContentResolver();
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor;
            {
                if (stype.equalsIgnoreCase("jpg") || stype.equalsIgnoreCase("png")) {
                    cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                            new String[]{imgPath}, null);
                } else {
                    cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=?",
                            new String[]{imgPath}, null);
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
            }
            boolean result = false;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        long id = cursor.getLong(0);
                        Uri uri = ContentUris.withAppendedId(contentUri, id);
                        int count = resolver.delete(uri, null, null);
                        result = count == 1;
                    }
                } catch (Exception e) {
                    ;
                }
                {
                    File file = new File(imgPath);
                    if (file.isFile() && file.exists()) {
                        file.delete();
                    }
                }
               // cursor.close();
            }


        }

    }
    public static void F_makeFullScreen(Context context)
    {
        checkDeviceHasNavigationBar(context);
    }

    //获取是否存在NavigationBar
    public  static void checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;

        Activity activity = (Activity) context;

        Window window =activity.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }

        if(hasNavigationBar)
        {

            // activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide navbar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }
    }



    public static String getFileNameFromDate(boolean bVideo, boolean blocal) {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.getDefault());
        String strDate = f.format(d);
        String StroragePath = "";

        StroragePath = sLocalPath;
        if(!blocal)
            StroragePath = sSDPath;
        String ext = "mp4";
        if (!bVideo)
        {
            ext = "png";
        }
        String recDir =  StroragePath;
        File dirPath = new File(recDir);
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
        String recPath = String.format("%s/%s.%s", recDir,strDate,ext);
        if(!blocal)
            recPath = String.format("%s/%s.%s", recDir,strDate,ext);
        return recPath;
    }
    public static  boolean CheckConnectedDevice(){
        if(singleton == null)
            return false;
        WifiManager wifi_service = (WifiManager)singleton.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info=null;
        try {
            info = wifi_service.getConnectionInfo();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        String wifiId;
        wifiId = (info != null ? info.getSSID() : null);
        if (wifiId != null) {
            wifiId = wifiId.replace("\"", "");
            if (wifiId.length() > 4)
                wifiId = wifiId.substring(wifiId.length() - 4);
        } else {
            wifiId = "nowifi";
        }
        String sIP = "";
        try
        {
            sIP = intToIp(info.getIpAddress());
        }
        catch (NullPointerException e)
        {
            ;
        }

        int x = sIP.lastIndexOf(".");
        sIP = sIP.substring(0, x);
        if(sIP.equalsIgnoreCase("192.168.27") ||
                sIP.equalsIgnoreCase("192.168.29")  ||
                sIP.equalsIgnoreCase("192.168.30") ||
                sIP.equalsIgnoreCase("192.168.33") ||         //MJ  支持SD
                sIP.equalsIgnoreCase("192.168.34"))           //H264 支持SD卡
        {
            if(sIP.equalsIgnoreCase("192.168.27"))
            {
                Command.sIP = "192.168.27.1";
            }
            if(sIP.equalsIgnoreCase("192.168.29"))
            {
                Command.sIP = "192.168.29.1";
            }
            if(sIP.equalsIgnoreCase("192.168.30"))
            {
                Command.sIP = "192.168.30.1";
            }

            if(sIP.equalsIgnoreCase("192.168.33"))
            {
                Command.sIP = "192.168.33.1";
            }
            if(sIP.equalsIgnoreCase("192.168.34"))
            {
                Command.sIP = "192.168.34.1";
            }

            return true;
        }else {
            Command.sIP = "";
            return false;
        }
    }

    public static SharedPreferences getSharedPreferences(String str)
    {
        return singleton.getSharedPreferences(str, 0);
    }
    public static boolean isAndroidQ(){
       // return (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q);
        return false;

    }


    public static void F_Exit()
    {
         EventBus.getDefault().post("","GotoExit_joy");
    }

}
