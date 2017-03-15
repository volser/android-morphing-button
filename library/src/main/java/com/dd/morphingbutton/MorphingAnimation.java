package com.dd.morphingbutton;

import android.animation.*;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

public class MorphingAnimation {

    public interface Listener {
        void onAnimationEnd();
    }

    public static class Params {

        private int fromHeight;
        private int toHeight;

        private int fromWidth;
        private int toWidth;

        private int fromColor;
        private int toColor;

        private int duration;

        private float fromStrokeWidth;
        private float toStrokeWidth;

        private int fromStrokeColor;
        private int toStrokeColor;

        private float fromStartAngle;
        private float toStartAngle;

        private float fromEndAngle;
        private float toEndAngle;

        private MorphingButton button;
        private MorphingAnimation.Listener animationListener;

        private Params(@NonNull MorphingButton button) {
            this.button = button;
        }

        public static Params create(@NonNull MorphingButton button) {
            return new Params(button);
        }

        public Params duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Params listener(@NonNull MorphingAnimation.Listener animationListener) {
            this.animationListener = animationListener;
            return this;
        }

        public Params color(int fromColor, int toColor) {
            this.fromColor = fromColor;
            this.toColor = toColor;
            return this;
        }

        public Params height(int fromHeight, int toHeight) {
            this.fromHeight = fromHeight;
            this.toHeight = toHeight;
            return this;
        }

        public Params width(int fromWidth, int toWidth) {
            this.fromWidth = fromWidth;
            this.toWidth = toWidth;
            return this;
        }

        public Params strokeWidth(float fromStrokeWidth, float toStrokeWidth) {
            this.fromStrokeWidth = fromStrokeWidth;
            this.toStrokeWidth = toStrokeWidth;
            return this;
        }

        public Params strokeColor(int fromStrokeColor, int toStrokeColor) {
            this.fromStrokeColor = fromStrokeColor;
            this.toStrokeColor = toStrokeColor;
            return this;
        }

        public Params angle(float fromStart, float fromEnd, float toStart, float toEnd) {
            this.fromStartAngle = fromStart;
            this.fromEndAngle = fromEnd;
            this.toStartAngle = toStart;
            this.toEndAngle = toEnd;
            return this;
        }

    }

    private Params mParams;

    public MorphingAnimation(@NonNull Params params) {
        mParams = params;
    }

    public void start() {
        SemiCircleDrawable background = mParams.button.getDrawableNormal();

        ObjectAnimator startAngleAnimation =
                ObjectAnimator.ofFloat(background, "startAngle", mParams.fromStartAngle, mParams.toStartAngle);
        ObjectAnimator endAngleAnimation =
                ObjectAnimator.ofFloat(background, "endAngle", mParams.fromEndAngle, mParams.toEndAngle);

        ObjectAnimator strokeWidthAnimation =
                ObjectAnimator.ofFloat(background, "strokeWidth", mParams.fromStrokeWidth, mParams.toStrokeWidth);

        ObjectAnimator strokeColorAnimation = ObjectAnimator.ofInt(background, "strokeColor", mParams.fromStrokeColor, mParams.toStrokeColor);
        strokeColorAnimation.setEvaluator(new ArgbEvaluator());

        ObjectAnimator bgColorAnimation = ObjectAnimator.ofInt(background, "fillColor", mParams.fromColor, mParams.toColor);
        bgColorAnimation.setEvaluator(new ArgbEvaluator());

        ValueAnimator heightAnimation = ValueAnimator.ofInt(mParams.fromHeight, mParams.toHeight);
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mParams.button.getLayoutParams();
                layoutParams.height = val;
                mParams.button.setLayoutParams(layoutParams);
            }
        });

        ValueAnimator widthAnimation = ValueAnimator.ofInt(mParams.fromWidth, mParams.toWidth);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mParams.button.getLayoutParams();
                layoutParams.width = val;
                mParams.button.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mParams.duration);
        animatorSet.playTogether(strokeWidthAnimation, strokeColorAnimation, startAngleAnimation, endAngleAnimation, bgColorAnimation,
                heightAnimation, widthAnimation);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mParams.animationListener != null) {
                    mParams.animationListener.onAnimationEnd();
                }
            }
        });
        animatorSet.start();
    }

}
