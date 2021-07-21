package com.joyhonest.joy_camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class JH_SeekBarA extends View {


    private int nWidth = 0;
    private int nHeight = 0;
    private String mOrientation="";
    private Button  button1;
    private Button  button2;

    private Bitmap bitmap;

    private int nValue = 0x80;

    private static final String TAG = "SeekBarJH_A";
    //判断进度条方向，默认是水平的
    public static final String VERTICAL = "VERTICAL";
    public JH_SeekBarA(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public JH_SeekBarA(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.e(TAG,"Measure: w = "+nWidth+"  h = "+nHeight);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        nWidth = w;
        nHeight = h;
    }

    boolean bIsMode = false;

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.JH_SeekBarA);
        String orientation = typedArray.getString(R.styleable.JH_SeekBarA_orientationSeekBarA);
        if(orientation!=null)
            mOrientation = orientation;

        if(mOrientation.endsWith(VERTICAL)) //竖
        {
            bIsMode  =true;
                this.setBackgroundResource(R.mipmap.seekbar_v_jh);
        }
        else
        {
            bIsMode  =false;
                this.setBackgroundResource(R.mipmap.seekbar_h_jh);
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(0xFF000000);
        mPaint.setTextSize(50);


        mCenterPoint = new Point();




        mCenterRect = new Rect();

        typedArray.recycle();


    }

    private boolean pointInRect(Point p, Rect rect) {
        if (p.x < rect.left) {
            return false;
        }
        if (p.x > rect.right) {
            return false;
        }
        if (p.y < rect.top) {
            return false;
        }
        if (p.y > rect.bottom) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int ww = getWidth();
        int hh = getHeight();
        Point point = new Point();
        point.x = x;
        point.y = y;

        Rect rect1 = new Rect();
        Rect rect2 = new Rect();



        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int w1 = Storage.dip2px(this.getContext(), 40);
                int H1 = Storage.dip2px(this.getContext(), 40);
                if (mOrientation.endsWith(VERTICAL))
                {

                    int dd = 0;
                    rect1.left = 0;
                    rect1.top = 0;
                    rect1.right = ww;
                    rect1.bottom = H1;
                    if (ww > H1) {
                        dd = (ww - H1) / 2;
                        rect1.inset(-dd, 0);
                    }


                    rect2.left =0;
                    rect2.top = hh-H1;
                    rect2.right = ww;
                    rect2.bottom = hh;
                    if (ww > H1) {
                        dd = (ww - H1) / 2;
                        rect2.inset(-dd, 0);
                    }

                    if (pointInRect(point, rect1)) {
                        nValue++;
                        if(nValue>0xC0)
                        {
                            nValue=0xC0;
                        }
                        invalidate();
                    }
                    if (pointInRect(point, rect2)) {
                        //Log.e(TAG, "dn clicked!");
                        nValue--;
                        if(nValue<0x40)
                        {
                            nValue=0x40;
                        }
                        invalidate();
                    }

                } else   //横
                {


                    int dd = 0;
                    rect1.left = 0;
                    rect1.top = 0;
                    rect1.right = w1;
                    rect1.bottom = hh;
                    if (hh > H1) {
                        dd = (hh - H1) / 2;
                        rect1.inset(0, -dd);
                    }


                    rect2.left = ww - w1;
                    rect2.top = 0;
                    rect2.right = ww;
                    rect2.bottom = hh;
                    if (hh > H1) {
                        dd = (hh - H1) / 2;
                        rect2.inset(0, -dd);
                    }

                    if (pointInRect(point, rect1)) {
                        Log.e(TAG, "left clicked!");
                        nValue--;
                        if(nValue<0x40)
                        {
                            nValue=0x40;
                        }
                        invalidate();
                    }
                    if (pointInRect(point, rect2)) {
                        Log.e(TAG, "right clicked!");
                        nValue++;
                        if(nValue>0xC0)
                        {
                            nValue=0xC0;
                        }
                        invalidate();
                    }
                }
            }


                break;
        }

        return true;
    }


    private int measureWidth;
    private int measureHeight;
    private Point mCenterPoint;
    private Paint mPaint;
    //private Paint mPaintRect;
    private  int nP=20;
    private Rect mCenterRect;

    private boolean bIsOne = true;



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


        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(8);
        mPaint.setTextSize(50);
        mPaint.setTextAlign(Paint.Align.CENTER);

        String text="测试：my text";
        //计算baseline
        Paint.FontMetrics fontMetrics=mPaint.getFontMetrics();
        float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
        float baseline=cx+distance;


        int dd = nValue-0x80;

        String str = ""+dd;

        if (!bIsMode) {
            //横
            baseline=cy+distance;
            canvas.drawText(str,mCenterPoint.x,baseline,mPaint);
        } else {
            canvas.rotate(-90.0f);
            //canvas.drawText(str,mCenterPoint.x,baseline,mPaint);
            canvas.drawText(str,-mCenterPoint.y,baseline,mPaint);
            canvas.rotate(90.0f);
        }
    }

    public  int F_GetValue()
    {
        return nValue;
    }

}
