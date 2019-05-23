package com.sss.wearable.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class OverviewBottomView extends View {
    Paint backgroundPaint;
    Paint whitePaint;
    AttributeSet attrs;
    boolean state = false;

    public OverviewBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(79, 75, 75));

        whitePaint = new Paint();
        whitePaint.setColor(Color.rgb(238, 238, 238));
        whitePaint.setStrokeWidth(10);
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
                backgroundPaint);

        canvas.drawLine(
                getWidth() >> 1,
                30,
                getWidth() >> 1,
                getHeight() - 30,
                whitePaint);
    }
}
