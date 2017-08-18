package com.doc.saomiaodemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class YcSaomiaoView extends View {

    private Paint mPaint,mPaintCircle;
    private float mValue;
    private float initValue=0;
    private float endValue=360;
    private ValueAnimator mAnimator;
    private float mContentWidth;
    private float mContetnHeight;
    private Paint mSmallCirclePaint;
    private Context mContext;
    private Matrix mMatrix;
    private float mCenterX;
    private float mCenterY;
    private float mRadius;
    //用于奇数偶数点的调整计数
    private int count;
    private SparseArray<Point> points=new SparseArray<>();
    private int mMinX;
    private int mMinY;
    private int mMaxX;
    private int mMaxY;

    public void startUp(){
        if(mAnimator!=null&&mAnimator.isRunning()){
            return;
        }
        // 启动就是不断的获取渐变值,然后重绘
        mAnimator = ValueAnimator.ofFloat(initValue,endValue);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimator.setDuration(8000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
                //第一次走完到后面每次开始时,这个方法调用,我们就开始调整点的位置,但是注意这里只做部分点调整
                count++;
                for(int i=0;i<5;i++){
                    int needX = (int) (mMinX+Math.random()*(mMaxX-mMinX+1));
                    int needY = (int) (mMinY+Math.random()*(mMaxY-mMinY+1));
                    Point point=points.get(i);
                    if(point==null) {
                        point = new Point(needX, needY);
                    }else{
                        if((i+count)%2==0) {
                            point.x = needX;
                            point.y = needY;
                        }
                    }
                    points.append(i,point);
                }
            }
        });


    }
    public void end(){
        if(mAnimator!=null&&mAnimator.isRunning()) {
            mAnimator.cancel();
            initValue = mValue;
            endValue = initValue + 360;
        }
    }
    public YcSaomiaoView(Context context, AttributeSet attrs) {
       this(context, attrs,0);
    }

    public YcSaomiaoView(Context context) {
        this(context,null);
    }

    public YcSaomiaoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
   //设置画笔
    private void init(Context context) {
        mContext=context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mMatrix = new Matrix();

        mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle .setColor(Color.WHITE);
        mPaintCircle .setStyle(Paint.Style.STROKE);


        mSmallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSmallCirclePaint .setColor(Color.WHITE);
        mSmallCirclePaint .setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStrokeCircles(canvas);
        drawPoints(canvas);
        canvas.save();
        drawBigCircleandRotateCanvas(canvas);
        canvas.restore();
    }
    //绘制5个白点
    private void drawPoints(Canvas canvas) {
        for(int i=0;i<points.size();i++){
            Point point=points.get(i);
            canvas.drawCircle(point.x,point.y,YcDensityUtil.dip2px(mContext,4),mSmallCirclePaint);
        }

    }
    //画最大的圆圈,这里效果看成以为是扇形,其实是整个圆圈,只不过调整postions
    private void drawBigCircleandRotateCanvas(Canvas canvas) {
        mMatrix.setRotate(mValue,mCenterX,mCenterY);
        canvas.concat(mMatrix);
        canvas.drawCircle(mCenterX,mCenterY,mRadius,mPaint);
    }

   //画多个空心圆
    private void drawStrokeCircles(Canvas canvas) {
        //考虑padding
        float everyLenght=mRadius*1.0f/5;
        for(int i=1;i<6;i++){
                canvas.drawCircle(mCenterX, mCenterY, everyLenght*i, mPaintCircle);
        }

    }
    //保证在除精确测量外还能显示,默认大小为80
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        //就是当用户的设置为atMost的时候,要指定为Exactly测量
        int contentWidth= YcDensityUtil.dip2px(getContext(),80);
        int contentHeight= YcDensityUtil.dip2px(getContext(),80);
        //用户必须的是精确测量啊
        int height=getMeasurement(contentHeight,heightMeasureSpec);
        int width=getMeasurement(contentWidth,widthMeasureSpec);
        int needValue=Math.min(width,height);
        setMeasuredDimension(needValue,needValue);
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
            //考虑padding的情况
            mContentWidth = mWidth-getPaddingRight()-getPaddingLeft();
            mContetnHeight = mHeight-getPaddingTop()-getPaddingBottom();
            //最大圆的圆心
            mCenterX = mContentWidth*1.0f/2+getPaddingLeft();
            mCenterY = mContetnHeight*1.0f/2+getPaddingTop();
            //最大圆的半径
            mRadius = (mWidth-getPaddingLeft()-getPaddingRight())*1.0f/2;
            //渐变值的颜色
            int[] colors=new int[]{mContext.getResources().getColor(R.color.colorlightwhite), mContext.getResources().getColor(R.color.colorwhite)};
            //针对渐变色来调整显示
            float[] positions=new float[]{0.9f,1.0f};
            SweepGradient sweeep=new SweepGradient(mCenterX,mCenterY,colors,positions);
            mPaint.setShader(sweeep);

            //生成5个随机点在圆圈内,,这里偷懒了,直接让点在圆中正方形的范围
            int length= (int) Math.sqrt(mRadius*mRadius/2);
            int offset= (int) (mRadius-length);
            //生成随机的在圆内的5个点
            mMinX = getPaddingLeft()+offset;
            mMinY = getPaddingTop()+offset;
            mMaxX = mWidth-getPaddingRight()-offset;
            mMaxY = mHeight-getPaddingBottom()-offset;
            for(int i=0;i<5;i++){
                int needX = (int) (mMinX +Math.random()*(mMaxX - mMinX +1));
                int needY = (int) (mMinY +Math.random()*(mMaxY - mMinY +1));
                Point point=points.get(i);
                if(point==null) {
                    point = new Point(needX, needY);
                }
                points.append(i,point);
            }

        }
    }

}
