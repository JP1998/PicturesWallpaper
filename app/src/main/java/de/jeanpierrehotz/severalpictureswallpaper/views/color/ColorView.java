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
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.jeanpierrehotz.severalpictureswallpaper.R;

/**
 *
 */
public class ColorView extends LinearLayout implements View.OnClickListener, ColorDialog.OnColorChangedListener {

    private ColorDialog colorDialog;

    private ColorShowerView colorShower;
    private TextView captionTextView;
    private TextView descriptionTextView;

    private OnColorChangedListener listener;

    public ColorView(Context context) {
        super(context);
        initialize(null);
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorView, 0, 0));
    }

    public ColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorView, defStyleAttr, 0));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ColorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorView, defStyleAttr, defStyleRes));
    }

    private void initialize(TypedArray attr) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_colorview, this, true);

        colorShower = (ColorShowerView) findViewById(R.id.layout_colorview_colorshower);
        colorShower.setOnClickListener(this);
        captionTextView = (TextView) findViewById(R.id.layout_colorview_captiontext);
        descriptionTextView = (TextView) findViewById(R.id.layout_colorview_descriptiontext);

        if (!isInEditMode()) {
            colorDialog = createColorDialog();
            colorDialog.setOnColorChangedListener(this);
        }

        if (attr != null) {
            try {
                setColor(attr.getColor(R.styleable.ColorView_cv_initialcolor, 0xFF54D850));
                setCaption(attr.getString(R.styleable.ColorView_cv_caption));
                setDescription(attr.getString(R.styleable.ColorView_cv_description));
            } finally {
                attr.recycle();
            }
        }
    }

    private ColorDialog createColorDialog() {
        return new ColorDialog(getContext(), colorShower.getColor(), R.string.colordialog_title, R.string.colordialog_positive, R.string.colordialog_negative);
    }

    public OnColorChangedListener getOnColorChangedListener() {
        return listener;
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    public int getColor() {
        return colorShower.getColor();
    }

    public void setColor(int color) {
        colorShower.setColor(color);
        if (!isInEditMode()) {
            colorDialog.setColor(color);
        }

        invalidate();
    }

    public String getCaption() {
        return captionTextView.getText().toString();
    }

    public void setCaption(String capt) {
        captionTextView.setText(capt);
    }

    public String getDescription() {
        return descriptionTextView.getText().toString();
    }

    public void setDescription(String desc) {
        descriptionTextView.setText(desc);
    }

    @Override
    public void onClick(View v) {
        colorDialog.show();
    }

    @Override
    public void onColorChanged(int color, boolean finalvalue) {
        colorShower.setColor(color);

        if (listener != null) {
            listener.onColorChanged(color, finalvalue);
        }
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color, boolean finalvalue);
    }
}
