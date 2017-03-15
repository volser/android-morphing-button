package com.dd.morphingbutton;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by pauliusvindzigelskis on 3/15/17.
 */

public class SemiCircleDrawable extends Drawable {

    private RectF rectF;
    public float startAngle;
    public float endAngle;

    public int fillColor;
    public int strokeColor;
    public float strokeWidth;

    public SemiCircleDrawable() {
        this(Color.blue(1), 0, 360);
    }

    public SemiCircleDrawable(int color, float startAngle, float endAngle) {
        this.strokeColor = color;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        rectF = new RectF();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        Rect bounds = getBounds();

//        if(angle == Direction.LEFT || angle == Direction.RIGHT)
//        {
//            canvas.scale(2, 1);
//            if(angle == Direction.RIGHT)
//            {
//                canvas.translate(-(bounds.right / 2), 0);
//            }
//        }
//        else
//        {
//            canvas.scale(1, 2);
//            if(angle == Direction.BOTTOM)
//            {
//                canvas.translate(0, -(bounds.bottom / 2));
//            }
//        }

        int alpha = (int) this.strokeWidth;
        Rect circleBounds = new Rect(bounds.left + alpha, bounds.top + alpha,
                bounds.right - alpha, bounds.bottom - alpha);
        rectF.set(circleBounds);

        Paint strokePaint = new Paint();
        strokePaint.setColor(this.strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(this.strokeWidth);
        canvas.drawArc(rectF, this.startAngle, this.endAngle, false, strokePaint);

            Paint fillPaint = new Paint();
            fillPaint.setColor(this.fillColor);
            fillPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() / 2, fillPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        // Has no effect
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Has no effect
    }

    @Override
    public int getOpacity() {
        // Not Implemented
        return PixelFormat.TRANSPARENT;
    }

}