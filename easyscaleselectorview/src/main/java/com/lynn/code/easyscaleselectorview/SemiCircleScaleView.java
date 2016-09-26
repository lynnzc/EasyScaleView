package com.lynn.code.easyscaleselectorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * 半圆刻度选择器
 * Created by Lynn on 9/22/16.
 */

public class SemiCircleScaleView extends EasyBaseScaleView {
    private float mRadius;
    //半圆范围
    private RectF mBackgroundBound;
    private int mEdge;
    //angle of rotation between two scale
    private int mScaleDegree;

    public SemiCircleScaleView(Context context) {
        super(context);
    }

    public SemiCircleScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SemiCircleScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitial() {
        mBackgroundBound = new RectF();
        mEdge = dp2Px(1);
        mScaleDegree = 6;
    }

    @Override
    protected boolean scrollDirection() {
        return SCROLL_HORIZONTAL;
    }

    /**
     * 绘制半圆
     */
    @Override
    protected void drawBackground(Canvas canvas, int width, int height, Paint paint) {
        mRadius = Math.min(width / 2, height);

        paint.reset();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mEdge);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getBackgroundColor());

        mBackgroundBound.left = -mRadius;
        mBackgroundBound.top = -mRadius;
        mBackgroundBound.right = mRadius;
        mBackgroundBound.bottom = mRadius;

        canvas.save();
        canvas.translate(width / 2, height);
        canvas.drawArc(mBackgroundBound, 180, 180, false, paint);
        canvas.restore();
    }

    @Override
    protected void drawLineAndText(Canvas canvas, int width, int height, Paint paint) {
        canvas.save();
        canvas.translate(width / 2, height);

        paint.setColor(getTextDefaultColor());
        paint.setTextSize(getTextSize());
        paint.setStrokeWidth(mEdge / 2);
        paint.setStyle(Paint.Style.FILL);

        final int valueOffset = getValueOffset();
        final float startX = 0;
        final float startY = -mRadius + mEdge;

        //绘制刻度
        for (int i = getMin(); i <= getMax(); i++) {
            //compute the diff between default and i
            final int space = getDefault() - i;
            final int index = valueOffset - space;
            //check if it is visible on the screen
            if (Math.abs(index) < 15) {
                canvas.save();
                float lineWidth = mRadius / 3;
                //rotate base on the default value
                canvas.rotate(index * mScaleDegree);

                if (i % getLongScaleCycle() == 0) {
                    //画长刻度线 默认每5格一个
                    if (Math.abs(space - valueOffset) < 1) {
                        //当前选中文字显示高亮
                        paint.setColor(getTextSelectedColor());
                    }
                    final String text = getValue(i);

                    canvas.save();
                    paint.getTextBounds(text, 0, text.length(), getTextBound());
                    //startY + lineWidth + offset between text and line
                    canvas.translate(startX, startY + lineWidth + dp2Px(4));
                    canvas.drawText(text,
                            -getTextBound().width() / 2,
                            -paint.getFontMetrics().top - getTextBound().height() / 2,
                            paint);
                    canvas.restore();
                } else {
                    lineWidth /= 3;
                }

                paint.setColor(getTextDefaultColor());
                //画刻度线
                canvas.drawLine(startX, startY, startX, startY + lineWidth, paint);
                canvas.restore();
            }
        }
        canvas.restore();
    }

    @Override
    protected void drawHighLight(Canvas canvas, int width, int height, Paint paint) {
        paint.setStrokeWidth(mEdge);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getHighLightColor());

        final float lineHeight = mRadius / 3;
        final float startX = 0;
        final float startY = -mRadius + mEdge;

        //draw highlight line
        canvas.save();
        canvas.translate(width / 2, height);
        canvas.drawLine(startX, startY, startX, startY + lineHeight, paint);
        canvas.restore();
    }

    public void setRotationAngle(int degree) {
        mScaleDegree = degree;
        postInvalidate();
    }
}
