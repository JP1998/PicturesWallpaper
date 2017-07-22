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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import de.jeanpierrehotz.severalpictureswallpaper.R;
import de.jeanpierrehotz.severalpictureswallpaper.utils.CommonUtils;

/**
 *
 */
public class ColorDialog implements SeekBar.OnSeekBarChangeListener, DialogInterface.OnCancelListener {

    private AlertDialog dialog;

    private TextView rgbIndicatorView;
    private TextView hexIndicatorView;

    private RelativeLayout /*LinearLayout*/ colorLayout;

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

        colorLayout = (RelativeLayout /*LinearLayout*/) dialog.findViewById(R.id.layout_colordialog_colorlayout);

        rgbIndicatorView = (TextView) dialog.findViewById(R.id.layout_colordialog_indicator_rgb);
        hexIndicatorView = (TextView) dialog.findViewById(R.id.layout_colordialog_indicator_hex);

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

    private void adopt() {
        mColorSaved = mColor;
        dispatchColorEvent(true);
    }

    private void discard() {
        mColor = mColorSaved;
        dispatchColorEvent(true);
        updateSeekbars();
    }

    public void setColor(int color) {
        mColor = color;
        mColorSaved = color;
        updateSeekbars();
    }

    private void dispatchColorEvent(boolean finalvalue) {
        if (listener != null) {
            listener.onColorChanged(mColor, finalvalue);
        }
    }

    private void updateSeekbars() {
        this.locked = true;

        rSeekBar.setProgress(Color.red(mColor));
        gSeekBar.setProgress(Color.green(mColor));
        bSeekBar.setProgress(Color.blue(mColor));

        updateColor();

        this.locked = false;
    }

    private void updateColor() {
        int inv = CommonUtils.ColorUtils.invert(mColor);

        colorLayout.setBackgroundColor(mColor);
        // update the color indicators
        rgbIndicatorView.setTextColor(inv);
        rgbIndicatorView.setText(CommonUtils.ColorUtils.formatToRGB(mColor));
        hexIndicatorView.setTextColor(inv);
        hexIndicatorView.setText(CommonUtils.ColorUtils.formatToHEX(mColor));
    }

    public OnColorChangedListener getOnColorChangedListener() {
        return listener;
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    public void show() {
        mColorSaved = mColor;
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
        discard();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        discard();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!locked) {
            mColor = Color.rgb(rSeekBar.getProgress(), gSeekBar.getProgress(), bSeekBar.getProgress());
            updateColor();
            dispatchColorEvent(false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color, boolean finalvalue);
    }

//    public static String formatToRGB(@ColorInt int col) {
//        return "rgb({r}, {g}, {b})"
//                .replace("{r}", "" + Color.red(col))
//                .replace("{g}", "" + Color.green(col))
//                .replace("{b}", "" + Color.blue(col));
//    }
//
//    public static String formatToHEX(@ColorInt int col) {
//        return "#" + stretch(Integer.toHexString(col & 0xFFFFFF).toUpperCase(), '0', 6);
//    }
//
//    public static String stretch(String str, char stretchWith, int minLength) {
//        String result = str;
//
//        while(result.length() < minLength) {
//            result = stretchWith + result;
//        }
//
//        return result;
//    }
//
//    public static int invert(int col) {
//        return Color.argb(
//                Color.alpha(col),
//                0xFF - Color.red(col),
//                0xFF - Color.green(col),
//                0xFF - Color.blue(col)
//        );
//    }
//
//    public static int avg(int col) {
//        return (Color.red(col) + Color.green(col) + Color.blue(col)) / 3;
//    }
}
