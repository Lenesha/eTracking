/*
 * Copyright (c) 2015. Configure.IT, All Rights Reserved.
 */

package com.airtec.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by Hitesh
 */
public class TextProgressBar extends ProgressBar {

    private String text = "";

    private Paint textPaint;

    private final int TEXTSIZE = 22;

    public TextProgressBar(Context context) {
        super(context);
        init();
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        textPaint = new Paint();
        textPaint.setTextSize(TEXTSIZE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // First draw the regular progress bar, then custom draw our text
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(text, x, y, textPaint);
    }

    public synchronized void setText(String text) {
        this.text = text;
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        drawableStateChanged();
    }
}
