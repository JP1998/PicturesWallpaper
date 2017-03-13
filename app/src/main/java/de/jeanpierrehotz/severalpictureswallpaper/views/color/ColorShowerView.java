/*
 *     Copyright 2017 Jean-Pierre Hotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.jeanpierrehotz.severalpictureswallpaper.views.color;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import de.jeanpierrehotz.severalpictureswallpaper.R;

/**
 *
 */
public class ColorShowerView extends View {

    private Paint mBorderPaint;
    private Paint mColorPaint;

    public ColorShowerView(Context context) {
        super(context);
        initialise(null);
    }

    public ColorShowerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorShowerView, 0, 0));
    }

    public ColorShowerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorShowerView, defStyleAttr, 0));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ColorShowerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialise(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorShowerView, defStyleAttr, defStyleRes));
    }

    private void initialise(TypedArray attr){
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mColorPaint = new Paint();
        mColorPaint.setStyle(Paint.Style.FILL);

        if(attr != null) {
            try {
                setColor(attr.getColor(R.styleable.ColorShowerView_cs_initialColor, 0xFF54D850));
                setBorderColor(attr.getColor(R.styleable.ColorShowerView_cs_borderColor, 0xFFA2A2A2));
                setBorderStroke(attr.getFloat(R.styleable.ColorShowerView_cs_borderWidth, 6.0f));
            } finally {
                attr.recycle();
            }
        }
    }

    public void setColor(int color){
        mColorPaint.setColor(color);
        invalidate();
    }

    public int getColor(){
        return mColorPaint.getColor();
    }

    public void setBorderColor(int color){
        mBorderPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public int getBorderColor(){
        return mBorderPaint.getColor();
    }

    public void setBorderStroke(float stroke) {
        mBorderPaint.setStrokeWidth(stroke);
        invalidate();
        requestLayout();
    }

    public float getBorderStroke(){
        return mBorderPaint.getStrokeWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(
                getWidth() / 2f,
                getHeight() / 2f,
                (getWidth() / 2f) - (mBorderPaint.getStrokeWidth() / 2f),
                mColorPaint
        );
        canvas.drawCircle(
                (getWidth() / 2f),
                (getHeight() / 2f),
                (getWidth() / 2f) - (mBorderPaint.getStrokeWidth() / 2f),
                mBorderPaint
        );
    }
}
