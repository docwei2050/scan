package com.doc.saomiaodemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class CustomView extends View {


    private float[] pots = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f};

    private Shader scanShader; // 扫描渲染shader
    private Matrix matrix = new Matrix(); // 旋转需要的矩阵
    private int scanSpeed = 5; // 扫描速度
    private int scanAngle; // 扫描旋转的角度

    private Paint mPaintCircle; // 画圆用到的paint
    private Paint mPaintRadar; // 扫描用到的paint

    public CustomView(Context context, AttributeSet attrs) {
        this(context, null,0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 画圆用到的paint
        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.STROKE); // 描边
        mPaintCircle.setStrokeWidth(1); // 宽度
        mPaintCircle.setAlpha(100); // 透明度
        mPaintCircle.setAntiAlias(true); // 抗锯齿
        mPaintCircle.setColor(Color.parseColor("#B0C4DE")); // 设置颜色 亮钢兰色

        // 扫描用到的paint
        mPaintRadar = new Paint();
        mPaintRadar.setStyle(Paint.Style.FILL_AND_STROKE); // 填充
        mPaintRadar.setAntiAlias(true); // 抗锯齿

        post(run);
    }

    public CustomView(Context context) {
       this(context,null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < pots.length; i++) {
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * pots[i], mPaintCircle);
        }

        // 画布的旋转变换 需要调用save() 和 restore()
        canvas.save();

        scanShader = new SweepGradient(mWidth / 2, mHeight / 2,
                new int[]{Color.TRANSPARENT, Color.parseColor("#84B5CA")}, null);
        mPaintRadar.setShader(scanShader); // 设置着色器
        canvas.concat(matrix);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * pots[4], mPaintRadar);

        canvas.restore();
    }

    private int getMeasurement(int contentWidth, int widthMeasureSpec) {
        int spcifiWidth=MeasureSpec.getSize(widthMeasureSpec);
        int mode=MeasureSpec.getMode(widthMeasureSpec);
        switch (mode){
            case MeasureSpec.AT_MOST:
                return Math.min(spcifiWidth,contentWidth);
            case MeasureSpec.UNSPECIFIED:
                return contentWidth;
            case MeasureSpec.EXACTLY:
                return spcifiWidth;
            default:
                return 0;
        }
    }
    private int mWidth;
    private int mHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(h!=oldh||w!=oldw){
            mWidth=w;
            mHeight=h;
        }
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            scanAngle = (scanAngle + scanSpeed) % 360; // 旋转角度 对360取余
            matrix.postRotate(scanSpeed, mWidth / 2, mHeight / 2); // 旋转矩阵
            invalidate(); // 通知view重绘
            postDelayed(run, 130); // 调用自身 重复绘制
        }
    };
}