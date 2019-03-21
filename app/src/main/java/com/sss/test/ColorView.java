package com.sss.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorView extends View {
    Paint backgroundPaint;
    int radius;
    long currentPlayTime = -1;
    int repeat = 0;

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(0, 0, 0));
    }

    void setBackgroundPaint(int red, int green, int blue) {
        backgroundPaint.setColor(Color.rgb(red, green, blue));
        invalidate();
    }

    void blink(int time) {
        if (time % 2 == 0) {
            backgroundPaint.setAlpha(255);
        } else {
            backgroundPaint.setAlpha(0);
        }

        invalidate();
    }

    void pulse(int alpha) {
        backgroundPaint.setAlpha(alpha);
        invalidate();
    }

    void rainbow(long currentPlayTime, int value) {
        if (this.currentPlayTime > currentPlayTime) {
            repeat++;
        }

        switch (repeat) {
            case 0:
                backgroundPaint.setARGB(255, value, 100, 100);
                break;

            case 1:
                backgroundPaint.setARGB(255, 175, value, 100);
                break;

            case 2:
                backgroundPaint.setARGB(255, 175, 175, value);
                break;
        }

        this.currentPlayTime = currentPlayTime;
        if (repeat > 2) {
            repeat = 0;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = 20;

        canvas.drawRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                radius,
                radius,
                backgroundPaint);
    }
}
