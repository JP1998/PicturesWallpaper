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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public class WallpaperPictureSelector {

    private static final int CROP_PHOTO_SMALL = 2903;
    private static final String IMAGE_CROPPER_BUNDLE = "image_cropper_bundle";
    private static final int SELECT_PIC = 0x701;

    private Callback mCallback;
    private Context mContext;

    private File mSrcFile;
    private File mOutFile;
    private File mTempFile;

    private int mAspectX = -1;
    private int mAspectY = -1;

    public WallpaperPictureSelector(final Context ctx) {
        mContext = ctx;
        calculateDisplayAspect();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == SELECT_PIC) {
            Uri uri = data.getData();
            String path = Compatibility.getPath(mContext, uri);
            if (mCallback != null) {
                handleSelectedResult(path);
            }
        }

        if (requestCode == CROP_PHOTO_SMALL) {
            if (mTempFile != null && mTempFile.exists()) {
                mTempFile.delete();
            }
            File outFile = null;
            if (mOutFile != null && mOutFile.exists()) {
                outFile = mOutFile;
            } else if (data.getData() != null) {
                String outFilePath = Compatibility.getPath(mContext, data.getData());
                if (!TextUtils.isEmpty(outFilePath)) {
                    outFile = new File(outFilePath);
                }
            } else {
                outFile = CommonUtils.FileUtils.generateExternalImageCacheFile(mContext, ".jpg");
                Bitmap bitmap = data.getParcelableExtra("data");
                if (bitmap != null) {
                    CommonUtils.ImageUtils.saveBitmap(bitmap, outFile.getPath(), Bitmap.CompressFormat.JPEG, 80);
                }
            }
            if (outFile != null && outFile.exists()) {
                if (mCallback != null) {
                    mCallback.onCropperResult(CropResult.success, mSrcFile, mOutFile);
                }
            } else {
                if (mCallback != null) {
                    mCallback.onCropperResult(CropResult.illegal_output, mSrcFile, null);
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle();
        bundle.putInt("aspectX", mAspectX);
        bundle.putInt("aspectY", mAspectY);
        bundle.putSerializable("outFile", mOutFile);
        bundle.putSerializable("srcFile", mSrcFile);
        bundle.putSerializable("tempFile", mTempFile);
        outState.putBundle(IMAGE_CROPPER_BUNDLE, bundle);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGE_CROPPER_BUNDLE)) {
            Bundle bundle = savedInstanceState.getBundle(IMAGE_CROPPER_BUNDLE);
            if (bundle != null) {
                mAspectX = bundle.getInt("aspectX");
                mAspectY = bundle.getInt("aspectY");
                mOutFile = (File) bundle.getSerializable("outFile");
                mSrcFile = (File) bundle.getSerializable("srcFile");
                mTempFile = (File) bundle.getSerializable("tempFile");
            }
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void selectImage(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        activity.startActivityForResult(intent, SELECT_PIC);
    }

    private void handleSelectedResult(String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            File outputFile = CommonUtils.FileUtils.generateExternalImageCacheFile(mContext, ".jpg");
            CommonUtils.FileUtils.copy(new File(fileName), outputFile);
            if (mCallback != null) {
                mCallback.onSelectedResult(outputFile.getAbsolutePath());
            }
            cropImage(f);
        } else {
            if (mCallback != null) {
                mCallback.onSelectedResult(null);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void calculateDisplayAspect() {
        Point p = new Point();
        if (mContext instanceof Activity) {
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getRealSize(p);
            double ratio = (double) p.x / (double) p.y;

            int ctr = 1;
            while (Math.abs(Math.round(ratio * ctr) - (ratio * ctr)) > 0.001d) {
                ctr++;
            }

            mAspectY = ctr;
            mAspectX = (int) (ctr * ratio);
        } else {
            mAspectX = 9;
            mAspectY = 16;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void cropImage(File srcFile) {
        calculateDisplayAspect();
        if (!(srcFile != null && srcFile.exists())) {
            if (mCallback != null) {
                mCallback.onCropperResult(CropResult.illegal_input, srcFile, null);
            }
            return;
        }
        if (mContext == null) {
            throw new NullPointerException("'mContext' is null.");
        }

        File outFile = CommonUtils.FileUtils.generateExternalImageCacheFile(mContext, srcFile, ".jpg");
        if (outFile.exists()) {
            outFile.delete();
        }
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }

        mSrcFile = srcFile;
        mOutFile = outFile;

        Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".imagefileprovider.provider", srcFile);

        if (uri.toString().contains("%")) {
            String inputFileName = srcFile.getName();
            String ext = inputFileName.substring(inputFileName.lastIndexOf("."));
            mTempFile = CommonUtils.FileUtils.generateExternalImageCacheFile(mContext, ext);
            CommonUtils.FileUtils.copy(srcFile, mTempFile);

            uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".imagefileprovider.provider", mTempFile);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");

        intent.putExtra("aspectX", mAspectX);
        intent.putExtra("aspectY", mAspectY);

        Uri photoURI = Uri.fromFile(outFile);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent, CROP_PHOTO_SMALL);
        }
    }

    public enum CropResult {
        success,
        illegal_input,
        illegal_output
    }

    public interface Callback {
        void onSelectedResult(String file);

        void onCropperResult(CropResult result, File srcFile, File outFile);
    }


    /*
     * Dumb classes I don't want but need to keep everything in "one" class :(
     */
    private static class Compatibility {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private static String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context       The context.
         * @param uri           The Uri to query.
         * @param selection     (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        public static String getDataColumn(Context context, Uri uri, String selection,
                                           String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {
                    column
            };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        public static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }
    }

//    private static class CommonUtils {
//        private static boolean copy(File source, File dest) {
//            BufferedInputStream bis = null;
//            BufferedOutputStream bos = null;
//            boolean result = true;
//            try {
//                bis = new BufferedInputStream(new FileInputStream(source));
//                bos = new BufferedOutputStream(new FileOutputStream(dest, false));
//
//                byte[] buf = new byte[1024];
//                bis.read(buf);
//
//                do {
//                    bos.write(buf);
//                } while (bis.read(buf) != -1);
//            } catch (IOException e) {
//                result = false;
//            } finally {
//                try {
//                    if (bis != null) bis.close();
//                    if (bos != null) bos.close();
//                } catch (IOException e) {
//                    result = false;
//                }
//            }
//
//            return result;
//        }
//
//        private static File generateExternalImageCacheFile(Context context, String ext) {
//            String fileName = "img_" + System.currentTimeMillis();
//            return generateExternalImageCacheFile(context, fileName, ext);
//        }
//
//        private static File generateExternalImageCacheFile(Context context, String fileName, String ext) {
//            File cacheDir = getExternalImageCacheDir(context);
//            String path = cacheDir.getPath() + File.separator + fileName + ext;
//            return new File(path);
//        }
//
//        private static File getExternalImageCacheDir(Context context) {
//            File externalCacheDir = getExternalCacheDir(context);
//            if (externalCacheDir != null) {
//                String path = externalCacheDir.getPath() + "/image/image_selector";
//                File file = new File(path);
//                if (file.exists() && file.isFile()) {
//                    file.delete();
//                }
//                if (!file.exists()) {
//                    file.mkdirs();
//                }
//                return file;
//            }
//            final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache" + "/image";
//            return new File(cacheDir);
//        }
//
//        private static File getExternalCacheDir(Context context) {
//            File file = context.getExternalCacheDir();
//            if (file == null) {
//                final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache";
//                file = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
//            }
//            return file;
//        }
//    }
//
//    private static class ImageUtils {
//        private static void saveBitmap(Bitmap bmp, String filePath, Bitmap.CompressFormat format, int quality) {
//            FileOutputStream fo;
//            try {
//                File f = new File(filePath);
//                if (f.exists()) {
//                    f.delete();
//                }
//                f.createNewFile();
//                fo = new FileOutputStream(f, true);
//                bmp.compress(format, quality, fo);
//                fo.flush();
//                fo.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
