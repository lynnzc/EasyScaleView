package com.lynn.code.easyscaleselectorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * 竖直刻度选择器
 * Created by Lynn on 9/22/16.
 */

public class VerticalScaleView extends EasyBaseScaleView {
    private int mEdge;
    private int mCornerRadius;
    private int mEdgeColor;
    private RectF mBackgroundBound;

    public VerticalScaleView(Context context) {
        super(context);
    }

    public VerticalScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    protected boolean scrollDirection() {
        return SCROLL_VERTICAL;
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
    protected void drawLineAndText(Canvas canvas, int width, int height, Paint paint) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        paint.setStrokeWidth(mEdge / 2);
        paint.setStyle(Paint.Style.FILL);

        final int valueOffset = getValueOffset();
        final int startX = mEdge;
        final int startY = 0;

        //draw line and text
        for (int i = getMin(); i <= getMax(); i++) {
            //difference value between default and i
            final int diff = getDefault() - i;
            //value i's y coordination
            float y = height / 2 + (valueOffset - diff) * getSpacing();
            //between the range of visible value
            if (y > 0 && y < height) {
                float lineWidth = width / 3;

                //interval for drawing lone line
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
                            startX + lineWidth + dp2Px(4),
                            startY + y - paint.getFontMetrics().top - getTextBound().height(),
                            paint);
                } else {
                    lineWidth /= 2;
                }

                paint.setColor(getTextDefaultColor());
                canvas.drawLine(startX, startY + y, startX + lineWidth, startY + y, paint);
            }
        }
    }

    @Override
    protected void drawHighLight(Canvas canvas, int width, int height, Paint paint) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mEdge);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getHighLightColor());

        final int startY = height / 2;
        final int startX = mEdge;
        //draw highlight line
        canvas.drawLine(startX, startY, startX + width / 3, startY, paint);
    }
}
