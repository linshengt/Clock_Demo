package com.linshengt.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

import static java.lang.Math.*;

/**
 * Created by linshengt on 2016/5/9.
 */
public class MyView extends View {

    private int width;
    private int height;
    private Paint mPaintCircle;
    private Paint mPaintScale;
    private Paint mPaintCircleCenter;
    private Paint mPaintHour;
    private Paint mPaintMinute;
    private Paint mPaintSechead;
    private Paint mPaintSectail;
    private Paint mPaintText;
    private Calendar mCalendar;
    public static final int NEED_INVALIDATE = 0X23;

    private int circleRadius = 300;         /*<外圆半径*/
    private int circleWith = 10;            /*<外圆宽度*/
    private int scaleWith = 10;             /*<刻度线宽*/
    private int scaleLen = 30;              /*<刻度长度*/
    private int circleRadiusCenter = 15;  /*<内圆半径*/

    //每隔一秒，在handler中调用一次重新绘制方法
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case NEED_INVALIDATE:
                    mCalendar = Calendar.getInstance();
                    invalidate();//告诉UI主线程重新绘制
                    handler.sendEmptyMessageDelayed(NEED_INVALIDATE,1000);
                    break;
                default:
                    break;
            }
        }
    };

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCalendar = Calendar.getInstance();

        /*<外圆属性*/
        mPaintCircle = new Paint();
        mPaintCircle.setColor(Color.BLACK);//设置颜色
        mPaintCircle.setStrokeWidth(circleWith);//设置线宽
        mPaintCircle.setAntiAlias(true);//设置是否抗锯齿
        mPaintCircle.setStyle(Paint.Style.STROKE);//设置绘制风格

        /*<刻度属性*/
        mPaintScale = new Paint();
        mPaintScale.setColor(Color.BLACK);//设置颜色
        mPaintScale.setAntiAlias(true);//设置是否抗锯齿
        mPaintScale.setStyle(Paint.Style.STROKE);//设置绘制风格

        /*<圆心属性*/
        mPaintCircleCenter = new Paint();
        mPaintCircleCenter.setColor(Color.BLACK);//设置颜色
        mPaintCircleCenter.setAntiAlias(true);//设置是否抗锯齿
        mPaintCircleCenter.setStyle(Paint.Style.FILL);//设置绘制风格

        /*<时间文本属性*/
        mPaintText = new Paint();
        mPaintText.setColor(Color.BLACK);
        mPaintText.setStrokeWidth(20);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(60);

        /*<时针属性*/
        mPaintHour = new Paint();
        mPaintHour.setStrokeWidth(10);
        mPaintHour.setAntiAlias(true);
        mPaintHour.setColor(Color.BLACK);

        /*<分针属性*/
        mPaintMinute = new Paint();
        mPaintMinute.setStrokeWidth(10);
        mPaintMinute.setAntiAlias(true);
        mPaintMinute.setColor(Color.BLACK);

        /*<秒针头部属性*/
        mPaintSechead = new Paint();
        mPaintSechead.setStrokeWidth(5);
        mPaintSechead.setAntiAlias(true);
        mPaintSechead.setColor(Color.RED);

        /*<秒针尾部属性*/
        mPaintSectail = new Paint();
        mPaintSectail.setStrokeWidth(10);
        mPaintSectail.setAntiAlias(true);
        mPaintSectail.setColor(Color.RED);

        handler.sendEmptyMessage(NEED_INVALIDATE);//向handler发送一个消息，让它开启重绘
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画出大圆
        canvas.drawCircle(width / 2, height / 2, circleRadius, mPaintCircle);

        /*<画出刻度*/
        drawScale(canvas);

        /*<画出时间文字*/
        drawTimeText(canvas);

        /*<画出其他文字*/
        drawTextOther(canvas);

        /*<画出时间时针，分针，秒针*/
        drawIndicator(canvas);

        //画出内圆
        canvas.drawCircle(width / 2, height / 2, circleRadiusCenter, mPaintCircleCenter);

    }

    public void drawScale(Canvas canvas){
        //依次旋转画布，画出每个刻度
        for (int i = 1; i <= 60; i++) {
            canvas.save();
            canvas.rotate(360/60*i, width/2, height/2);
            if(i%5 == 0){
                mPaintScale.setStrokeWidth(scaleWith);
                canvas.drawLine(width/2, height/2 - circleRadius, width/2, height/2 - circleRadius + scaleLen, mPaintScale);
            }
            else {
                mPaintScale.setStrokeWidth(5);
                canvas.drawLine(width/2, height/2 - circleRadius, width/ 2, height/2 - circleRadius + scaleLen /2, mPaintScale);
            }
            canvas.restore();
        }
    }

    public void drawTimeText(Canvas canvas){

        String TargetText [] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

        for(int i = 1; i <= 12; i++){

            float offset = mPaintText.measureText(TargetText[i-1]) / 2;
            float circleRadiusText = 4*circleRadius/5;
            canvas.save();
            canvas.rotate(0,width/2,height/2);
            canvas.drawText(TargetText[i-1], (float) (width/2 +circleRadiusText* sin(i*PI/6)), (float) (height/2 -circleRadiusText* cos(i*PI/6) + offset),mPaintText );
            canvas.restore();
        }
    }
    public void drawTextOther(Canvas canvas){
        canvas.save();
        canvas.rotate(0,width/2,height/2);
        canvas.drawText("clock", width/2, height/2 + 150, mPaintText );
        canvas.restore();
    }
    public void drawIndicator(Canvas canvas){
        int minute = mCalendar.get(Calendar.MINUTE);//得到当前分钟数
        int hour = mCalendar.get(Calendar.HOUR);//得到当前小时数
        int sec = mCalendar.get(Calendar.SECOND);//得到当前秒数

        float secDegree = sec/60f*360;//得到秒针旋转的角度
        canvas.save();
        canvas.rotate(secDegree,width/2,height/2);
        canvas.drawLine(width/2,height/2-250,width/2,height/2,mPaintSechead);
        canvas.drawLine(width/2,height/2,width/2,height/2+100,mPaintSectail);
        canvas.restore();

        float minuteDegree = minute/60f*360;//得到分针旋转的角度
        canvas.save();
        canvas.rotate(minuteDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2 - 250, width / 2, height / 2 + 50, mPaintMinute);
        canvas.restore();

        float hourDegree = (hour*60+minute)/12f/60*360;//得到时钟旋转的角度
        canvas.save();
        canvas.rotate(hourDegree, width / 2, height / 2);
        canvas.drawLine(width / 2, height / 2 - 200, width / 2, height / 2 + 40, mPaintHour);
        canvas.restore();
    }
}

