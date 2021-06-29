package com.joyhonest.joy_camera;

import android.graphics.Bitmap;

/**
 * Created by aivenlau on 2017/2/15.
 */

public class MyNode {
    public static final int Stauts_normal = 0;
    public static final int Status_downloading = 1;
    public static final int Status_downloaded = 2;
    public static int TYPE_Local = 0;
    public static int TYPE_Remote = 1;

    public int nStatus;
    public int nType;
    public int nGetType;
    public Bitmap bitmap;
    public String sPath;           // local filename
    public String sUrl;           //remote  filename
    public String sText;
    public long nPre;

    public int nSelect;

    public long bytesWritten;
    public long totalSize;
    public int nPostion;


    public MyNode() {
        nStatus = Stauts_normal;
        nType = TYPE_Local;
        bitmap = null;
        sPath = "";
        sUrl = "";
        sText = "";
        nSelect = 0;
    }

    public MyNode(int nType) {
        nStatus = Stauts_normal;
        this.nType = nType;
        bitmap = null;
        sPath = "";
        sUrl = "";
        sText = "";
        nSelect = 0;

    }

}
