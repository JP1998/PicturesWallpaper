/*
 *      Copyright 2016 Jean-Pierre Hotz
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.jeanpierrehotz.severalpictureswallpaper.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import de.jeanpierrehotz.severalpictureswallpaper.R;

public class DividerView extends View{

    private Paint p;

    public DividerView(Context context){
        super(context);
        this.p = new Paint();
        p.setColor(ContextCompat.getColor(getContext(), R.color.dividercolor));
        p.setStyle(Paint.Style.FILL);
        this.postInvalidate();
    }

    public DividerView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.p = new Paint();
        p.setColor(ContextCompat.getColor(getContext(), R.color.dividercolor));
        p.setStyle(Paint.Style.FILL);
        this.postInvalidate();
    }

    public DividerView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        this.p = new Paint();
        p.setColor(ContextCompat.getColor(getContext(), R.color.dividercolor));
        p.setStyle(Paint.Style.FILL);
        this.postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
    }
}
