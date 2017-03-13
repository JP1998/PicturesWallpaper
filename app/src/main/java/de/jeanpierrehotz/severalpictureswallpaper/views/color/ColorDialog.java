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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import de.jeanpierrehotz.severalpictureswallpaper.R;

/**
 *
 */
public class ColorDialog implements SeekBar.OnSeekBarChangeListener, DialogInterface.OnCancelListener{

    private AlertDialog dialog;

    private LinearLayout colorLayout;

    private SeekBar rSeekBar;
    private SeekBar gSeekBar;
    private SeekBar bSeekBar;

    private boolean locked;

    private int mColor;
    private int mColorSaved;

    private OnColorChangedListener listener;

    public ColorDialog(Context ctx, int color, int title, int positive, int negative) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new AlertDialog.Builder(ctx)
                    .setView(R.layout.layout_colordialog)
                    .setTitle(title)
                    .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adopt();
                        }
                    })
                    .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            discard();
                        }
                    })
                    .setOnCancelListener(this)
                    .create();

        } else {
            View v = LayoutInflater.from(ctx)
                    .inflate(R.layout.layout_colordialog, null, false);

            dialog = new AlertDialog.Builder(ctx)
                    .setView(v)
                    .setTitle(title)
                    .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adopt();
                        }
                    })
                    .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            discard();
                        }
                    })
                    .setOnCancelListener(this)
                    .create();
        }

        dialog.show();
        dialog.cancel();

        colorLayout = (LinearLayout) dialog.findViewById(R.id.layout_colordialog_colorlayout);

        rSeekBar = (SeekBar) dialog.findViewById(R.id.layout_colordialog_redseekbar);
        rSeekBar.setOnSeekBarChangeListener(this);

        gSeekBar = (SeekBar) dialog.findViewById(R.id.layout_colordialog_greenseekbar);
        gSeekBar.setOnSeekBarChangeListener(this);

        bSeekBar = (SeekBar) dialog.findViewById(R.id.layout_colordialog_blueseekbar);
        bSeekBar.setOnSeekBarChangeListener(this);

        this.mColor = color;
        this.mColorSaved = color;

        this.locked = false;

        updateSeekbars();
    }

    private void adopt(){
        mColorSaved = mColor;
        dispatchColorEvent(true);
    }

    private void discard(){
        mColor = mColorSaved;
        dispatchColorEvent(true);
        updateSeekbars();
    }

    public void setColor(int color) {
        mColor = color;
        mColorSaved = color;
        updateSeekbars();
    }

    private void dispatchColorEvent(boolean finalvalue){
        if(listener != null) {
            listener.onColorChanged(mColor, finalvalue);
        }
    }

    private void updateSeekbars(){
        this.locked = true;

        rSeekBar.setProgress(Color.red(mColor));
        gSeekBar.setProgress(Color.green(mColor));
        bSeekBar.setProgress(Color.blue(mColor));

        updateColor();

        this.locked = false;
    }

    private void updateColor(){
        colorLayout.setBackgroundColor(mColor);
    }

    public OnColorChangedListener getOnColorChangedListener() {
        return listener;
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    public void show(){
        mColorSaved = mColor;
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
        discard();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        discard();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(!locked) {
            mColor = Color.rgb(rSeekBar.getProgress(), gSeekBar.getProgress(), bSeekBar.getProgress());
            updateColor();
            dispatchColorEvent(false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    public interface OnColorChangedListener {
        void onColorChanged(int color, boolean finalvalue);
    }
}
