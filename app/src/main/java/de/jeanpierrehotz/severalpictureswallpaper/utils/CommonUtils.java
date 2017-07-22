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

package de.jeanpierrehotz.severalpictureswallpaper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.ColorInt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class CommonUtils {

    public static class FileUtils {

        public static boolean copy(File source, File dest) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            boolean result = true;
            try {
                bis = new BufferedInputStream(new FileInputStream(source));
                bos = new BufferedOutputStream(new FileOutputStream(dest, false));

                byte[] buf = new byte[1024];
                bis.read(buf);

                do {
                    bos.write(buf);
                } while (bis.read(buf) != -1);
            } catch (IOException e) {
                result = false;
            } finally {
                try {
                    if (bis != null) bis.close();
                    if (bos != null) bos.close();
                } catch (IOException e) {
                    result = false;
                }
            }

            return result;
        }

        public static File generateExternalImageCacheFile(Context context, String ext) {
            String fileName = "img_" + System.currentTimeMillis();
            return generateExternalImageCacheFile(context, fileName, ext);
        }

        public static File generateExternalImageCacheFile(Context context, String fileName, String ext) {
            File cacheDir = getExternalImageCacheDir(context);
            String path = cacheDir.getPath() + File.separator + fileName + ext;
            return new File(path);
        }

        public static String getFileName(File f) {
            String name = f.getName();
            int i = name.lastIndexOf('.');

            if (i == -1) {
                return name;
            } else {
                return name.substring(0, i);
            }
        }

        public static File generateExternalImageCacheFile(Context ctx, File orig, String ext) {
            File cacheDir = getExternalCacheDir(ctx);
            File f;
            int i = 0;

            String name = FileUtils.getFileName(orig);

            while ((f = new File(cacheDir, name + i + ext)).exists()) {
                i++;
            }

            return f;
        }

        public static File getExternalImageCacheDir(Context context) {
            File externalCacheDir = getExternalCacheDir(context);
            if (externalCacheDir != null) {
                String path = externalCacheDir.getPath() + "/image/image_selector";
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file;
            }
            final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache" + "/image";
            return new File(cacheDir);
        }

        public static File getExternalCacheDir(Context context) {
            File file = context.getExternalCacheDir();
            if (file == null) {
                final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache";
                file = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
            }
            return file;
        }

    }

    public static class ImageUtils {

        public static void saveBitmap(Bitmap bmp, String filePath, Bitmap.CompressFormat format, int quality) {
            FileOutputStream fo;
            try {
                File f = new File(filePath);
                if (f.exists()) {
                    f.delete();
                }
                f.createNewFile();
                fo = new FileOutputStream(f, true);
                bmp.compress(format, quality, fo);
                fo.flush();
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static class ColorUtils {

        public static String formatToRGB(@ColorInt int col) {
            return "rgb({r}, {g}, {b})"
                    .replace("{r}", "" + Color.red(col))
                    .replace("{g}", "" + Color.green(col))
                    .replace("{b}", "" + Color.blue(col));
        }

        public static String formatToHEX(@ColorInt int col) {
            return "#" + TextUtils.stretch(Integer.toHexString(col & 0xFFFFFF).toUpperCase(), '0', 6);
        }

        public static int invert(int col) {
            return Color.argb(
                    Color.alpha(col),
                    0xFF - Color.red(col),
                    0xFF - Color.green(col),
                    0xFF - Color.blue(col)
            );
        }

        public static int avg(int col) {
            int avg = (Color.red(col) + Color.green(col) + Color.blue(col)) / 3;
            return Color.argb(
                    Color.alpha(col),
                    avg, avg, avg
            );
        }

    }

    public static class TextUtils {

        public static String stretch(String str, char stretchWith, int minLength) {
            String result = str;

            while (result.length() < minLength) {
                result = stretchWith + result;
            }

            return result;
        }

    }

}
