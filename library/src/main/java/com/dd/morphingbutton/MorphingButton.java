package com.dd.morphingbutton;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MorphingButton extends Button {

    private Padding mPadding;
    private Params mParams;

    protected boolean mAnimationInProgress;

    private SemiCircleDrawable mDrawableNormal;
    private SemiCircleDrawable mDrawablePressed;

    public MorphingButton(Context context) {
        super(context);
        initView(context, null);
    }

    public MorphingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MorphingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mParams.height == 0 && mParams.width == 0 && w != 0 && h != 0) {
            mParams.height = getHeight();
            mParams.height = getWidth();
        }
    }

    public SemiCircleDrawable getDrawableNormal() {
        return mDrawableNormal;
    }

    public Params getParams()
    {
        return this.mParams;
    }

    public void morph(@NonNull Params params) {
        if (!mAnimationInProgress) {

            mDrawablePressed.fillColor = params.colorPressed;
            mDrawablePressed.strokeColor = params.strokeColor;
            mDrawablePressed.strokeWidth = params.strokeWidth;
            mDrawablePressed.startAngle = params.startAngle;
            mDrawablePressed.endAngle = params.endAngle;

            if (params.duration == 0) {
                morphWithoutAnimation(params);
            } else {
                morphWithAnimation(params);
            }
            mParams = params;
        }
    }

    private void morphWithAnimation(@NonNull final Params params) {
        mAnimationInProgress = true;
        setText(null);
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        setPadding(mPadding.left, mPadding.top, mPadding.right, mPadding.bottom);

        MorphingAnimation.Params animationParams = MorphingAnimation.Params.create(this)
                .color(mParams.color, params.color)
                .strokeWidth(mParams.strokeWidth, params.strokeWidth)
                .strokeColor(mParams.strokeColor, params.strokeColor)
                .height(getHeight(), params.height)
                .width(getWidth(), params.width)
                .duration(params.duration)
                .angle(mParams.startAngle, mParams.endAngle, params.startAngle, params.endAngle)
                .listener(new MorphingAnimation.Listener() {
                    @Override
                    public void onAnimationEnd() {
                        finalizeMorphing(params);
                    }
                });

        MorphingAnimation animation = new MorphingAnimation(animationParams);
        animation.start();
    }

    private void morphWithoutAnimation(@NonNull Params params) {
        mDrawableNormal.fillColor = params.color;
        mDrawableNormal.strokeColor = params.strokeColor;
        mDrawableNormal.strokeWidth = params.strokeWidth;
        mDrawableNormal.startAngle = params.startAngle;
        mDrawableNormal.endAngle = params.endAngle;

//        mDrawableNormal.setColor(params.color);
//        mDrawableNormal.setCornerRadius(params.cornerRadius);
//        mDrawableNormal.setStrokeColor(params.strokeColor);
//        mDrawableNormal.setStrokeWidth(params.strokeWidth);

        if(params.width != 0 && params.height !=0) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = params.width;
            layoutParams.height = params.height;
            setLayoutParams(layoutParams);
        }

        finalizeMorphing(params);
    }

    private void finalizeMorphing(@NonNull Params params) {
        mAnimationInProgress = false;

        if (params.icon != 0 && params.text != null) {
            setIconLeft(params.icon);
            setText(params.text);
        } else if (params.icon != 0) {
            setIcon(params.icon);
        } else if(params.text != null) {
            setText(params.text);
        }

        if (params.animationListener != null) {
            params.animationListener.onAnimationEnd();
        }
    }

    public void blockTouch() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void unblockTouch() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    protected TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }
    
    private void initView(Context context, AttributeSet attributeSet) {
        mPadding = new Padding();
        mPadding.left = getPaddingLeft();
        mPadding.right = getPaddingRight();
        mPadding.top = getPaddingTop();
        mPadding.bottom = getPaddingBottom();

        Resources resources = getResources();
        TypedArray attr = getTypedArray(context, attributeSet, R.styleable.MorphingButton);
        int color =         attr.getColor(R.styleable.MorphingButton_mb_color, resources.getColor(R.color.mb_blue));
        int pressedColor =  attr.getColor(R.styleable.MorphingButton_mb_pressedColor, resources.getColor(R.color.mb_blue_dark));
        int strokeColor =   attr.getColor(R.styleable.MorphingButton_mb_strokeColor, resources.getColor(R.color.mb_blue));
        float strokeWidth =   attr.getDimension(R.styleable.MorphingButton_mb_strokeWidth, 0);
        float startAngle =   attr.getDimension(R.styleable.MorphingButton_mb_startAngle, 0);
        float endAngle =   attr.getDimension(R.styleable.MorphingButton_mb_endAngle, 360);

        StateListDrawable background = new StateListDrawable();
        mDrawableNormal = createDrawable(color, strokeWidth, strokeColor, startAngle, endAngle);
        mDrawablePressed = createDrawable(pressedColor, strokeWidth, strokeColor, startAngle, endAngle);

        mParams = Params.create()
                .color(color)
                .colorPressed(pressedColor)
                .strokeWidth(strokeWidth)
                .strokeColor(strokeColor)
                .startAngle(startAngle)
                .endAngle(endAngle);

        background.addState(new int[]{android.R.attr.state_pressed}, mDrawablePressed);
        background.addState(StateSet.WILD_CARD, mDrawableNormal);

        setBackgroundCompat(background);
    }

    private SemiCircleDrawable createDrawable(int color, float strokeWidth, int strokeColor,
                                              float startAngle, float endAngle) {
        SemiCircleDrawable drawable = new SemiCircleDrawable(strokeColor, startAngle, endAngle);
        drawable.fillColor = color;
        drawable.strokeWidth = strokeWidth;

        return drawable;
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(@Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    public void setIcon(@DrawableRes final int icon) {
        // post is necessary, to make sure getWidth() doesn't return 0
        post(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = getResources().getDrawable(icon);
                int padding = (getWidth() / 2) - (drawable.getIntrinsicWidth() / 2);
                setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
                setPadding(padding, 0, 0, 0);
            }
        });
    }

    public void setIconLeft(@DrawableRes int icon) {
        setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
    }

    private class Padding {
        public int left;
        public int right;
        public int top;
        public int bottom;
    }

    public static class Params {
        private int width;
        private int height;
        private int color;
        private int colorPressed;
        private int duration;
        private int icon;
        private float strokeWidth;
        private int strokeColor;
        private float startAngle;
        private float endAngle;
        private String text;
        private MorphingAnimation.Listener animationListener;

        private Params() {

        }

        public static Params create() {
            return new Params();
        }

        public Params text(@NonNull String text) {
            this.text = text;
            return this;
        }

        public Params startAngle(float angle)
        {
            this.startAngle = angle;
            return this;
        }

        public Params endAngle(float angle)
        {
            this.endAngle = angle;
            return this;
        }

        public Params icon(@DrawableRes int icon) {
            this.icon = icon;
            return this;
        }

        public Params width(int width) {
            this.width = width;
            return this;
        }

        public Params height(int height) {
            this.height = height;
            return this;
        }

        public Params color(int color) {
            this.color = color;
            return this;
        }

        public Params colorPressed(int colorPressed) {
            this.colorPressed = colorPressed;
            return this;
        }

        public Params duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Params strokeWidth(float strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Params strokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        public Params animationListener(MorphingAnimation.Listener animationListener) {
            this.animationListener = animationListener;
            return this;
        }
    }
}
