package com.joyhonest.joy_camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class JH_SeekBar extends View {
    private static final String TAG = "SeekBarJH";
    //判断进度条方向，默认是水平的
    public static final String VERTICAL = "VERTICAL";

    private boolean bIsMode;
    private String mOrientation;
    //Rect是否可移动
    // private boolean bCenterRect = false;
    //一次判断
    private boolean bIsOne = true;

    private Bitmap bitmap;

    //可点击的三个矩形
//    private Rect mLeftRect;
    private Rect mCenterRect;
//    private Rect mRightRect;

    private Paint mPaint;
    private Paint mPaintRect;

    private Point mCenterPoint;


    //矩形当前坐标
    private int mCenterX;
    private int mCenterY;
    //x，y百分比值
    private int mPercentX= 50;
    private int mPercentY= 50;


    //监听接口
//    private OnTouchListener onTouchListener;
//    private OnPercentListener onPercentListener;

    //控件的宽高
    private int measureWidth;
    private int measureHeight;


    public JH_SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public JH_SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    private  int nP=20;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取控件宽高
        measureWidth = getMeasuredWidth();
        measureHeight = getMeasuredHeight();

        //控件中心点
        int cx = measureWidth / 2;
        int cy = measureHeight / 2;
        mCenterPoint.set(cx, cy);



        if (!bIsMode) { //横

            nP = (measureHeight*6)/47;
            mPaintRect.setStrokeWidth((float)nP);
            int nHH  =  measureHeight;
            int nWW =  (nHH *22)/47;


            //绘制进度条
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect dst = new Rect(0, 0, measureWidth, measureHeight);
            canvas.drawBitmap(bitmap, src, dst, mPaint);

            //第一次绘制时将矩形居中显示
            if (bIsOne) {
                mCenterRect = new Rect();
                mCenterRect.set(cx - nWW/2, 0+nP/2, cx + nWW/2, measureHeight-nP/2);
                //mRightRect.offset(measureWidth - 50, 0);
                canvas.drawRect(mCenterRect, mPaintRect);
                bIsOne = false;
            } else {
                canvas.drawRect(mCenterRect, mPaintRect);
                Log.i(TAG, "mCenterRect当前x轴的位置" + mCenterRect.centerX());
            }
        } else {
            //绘制进度条
            nP = (measureWidth*6)/47;

            mPaintRect.setStrokeWidth((float)nP);

            int nWW =  measureWidth;
            int nHH  = (nWW*22)/47;


            mPaintRect.setStrokeWidth((float)nP);
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect dst = new Rect(0, 0, measureWidth, measureHeight);
            canvas.drawBitmap(bitmap, src, dst, mPaint);
            //第一次绘制时将矩形居中显示
            if (bIsOne) {
                mCenterRect = new Rect();
                mCenterRect.set(0+nP/2, cy - nHH/2, measureWidth-nP/2, cy + nHH/2);
              //  mRightRect.offset(0, measureHeight - 80);
                canvas.drawRect(mCenterRect, mPaintRect);
                //Log.i(TAG, "矩形初始化成功");
                bIsOne = false;
            } else {
                canvas.drawRect(mCenterRect, mPaintRect);
              //  Log.i(TAG, "mCenterRect当前Y轴位置" + mCenterRect.centerY());
            }


        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int ww = getWidth();
        int hh = getHeight();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //开始回调
                //callBackStart();
                //根据点击位置移动矩形
                //downBorder(x, y);

                mCenterX = x;
                mCenterY = y;

                //移动到点击后的位置
                if (!bIsMode)
                {   //横屏



                    int da = x - mCenterRect.centerX();

                    if (mCenterRect.centerX() + da <= ww-mCenterRect.width()/2-nP/2 && mCenterRect.centerX() + da>=mCenterRect.width()/2+nP/2)
                    {
                        ;
                    }
                    else
                    {
                        if(mCenterRect.centerX() + da > ww-mCenterRect.width()/2-nP/2)
                        {
                            da = ww-mCenterRect.width()/2-mCenterRect.centerX()-nP/2;

                        }
                        else if(mCenterRect.centerX() + da<mCenterRect.width()/2+nP/2)
                        {
                            da = mCenterRect.width()/2-mCenterRect.centerX()+nP/2;

                        }
                        else
                        {
                            da = 0;
                        }

                    }
                    mCenterRect.offset(da, 0);
                    invalidate();
                } else {
                    int da = y - mCenterRect.centerY();
                    if (mCenterRect.centerY() + da <= hh-mCenterRect.height()/2-nP/2 && mCenterRect.centerY() + da>=mCenterRect.height()/2+nP/2) {
                        ;
                    }
                    else
                    {
                        if(mCenterRect.centerY() + da > hh-mCenterRect.height()/2-nP/2)
                        {
                            da = hh-mCenterRect.height()/2-mCenterRect.centerY()-nP/2;
                        }
                        else if(mCenterRect.centerY() + da<mCenterRect.height()/2+nP/2)
                        {
                            da = mCenterRect.height()/2-mCenterRect.centerY()+nP/2;
                        }
                        else
                        {
                            da = 0;
                        }
                    }
                    mCenterRect.offset(0, da);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                    //移动矩形
                    if (!bIsMode) { //横屏
                        int x1 = (int) event.getX();
                        int da = x1 - mCenterX;
                        mCenterX = x1;

                        if (mCenterRect.centerX() + da <= ww-mCenterRect.width()/2-nP/2 && mCenterRect.centerX() + da>=mCenterRect.width()/2+nP/2)
                        {
                            ;
                        }
                        else
                        {
                            if(mCenterRect.centerX() + da > ww-mCenterRect.width()/2-nP/2)
                            {
                                da = ww-mCenterRect.width()/2-mCenterRect.centerX()-nP/2;

                            }
                            else if(mCenterRect.centerX() + da<mCenterRect.width()/2+nP/2)
                            {
                                da = mCenterRect.width()/2-mCenterRect.centerX()+nP/2;

                            }
                            else
                            {
                                da = 0;
                            }

                        }
                        mCenterRect.offset(da, 0);
                        invalidate();
                        Log.e(TAG,"dd = "+F_GetValue());

                    } else {

                            int y1 = (int) event.getY();
                            int da = y1 - mCenterY;
                            mCenterY = y1;

                        if (mCenterRect.centerY() + da <= hh-mCenterRect.height()/2-nP/2 && mCenterRect.centerY() + da>=mCenterRect.height()/2+nP/2) {
                            ;
                        }
                        else
                        {
                            if(mCenterRect.centerY() + da > hh-mCenterRect.height()/2-nP/2)
                            {
                                da = hh-mCenterRect.height()/2-mCenterRect.centerY()-nP/2;
                            }
                            else if(mCenterRect.centerY() + da<mCenterRect.height()/2+nP/2)
                            {
                                da = mCenterRect.height()/2-mCenterRect.centerY()+nP/2;
                            }
                            else
                            {
                                da = 0;
                            }
                        }
                        mCenterRect.offset(0, da);
                        invalidate();
                        Log.e(TAG,"dd = "+F_GetValue());

                    }
//                    int moveX = (int) event.getX();
//                    int moveY = (int) event.getY();
//                    callBackPercent(mCenterRect.centerX(),mCenterRect.centerY());
                    //显示坐标到接口
                    //callBackPosition(mCenterRect.centerX(), mCenterRect.centerY());
                    break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:

                        //callBackFinish();

                break;
        }
        return true;
    }

    //初始化数据
    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SeekBarJH);
        String orientation = typedArray.getString(R.styleable.SeekBarJH_orientationSeekBar);

        if (orientation.equals(VERTICAL)) {
            mOrientation = orientation;
            bIsMode = true;

            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.seekbartop_jh);
        } else {
            mOrientation = orientation;

            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.jiantou_jh);
            bIsMode = false;
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mCenterPoint = new Point();


        mPaintRect = new Paint();
        mPaintRect.setAntiAlias(true);
        mPaintRect.setStrokeWidth((float) 10.0);
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setColor(Color.BLACK);

//        mLeftRect = new Rect(0, 0, 70, 70);
//        mRightRect = new Rect(0, 0, 70, 70);
        typedArray.recycle();
    }

    /**
     * Rect移动事件的监听接口
     */
    public interface OnTouchListener {
        //开始
        void start();

        /**
         * @param x Rect当前x轴坐标
         * @param y Rect当前y轴坐标
         */
        void position(int x, int y);

        //结束
        void Finish();

    }

    //down事件下调用已实现接口的start方法
//    private void callBackStart() {
//        if (onTouchListener != null) {
//            onTouchListener.start();
//        }
//        if (onPercentListener!=null){
//            onPercentListener.onStart();
//        }
//    }
//
//    //move事件中调用已实现接口的position方法
//    private void callBackPosition(int x, int y) {
//        if (onTouchListener != null) {
//            onTouchListener.position(x, y);
//        }
//
//
//    }

    private void callBackPercent(int x,int y){
        int countX = 0;
        int countY = 0;
        int lenX=mCenterPoint.x*2/100;
        int lenY=mCenterPoint.y*2/100;
        if(lenX !=0 ) {
            countX = x / lenX;
        }
        if(lenY!=0) {
            countY = y / lenY;
        }

        if (countX<0){
            countX=0;
        }
        if (countX>100){
            countX=100;
        }
        if (countY<0){
            countY=0;
        }
        if (countY>100){
            countY=100;
        }
        mPercentX += countX;
        mPercentY += countY;
        if (mPercentY>50){
            mPercentY=50-(mPercentY-50);
        }else {
            mPercentY=50+(50-mPercentY);
        }
//        if (onPercentListener!=null){
//            onPercentListener.percent(mPercentX,mPercentY);
//        }
        mPercentX=0;
        mPercentY=0;
    }

//    private void callBackFinish() {
//        if (onTouchListener != null) {
//            onTouchListener.Finish();
//        }
//        if (onPercentListener!=null){
//            onPercentListener.onFinish();
//        }
//    }

    public interface OnPercentListener{
        void onStart();

        void percent(int x,int y);

        void onFinish();
    }

//    //添加Rect移动事件的监听接口
//    public void setOnTouchListener(OnTouchListener listener) {
//        onTouchListener = listener;
//    }
//
//    public void setOnPercentListener(OnPercentListener listener){
//        onPercentListener=listener;
//    }

    //根据点击位置移动Rect
//    private void downBorder(int x, int y) {
//        if (!bIsMode) {
//            if (mLeftRect.contains(x, y)) {
//                if (mCenterRect.centerX() > 25) {
//                    mCenterRect.offset(-mCenterPoint.x*2/100, 0);
//                    invalidate();
//                }
//
//            } else if (mRightRect.contains(x, y)) {
//                if (mCenterRect.centerX() < 730) {
//                    mCenterRect.offset(mCenterPoint.x*2/100, 0);
//                    invalidate();
//                }
//            }
//
//
//        } else {
//            if (mLeftRect.contains(x, y)) {
//                if (mCenterRect.centerY() > 25) {
//                    mCenterRect.offset(0, -mCenterPoint.y*2/100);
//                    invalidate();
//                }
//            } else if (mRightRect.contains(x, y)) {
//                if (mCenterRect.centerY() < 730) {
//                    mCenterRect.offset(0, mCenterPoint.y*2/100);
//                    invalidate();
//                }
//            }
//        }
//    }


    public  void F_SetValue(int nP)
    {
        if(nP>=255)
               nP=256;
        if(nP<0)
        {
               nP=0;
        }
        if(mCenterRect==null)
        {
            return;
        }
        float da = nP/256.0f;
        if(bIsMode) //竖
        {
            int h = mCenterRect.height();
            int H = getHeight()-h;
            int top = H-((int)(da*H)+h/2);
            int bot = top+mCenterRect.height();
            mCenterRect.top = top;
            mCenterRect.bottom = bot;
            invalidate();
        }
        else
        {
            int w = mCenterRect.width();
            int W = getWidth()-w;
            int left = (int)(da*W)+w/2;
            int right = left+mCenterRect.width();
            mCenterRect.left = left;
            mCenterRect.right = right;
            invalidate();
        }


    }

    public  int F_GetValue()
    {
        int cent = 0;
        if(mCenterRect==null)
        {
            return 0x80;
        }
        if(bIsMode)  //竖
        {
                cent= mCenterRect.centerY();
                int da=getHeight()-mCenterRect.height();
                cent-=mCenterRect.height()/2;
                int i = cent*100/(da);
                if(i<0)
                    i=0;
                if(i>100)
                    i=100;
                  i=   100-i;
                float da1 = i*256/100.0f;
                da = (int)da1;
                if(da<0)
                    da = 0;
                if(da>255)
                    da = 255;
                return da;

        }
        else
        {
            cent= mCenterRect.centerX();
            int da=getWidth()-mCenterRect.width();
            cent-=mCenterRect.width()/2;
            int i = cent*100/(da);
            if(i<0)
                i=0;
            if(i>100)
                i=100;
            float da1 = i*256/100.0f;
            da = (int)da1;
            if(da<0)
                da = 0;
            if(da>255)
                da = 255;
            return da;
        }

    }

}
