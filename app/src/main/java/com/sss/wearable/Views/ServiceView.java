package com.sss.wearable.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.sss.wearable.R;

public class ServiceView extends View {
    Paint backgroundPaint;
    Paint blackPaint;
    AttributeSet attrs;
    boolean state = false;

    public ServiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        blackPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(238, 238, 238));
        blackPaint.setColor(Color.rgb(0, 0, 0));
        blackPaint.setTextSize(40);
        blackPaint.setStrokeWidth(3);
        blackPaint.setTypeface(Typeface.DEFAULT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = 100;
        String text = "";

        switch (getId()) {
            case R.id.svInternet:
                text = "internet connection";
                break;
            case R.id.svBluetooth:
                text = "bluetooth";
                break;
            case R.id.svLocation:
                text = "location services";
                break;
        }

        canvas.drawRoundRect(
                0,
                0,
                getWidth(),
                getHeight(),
                radius,
                radius,
                blackPaint);

        canvas.drawRoundRect(
                3,
                3,
                getWidth() - 3,
                getHeight() - 3,
                radius,
                radius,
                backgroundPaint);

        canvas.drawText(text, 70, 110, blackPaint);

        if (state) {
            canvas.drawLine(getWidth() - 140, 110, getWidth() - 110, getHeight() - 40, blackPaint);
            canvas.drawLine(getWidth() - 110, getHeight() - 40, getWidth() - 60, 60, blackPaint);
        }
    }

    public void setState(boolean state) {
        this.state = state;
        invalidate();
    }

    public boolean getState() {
        return state;
    }
}
