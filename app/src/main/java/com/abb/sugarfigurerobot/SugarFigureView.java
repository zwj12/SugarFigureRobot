package com.abb.sugarfigurerobot;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * @author CNMIZHU7
 * @date 2/9/2020
 * description：
 */

public class SugarFigureView extends View {
    private static final String TAG = "SugarFigureView";
    private float density;
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;

    private int figureWidth = 640;
    private int figureHeight = 960;
    private int strokeWidth=5;
    private float start_x;
    private float start_y;
    private float end_x;
    private float end_y;

    private ArrayList<SugarFigurePath> listSugarFigurePath = new ArrayList<SugarFigurePath>();
    private SugarFigurePath sugarFigurePathCur;
    private GPath gPathCur;

    public SugarFigureView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        density = context.getResources().getDisplayMetrics().density;
        paint = new Paint();//创建一个画笔
        paint.setStyle(Paint.Style.STROKE);//设置非填充
        paint.setStrokeWidth(strokeWidth * density);//笔宽5像素
        paint.setColor(Color.MAGENTA);//设置为红笔
        paint.setAntiAlias(true);//锯齿不显示
        paint.setDither(true);//设置图像抖动处理
        paint.setStrokeJoin(Paint.Join.ROUND);//设置图像的结合方式
        paint.setStrokeCap(Paint.Cap.ROUND);//设置画笔为圆形样式
    }

    public SugarFigureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SugarFigureView);
        figureWidth = ta.getInteger(R.styleable.SugarFigureView_figureWidth, -1);
        figureHeight = ta.getInteger(R.styleable.SugarFigureView_figureHeight, -1);
        strokeWidth = ta.getInteger(R.styleable.SugarFigureView_sugarWidth, 5);
        ta.recycle();
        initView(context);
    }

    public void SetStrokeWidth(int strokeWidth){
        this.strokeWidth=strokeWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.bitmap == null) {
//            this.bitmap = Bitmap.createBitmap(figureWidth, figureHeight, Bitmap.Config.RGB_565);
            this.bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.RGB_565);
            this.canvas = new Canvas(bitmap);
            this.listSugarFigurePath.clear();
        }
        canvas.drawBitmap(bitmap, 0, 0, paint);
//		canvas.drawPath(path, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获取宽-测量规则的模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        // 获取高-测量规则的模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 设置wrap_content的默认宽 / 高值
        // 默认宽/高的设定并无固定依据,根据需要灵活设置
        // 类似TextView,ImageView等针对wrap_content均在onMeasure()对设置默认宽 / 高值有特殊处理,具体读者可以自行查看
        int mWidth = figureWidth;
        int mHeight = figureHeight;

        // 当布局参数设置为wrap_content时，设置默认值
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.sugarFigurePathCur=new SugarFigurePath();
                start_x = event.getX();//获取手指落下的x坐标
                start_y = event.getY();//获取手指落下的y坐标
                canvas.drawPoint(start_x, start_y, paint);//在画布上画点
                gPathCur=new GPath(null,start_x, start_y,0,0,GType.G0);
                this.sugarFigurePathCur.AddGPath(gPathCur);
                invalidate();
                performClick();
                break;
            case MotionEvent.ACTION_MOVE:
                end_x = event.getX();//获取手指移动的x坐标
                end_y = event.getY();//获取手指移动的y坐标
                canvas.drawLine(start_x, start_y, end_x, end_y, paint);//在画布上画线
                start_x = end_x;//将上一个终止点的x坐标赋值给起始点的x坐标
                start_y = end_y;//将上一个终止点的y坐标赋值给起始点的y坐标
                gPathCur=new GPath(null,end_x, end_y,0,0,GType.G1);
                this.sugarFigurePathCur.AddGPath(gPathCur);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                this.listSugarFigurePath.add(this.sugarFigurePathCur);
                break;
        }
        return true;
    }

}