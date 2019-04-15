package com.sss.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ServiceView extends View {
    Paint backgroundPaint;
    Paint borderPaint;

    public ServiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        borderPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(238, 238, 238));
        borderPaint.setColor(Color.rgb(0, 0, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = 100;

        canvas.drawRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                radius,
                radius,
                borderPaint);

        canvas.drawRoundRect(
                5,
                5,
                getWidth() - 5,
                getHeight() - 5,
                radius,
                radius,
                backgroundPaint);
    }
}
