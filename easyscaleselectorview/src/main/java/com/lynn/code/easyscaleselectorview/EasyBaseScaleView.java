package com.lynn.code.easyscaleselectorview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * 刻度选择器的基类
 * Created by Lynn on 9/22/16.
 */

public abstract class EasyBaseScaleView extends View {
    public static final boolean SCROLL_VERTICAL = true;
    public static final boolean SCROLL_HORIZONTAL = false;
    public static final int DEFAULT_WIDTH = 100;
    public static final int DEFAULT_HEIGHT = 500;

    private Paint mPaint;

    private float mPre;
    private float mTotalOffset;

    private float mSpacing;
    private int mLongScaleCycle;

    private int mMaxValue;
    private int mMinValue;
    //default value
    private int mDefaultValue;

    private Rect mTextBound;
    private int mTextSize;
    private int mTextDefaultColor;
    private int mTextSelectedColor;

    private int mBackgroundColor;
    private int mHighLightColor;

    //控制滑动
    private int mMinVelocity;
    private OverScroller mOverScroller;
    private VelocityTracker mVelocityTracker;
    private CustomScrollerAnimation mCustomScrollerAnimation;

    //TODO default start displaying values from left to right / from top to bottom
    private boolean mIsReverse;

    //range
    private List<String> mValues;
    private OnValueSelectedCallback mOnValueSelectedCallback = null;

    public EasyBaseScaleView(Context context) {
        this(context, null);
    }

    public EasyBaseScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyBaseScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EasyBaseScaleView, defStyleAttr, 0);

            //default long scale cycle
            mLongScaleCycle = typedArray.getInt(R.styleable.EasyBaseScaleView_longScaleCycle, 5);
            //initial textsize, text color, selected text color
            mTextSize = typedArray.getDimensionPixelSize(R.styleable.EasyBaseScaleView_scaleTextSize, getFontSize(13));
            mTextDefaultColor = typedArray.getResourceId(R.styleable.EasyBaseScaleView_scaleTextDefaultColor, Color.parseColor("#d2d2d2"));
            mTextSelectedColor = typedArray.getResourceId(R.styleable.EasyBaseScaleView_scaleTextSelectedColor, Color.parseColor("#000000"));

            mBackgroundColor = typedArray.getResourceId(R.styleable.EasyBaseScaleView_scaleBackgroundColor, Color.parseColor("#3F51B5"));
            mHighLightColor = typedArray.getResourceId(R.styleable.EasyBaseScaleView_scaleHighlightColor, Color.parseColor("#000000"));
            //default spacing between scales
            mSpacing = typedArray.getDimensionPixelOffset(R.styleable.EasyBaseScaleView_spacing, dp2Px(6));
            //TODO
            mIsReverse = false;

            typedArray.recycle();
        }

        init();
    }


    private void init() {
        mOverScroller = new OverScroller(getContext());

        //the minimum velocity to trigger fling event
        mMinVelocity = ViewConfiguration.get(getContext())
                .getScaledMinimumFlingVelocity();

        //initial min, max, default value
        mMinValue = 0;
        mMaxValue = 200;
        mDefaultValue = mMinValue + (mMaxValue - mMinValue) >> 1;

        mTextBound = new Rect();
        mValues = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPre = 0f;
        mTotalOffset = 0f;

        onInitial();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec, scrollDirection() ? DEFAULT_WIDTH : DEFAULT_HEIGHT),
                measureSize(heightMeasureSpec, scrollDirection() ? DEFAULT_HEIGHT : DEFAULT_WIDTH));
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureSize(int spec, int defaultSize) {
        final int specMode = MeasureSpec.getMode(spec);
        final int specSize = MeasureSpec.getSize(spec);
        int size = defaultSize;

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                size = Math.max(specSize, size);
                break;
            case MeasureSpec.EXACTLY:
                size = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                break;
        }
        return size;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        final int w = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        final int h = canvas.getHeight() - getPaddingTop() - getPaddingBottom();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        drawBackground(canvas, w, h, mPaint);

        drawLineAndText(canvas, w, h, mPaint);

        drawHighLight(canvas, w, h, mPaint);
    }

    /**
     * extra initialization
     * 进行必要的初始化
     */
    protected abstract void onInitial();

    /**
     * draw background
     * 绘制自定义背景
     */
    protected abstract void drawBackground(Canvas canvas, int width, int height, Paint paint);

    /**
     * draw the highlight
     * 绘制中间高亮刻度
     */
    protected abstract void drawHighLight(Canvas canvas, int width, int height, Paint paint);


    /**
     * draw main content
     * 绘制具体的刻度和文字
     */
    protected abstract void drawLineAndText(Canvas canvas, int width, int height, Paint paint);

    /**
     * scroll direction: horizontal / vertical
     * 滚动方向: 水平 / 竖直
     */
    protected abstract boolean scrollDirection();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float cur;
        if (scrollDirection()) {
            cur = event.getY();
        } else {
            cur = event.getX();
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mCustomScrollerAnimation != null) {
                    clearAnimation();
                }
//                mOverScroller.forceFinished(true);
                if (!mOverScroller.isFinished()) {
                    mOverScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //compute gesture offset
                final float offset = cur - mPre;
                //update the total offset
                mTotalOffset += offset;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //compute current velocity base on the scroll direction
                mVelocityTracker.computeCurrentVelocity(800);
                final float velocity;
                if (scrollDirection()) {
                    //vertical velocity
                    velocity = mVelocityTracker.getYVelocity();
                    //if can fling
                    if (Math.abs(velocity) > mMinVelocity) {
                        //make fling
                        mOverScroller.fling(0, 0, 0, (int) velocity,
                                -(int) (getMax() * getSpacing() + 1), (int) (getMax() * getSpacing() + 1), 0, 0);
                    }
                } else {
                    //get the horizontal velocity
                    velocity = mVelocityTracker.getXVelocity();
                    //if can fling
                    if (Math.abs(velocity) > mMinVelocity) {
                        //make fling
                        mOverScroller.fling(0, 0, (int) velocity, 0, 0, 0,
                                -(int) (getMax() * getSpacing() + 1), (int) (getMax() * getSpacing() + 1));
                    }
                }
                startScrollAnimation();

                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                return false;
            default:
                break;
        }
        mPre = cur;
        return true;
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            final float offset;
            if (scrollDirection()) {
                //vertical offset, base on y axis
                offset = (mOverScroller.getFinalY() - mOverScroller.getCurrY());
            } else {
                //horizontal offset, base on x axis
                offset = (mOverScroller.getFinalX() - mOverScroller.getCurrX());
            }
            //update total offset
            mTotalOffset += offset * offsetFactor();
            if (mTotalOffset < (mDefaultValue - mMinValue) * mSpacing &&
                    mTotalOffset > (mDefaultValue - mMaxValue) * mSpacing) {
                //limit the offset range
                //在视图范围内允许触发惯性位移
                startScrollAnimation();
            }
        }
        super.computeScroll();
    }

    /**
     * 滑动的位移系数
     *
     * @return
     */
    protected float offsetFactor() {
        return 0.3f;
    }

    /**
     * custom scroll animation
     * 自定义滚动动画
     */
    private class CustomScrollerAnimation extends Animation {
        private float from = 0f;
        private float to = 0f;

        CustomScrollerAnimation(float f, float d) {
            from = f;
            to = d;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //smooth scroll to offset
            mTotalOffset = from + (to - from) * interpolatedTime;
            //refresh
            invalidate();
        }
    }

    private void startScrollAnimation() {
        //check scroll range
        if (mTotalOffset < (getDefault() - getMax()) * getSpacing() ||
                mTotalOffset > (getDefault() - getMin()) * getSpacing()) {
            //out of scroll bound
            if (mTotalOffset > 0) {
                //lower bound
                scrollToOffset(mDefaultValue - mMinValue, 250);
            } else {
                //upper bound
                scrollToOffset(mDefaultValue - mMaxValue, 250);
            }

        } else {
//            int valueOffset = (int) (Math.rint(mTotalOffset / getSpacing()));
            //between the scroll range
            scrollToOffset(getValueOffset(), 200);
        }
    }

    private void scrollToOffset(int valueOffset, int duration) {
        if (mCustomScrollerAnimation != null) {
            clearAnimation();
        }

        if (mCustomScrollerAnimation == null) {
            mCustomScrollerAnimation = new CustomScrollerAnimation(mTotalOffset, (valueOffset * mSpacing));
        } else {
            mCustomScrollerAnimation.from = mTotalOffset;
            mCustomScrollerAnimation.to = (valueOffset * mSpacing);
        }

        mCustomScrollerAnimation.setDuration(duration);
        mCustomScrollerAnimation.setInterpolator(new DecelerateInterpolator());
        startAnimation(mCustomScrollerAnimation);

        if (mOnValueSelectedCallback != null) {
            //callback
            int index = mDefaultValue - valueOffset;
//            Log.d("value: ", (mDefaultValue - valueOffset) + "");
            if (index < getMin()) {
                index = getMin();
            } else if (index > getMax()) {
                index = getMax();
            }
            mOnValueSelectedCallback.onValueSelected(mValues.get(index));
        }
    }

    /**
     * 初始化刻度选择的值
     */
    public void initValues(List<String> values, OnValueSelectedCallback onValueSelectedCallback, int defaultValue) {
        mValues.clear();
        mValues.addAll(values);

        mOnValueSelectedCallback = onValueSelectedCallback;

        mMaxValue = mValues.size() - 1;
        mMinValue = 0;
        mDefaultValue = defaultValue;
        //refresh
        postInvalidate();
    }

    /**
     * 初始化刻度选择的值, 重载方法
     */
    public void initValues(List<String> values, OnValueSelectedCallback onValueSelectedCallback) {
        initValues(values, onValueSelectedCallback, mMinValue + (mMaxValue - mMinValue) >> 1);
    }

    /**
     * change the default value
     * 改变默认显示
     */
    public void setDefaultValue(int defaultValue) {
        if (defaultValue < mMinValue) {
            defaultValue = mMinValue;
        } else if (defaultValue > mMaxValue - 1) {
            defaultValue = mMaxValue - 1;
        }

        this.mDefaultValue = defaultValue;
        //refresh
        postInvalidate();
    }

    public interface OnValueSelectedCallback {
        void onValueSelected(String value);
    }

    @SuppressWarnings("deprecation")
    protected int getColor(@ColorRes int color) {
        return getContext().getResources().getColor(color);
    }

    protected int getFontSize(int sp) {
        return (int) getContext().getResources().getDisplayMetrics().scaledDensity * sp;
    }

    protected int dp2Px(int dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    protected int getTextDefaultColor() {
        return mTextDefaultColor;
    }

    protected int getTextSelectedColor() {
        return mTextSelectedColor;
    }

    protected int getTextSize() {
        return mTextSize;
    }

    protected int getBackgroundColor() {
        return mBackgroundColor;
    }

    protected int getHighLightColor() {
        return mHighLightColor;
    }

    protected int getMin() {
        return mMinValue;
    }

    protected int getMax() {
        return mMaxValue;
    }

    protected int getDefault() {
        return mDefaultValue;
    }

    protected float getSpacing() {
        return mSpacing;
    }

    protected int getLongScaleCycle() {
        return mLongScaleCycle;
    }

    protected String getValue(int i) {
        return mValues.get(i);
    }

    protected Rect getTextBound() {
        return mTextBound;
    }

    protected int getValueOffset() {
        return (int) (Math.rint(mTotalOffset / getSpacing()));
    }
}
