package com.sss.wearable.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorView extends View {
    Paint backgroundPaint;
    Paint textPaint;
    long currentPlayTime = -1;
    int repeat = 0;
    String name = "";

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(0, 0, 0));

        textPaint = new Paint();
        textPaint.setTextSize(64);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setBackgroundPaint(int color) {
        backgroundPaint.setColor(color);
        invalidate();
    }

    public void setName(String name, int textColor) {
        this.name = name;
        textPaint.setColor(textColor);
        invalidate();
    }

    public int getPaintColor() {
        return backgroundPaint.getColor();
    }

    public void blink(int time) {
        if (time % 2 == 0) {
            backgroundPaint.setAlpha(255);
        } else {
            backgroundPaint.setAlpha(0);
        }

        invalidate();
    }

    public void pulse(int alpha) {
        backgroundPaint.setAlpha(alpha);
        invalidate();
    }

    public void rainbow(long currentPlayTime, int value) {
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

        int x = getWidth() / 2;
        int y = (int) (getHeight() / 2 - ((textPaint.descent() + textPaint.ascent()) / 2));

        canvas.drawText(name, x, y, textPaint);
    }
}
