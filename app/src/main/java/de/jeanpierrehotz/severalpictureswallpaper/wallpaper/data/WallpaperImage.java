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

package de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Locale;

/**
 *
 */
public class WallpaperImage {

    private static final int SAMPLE_WIDTH = 450;
    private static final int SAMPLE_HEIGHT = 800;

    private final String mPath;
    private Bitmap mImage;

    private int x;
    private int y;

    public WallpaperImage(String path) {
        mPath = path;
    }

    public void loadImage(Context ctx) {
        if (mImage == null) {
            DisplayMetrics display = new DisplayMetrics();
            ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(display);

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = calculateInSampleSize(x, y, display.widthPixels, display.heightPixels);
            opt.inJustDecodeBounds = false;

            mImage = BitmapFactory.decodeFile(mPath, opt);

            if (mImage != null) {
                x = mImage.getWidth();
                y = mImage.getHeight();
            } else {
                x = 0;
                y = 0;
            }
        }
    }

    public PreviewLoaderTask loadAsPreview(ImageView v) {
        if (mImage == null) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPath, opt);

            x = opt.outWidth;
            y = opt.outHeight;

            PreviewLoaderTask task = new PreviewLoaderTask(v);
            task.execute(x, y, SAMPLE_WIDTH, SAMPLE_HEIGHT);

            return task;
        }

        return null;
    }

    public void releaseImage() {
        if (mImage != null) {
            mImage.recycle();
            mImage = null;
        }
    }

    public String getResolution() {
        if (mImage != null || (x != 0 && y != 0)) {
            return String.format(Locale.getDefault(), "%1$d x %2$dpx", x, y);
        } else {
            return "0 x 0px";
        }
    }

    public String getFileName() {
        return mPath.substring(mPath.lastIndexOf("/") + 1, mPath.lastIndexOf("."));
    }

    String getPath() {
        return this.mPath;
    }

    public boolean drawable() {
        return this.mImage != null;
    }

    public void drawImage(float x, float y, Canvas c, Paint p) {
        if (this.mImage != null) {
            c.drawBitmap(mImage, new Rect(0, 0, mImage.getWidth(), mImage.getHeight()), new RectF(0, 0, x, y), p);
        }
    }

    public int calculateInSampleSize(int currWidth, int currHeight, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (currHeight > reqHeight || currWidth > reqWidth) {

            final int halfHeight = currHeight / 2;
            final int halfWidth = currWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public class PreviewLoaderTask extends AsyncTask<Integer, Void, Bitmap> {

        private ImageView imageViewReference;

        public PreviewLoaderTask(ImageView v) {
            this.imageViewReference = v;
        }

        public void cancel() {
            this.imageViewReference = null;
        }

        /**
         * 0 -> currX
         * 1 -> currY
         * 2 -> targetX
         * 3 -> targetY
         *
         * @param integers asd
         * @return asd
         */
        @Override
        protected Bitmap doInBackground(Integer... integers) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = calculateInSampleSize(x, y, SAMPLE_WIDTH, SAMPLE_HEIGHT);
            opt.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(mPath, opt);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && imageViewReference != null) {
                mImage = bitmap;
                imageViewReference.setImageDrawable(new BitmapDrawable(Resources.getSystem(), mImage));
                imageViewReference = null;
            }
        }
    }


}
