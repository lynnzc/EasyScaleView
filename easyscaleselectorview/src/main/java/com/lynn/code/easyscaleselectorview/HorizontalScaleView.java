package com.lynn.code.easyscaleselectorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * 水平刻度选择器
 * Created by Lynn on 9/23/16.
 */

public class HorizontalScaleView extends EasyBaseScaleView {
    private int mEdge;
    private int mCornerRadius;
    private int mEdgeColor;
    private RectF mBackgroundBound;

    public HorizontalScaleView(Context context) {
        super(context);
    }

    public HorizontalScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitial() {
        mEdge = dp2Px(1);
        mCornerRadius = dp2Px(5);
        mEdgeColor = Color.parseColor("#000000");
        mBackgroundBound = new RectF();
    }

    @Override
    protected void drawBackground(Canvas canvas, int width, int height, Paint paint) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mEdge);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getBackgroundColor());

        mBackgroundBound.top = 0;
        mBackgroundBound.left = 0;
        mBackgroundBound.bottom = height;
        mBackgroundBound.right = width;

        canvas.drawRoundRect(mBackgroundBound, mCornerRadius, mCornerRadius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mEdgeColor);

        canvas.drawRoundRect(mBackgroundBound, mCornerRadius, mCornerRadius, paint);
    }

    @Override
    protected void drawHighLight(Canvas canvas, int width, int height, Paint paint) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mEdge);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getHighLightColor());

        final int startX = width / 2;
        final int startY = mEdge;

        canvas.save();
        //draw highlight
        canvas.drawLine(startX, startY, startX, startY + height / 3, paint);
        canvas.restore();
    }

    @Override
    protected void drawLineAndText(Canvas canvas, int width, int height, Paint paint) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(getTextDefaultColor());
        paint.setTextSize(getTextSize());
        paint.setStrokeWidth(mEdge / 2);
        paint.setStyle(Paint.Style.FILL);

        final int valueOffset = getValueOffset();
        final int startX = 0;
        final int startY = mEdge;

        //draw line and text
        for (int i = getMin(); i <= getMax(); i++) {
            //difference value between default and i
            final int diff = getDefault() - i;
            //value i's x coordination
            float x = width / 2 + (valueOffset - diff) * getSpacing();
            //between the range of visible value
            if (x > 0 && x < width) {
                float lineHeight = height / 3;

                //interval for drawing long line
                if (i % getLongScaleCycle() == 0) {
                    if (Math.abs(getDefault() - valueOffset - i) < 1) {
                        //highlight the selected value
                        paint.setColor(getTextSelectedColor());
                    } else {
                        paint.setColor(getTextDefaultColor());
                    }
                    final String text = getValue(i);
                    paint.getTextBounds(text, 0, text.length(), getTextBound());
                    //draw text
                    canvas.drawText(text,
                            startX + x - getTextBound().width() / 2,
                            startY + lineHeight + dp2Px(4) - paint.getFontMetrics().top,
                            paint);
                } else {
                    lineHeight /= 2;
                }

                paint.setColor(getTextDefaultColor());
                canvas.drawLine(startX + x, startY, startX + x, startY + lineHeight, paint);
            }
        }
    }

    @Override
    protected boolean scrollDirection() {
        return SCROLL_HORIZONTAL;
    }
}
