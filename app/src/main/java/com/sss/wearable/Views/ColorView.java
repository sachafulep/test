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
