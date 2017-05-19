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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import de.jeanpierrehotz.severalpictureswallpaper.R;

/**
 *
 */
public class WallpaperDataManager {

    private static final String WALLPAPERSETTINGS_PREFERENCES_CODE = "This is the preference for all the wallpaper settings";
    private static final String WALLPAPERSETTINGS_IMAGES_PREFERENCES_CODE_AT = "This is the preference for all the images of the wallpaper setting at index ";
    private static final String WALLPAPERSETTINGS_SELECTEDSETTING_PREFERENCES_CODE = "This is the preference for the index of the selected setting";
    private static final String WALLPAPERSETTINGS_NUMBEROFSETTINGS = "This is the code for the amount of settings stored in this preference";
    private static final String WALLPAPERSETTINGS_CAPTION_AT = "This is the code for the caption of the setting at index ";
    private static final String WALLPAPERSETTINGS_DETECTGESTURES_AT = "This is the code for the detectgestures field for the setting at index ";
    private static final String WALLPAPERSETTINGS_LOCKWALLPAPER_AT = "This is the code for the lockwallpaper field for the setting at index ";
    private static final String WALLPAPERSETTINGS_SHOWWALLPAPERTIME_AT = "This is the code for the showWallpaperTime field for the setting at index ";
    private static final String WALLPAPERSETTINGS_FALLBACKCOLOR_AT = "This is the code for the fallbackcolor field for the setting at index ";
    private static final String WALLPAPERSETTINGS_CURRENTIMAGE_AT = "This is the code for the currentimage field for the setting at index ";
    private static final String WALLPAPERSETTINGS_IMAGES_LENGTH = "This is the code for the amount of images";
    private static final String WALLPAPERSETTINGS_IMAGES_PATH_AT = "This is the code for the image at index ";
    private static final String WALLPAPERSETTINGS_SELECTEDSETTING_SELECTEDSETTING = "This is the code for the value of the selected setting";


    /* TODO: Remove since old architecture has become obsolete */
    private static final String IMAGES_PATH_AT = "This is the image at index ";
    private static final String IMAGES_TOTAL_NUMBER = "This is the length of the pictures.";

    @Deprecated
    public static void saveToSharedPreferences(List<WallpaperImage> imgs, SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit().clear().putInt(IMAGES_TOTAL_NUMBER, imgs.size());

        for (int i = 0; i < imgs.size(); i++) {
            edit.putString(IMAGES_PATH_AT + i, imgs.get(i).getPath());
        }

        edit.apply();
    }

    @Deprecated
    public static List<WallpaperImage> loadFromSharedPreferences(SharedPreferences prefs) {
        int length = prefs.getInt(IMAGES_TOTAL_NUMBER, 0);
        ArrayList<WallpaperImage> images = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            images.add(new WallpaperImage(prefs.getString(IMAGES_PATH_AT + i, "")));
        }

        return images;
    }

    /**
     * @param ctx The Context to transcribe all the values with
     * @return {@code true} if (and only if) transcribing values was needed and also successfully happened;
     * {@code false} otherwise
     */
    @Deprecated
    public static boolean transcribeFromOldArchitecture(Context ctx) {
        SharedPreferences settingsPrefs = ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpapersinfo), Context.MODE_PRIVATE);
        int length = settingsPrefs.getInt(ctx.getString(R.string.prefs_wallpapercount), -1);

        /*
         * Little explanation at this point:
         *
         * I didn't think of the architecture of this app to begin with, which has made this code (as it has progressed
         * and gotten features added to it) extremely messy. This method is a one-time use method which is supposed
         * to transcribe the saved values for the wallpaper settings (stored previously within 2n+1 preferences; now within n+1)
         * into the new architecture; The new architecture allows the easy maintenance of all the values while still kept together fairly well.
         *
         * With the beginning of the usage of the new architecture the use of the methods WallpaperDataManager#saveToSharedPreferences(List, SharedPreferences)
         * and WallpaperDataManager#loadFromSharedPreferences(SharedPreferences) will be deprecated.
         *
         * This method itself will also be deprecated since the use of it should be discontinued as soon as possible.
         */

        if (length != -1) {
            List<WallpaperSettings> settingsList = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                String caption = settingsPrefs.getString(ctx.getString(R.string.prefs_wallpapername) + i, "");
                List<WallpaperImage> images = WallpaperDataManager.loadFromSharedPreferences(
                        ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpaperimages) + i, Context.MODE_PRIVATE)
                );

                SharedPreferences currentMiscPrefs = ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_miscellanous) + i, Context.MODE_PRIVATE);

                int waittime = currentMiscPrefs.getInt(ctx.getString(R.string.prefs_showPictureTime), WallpaperSettings.DEFAULT_SHOWPICTURETIME);
                boolean detectgestures = currentMiscPrefs.getBoolean(ctx.getString(R.string.prefs_detectGestures), WallpaperSettings.DEFAULT_DETECTGESTURES);
                boolean lockwallpaper = currentMiscPrefs.getBoolean(ctx.getString(R.string.prefs_lockwallpaper), WallpaperSettings.DEFAULT_LOCKWALLPAPER);
                int fallbackcolor = currentMiscPrefs.getInt(ctx.getString(R.string.prefs_fallbackcolor), WallpaperSettings.DEFAULT_FALLBACKCOLOR);
                int currentImage = currentMiscPrefs.getInt(ctx.getString(R.string.prefs_currentIndex), 0);

                settingsList.add(new WallpaperSettings(
                        caption,
                        detectgestures,
                        lockwallpaper,
                        waittime,
                        fallbackcolor,
                        currentImage,
                        images
                ));
            }

            saveAllSettings(ctx, settingsList);

            settingsPrefs.edit().clear().apply();
            for (int i = 0; i < length; i++) {
                ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpaperimages) + i, Context.MODE_PRIVATE).edit().clear().apply();
                ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_miscellanous) + i, Context.MODE_PRIVATE).edit().clear().apply();
            }

            return true;
        }

        return false;
    }
    /* TODO: Remove since old architecture has become obsolete */

    public static List<String> getUsedImageFiles(Context ctx) {
        /* TO/DO: Change to new architecture */
//        List<String> files = new ArrayList<>();
//
//        SharedPreferences settingsPrefs = ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpapersinfo), Context.MODE_PRIVATE);
//        int length = settingsPrefs.getInt(ctx.getString(R.string.prefs_wallpapercount), 0);
//
//        for (int i = 0; i < length; i++) {
//            SharedPreferences wallpaperpreferences = ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpaperimages) + i, Context.MODE_PRIVATE);
//            int wallpaperamount = wallpaperpreferences.getInt(IMAGES_TOTAL_NUMBER, 0);
//
//            for (int j = 0; j < wallpaperamount; j++) {
//                files.add(wallpaperpreferences.getString(IMAGES_PATH_AT + j, ""));
//            }
//        }
//
//        return files;

        List<String> files = new ArrayList<>();

        SharedPreferences settingsPreferences = ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE);
        int length = settingsPreferences.getInt(WALLPAPERSETTINGS_NUMBEROFSETTINGS, 0);

        for (int i = 0; i < length; i++) {
            SharedPreferences imagesPreferences = ctx.getSharedPreferences(WALLPAPERSETTINGS_IMAGES_PREFERENCES_CODE_AT + i, Context.MODE_PRIVATE);
            int imgLength = imagesPreferences.getInt(WALLPAPERSETTINGS_IMAGES_LENGTH, 0);

            for (int j = 0; j < imgLength; j++) {
                files.add(imagesPreferences.getString(WALLPAPERSETTINGS_IMAGES_PATH_AT + j, ""));
            }
        }

        return files;
    }

    public static int loadSelectedSettingsIndex(Context ctx, int def) {
        return ctx.getSharedPreferences(WALLPAPERSETTINGS_SELECTEDSETTING_PREFERENCES_CODE, Context.MODE_PRIVATE).getInt(WALLPAPERSETTINGS_SELECTEDSETTING_SELECTEDSETTING, def);
    }

    public static void saveSelectedSettingsIndex(Context ctx, int i) {
        ctx.getSharedPreferences(WALLPAPERSETTINGS_SELECTEDSETTING_PREFERENCES_CODE, Context.MODE_PRIVATE).edit()
                .putInt(WALLPAPERSETTINGS_SELECTEDSETTING_SELECTEDSETTING, i)
                .apply();
    }

    /**
     * This method looks up the next index of a unlocked wallpaper. If there is no wallpaper unlocked
     * (except the wallpaper whose index is given) this method will return the given index regardless
     * of whether or not it is unlocked.
     *
     * @param ctx   the Context used for loading the values from preferences
     * @param index the index to search from
     * @return the índex of the next unlocked wallpaper
     */
    public static int loadNextUnlockedWallpaperIndex(Context ctx, int index) {
        int length = loadWallpaperSettingsAmount(ctx);

        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + "; Length: " + length);
        }

        for (int i = index + 1; i < length; i++) {
            if (!ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE)
                    .getBoolean(WALLPAPERSETTINGS_LOCKWALLPAPER_AT + i, WallpaperSettings.DEFAULT_LOCKWALLPAPER)) {
                return i;
            }
        }

        for (int i = 0; i < index; i++) {
            if (!ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE)
                    .getBoolean(WALLPAPERSETTINGS_LOCKWALLPAPER_AT + i, WallpaperSettings.DEFAULT_LOCKWALLPAPER)) {
                return i;
            }
        }

        return index;
    }

    /**
     * This method looks up the previous index of a unlocked wallpaper. If there is no wallpaper unlocked
     * (except the wallpaper whose index is given) this method will return the given index regardless
     * of whether or not it is unlocked.
     *
     * @param ctx   the Context used for loading the values from preferences
     * @param index the index to search from
     * @return the índex of the next unlocked wallpaper
     */
    public static int loadPrevUnlockedWallpaperIndex(Context ctx, int index) {
        int length = loadWallpaperSettingsAmount(ctx);

        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + "; Length: " + length);
        }

        for (int i = index - 1; i >= 0; i--) {
            if (!ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE)
                    .getBoolean(WALLPAPERSETTINGS_LOCKWALLPAPER_AT + i, WallpaperSettings.DEFAULT_LOCKWALLPAPER)) {
                return i;
            }
        }

        for (int i = length - 1; i > index; i--) {
            if (!ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE)
                    .getBoolean(WALLPAPERSETTINGS_LOCKWALLPAPER_AT + i, WallpaperSettings.DEFAULT_LOCKWALLPAPER)) {
                return i;
            }
        }

        return index;
    }

    public static int loadWallpaperSettingsAmount(Context ctx) {
        return ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE)
                .getInt(WALLPAPERSETTINGS_NUMBEROFSETTINGS, 0);
    }

    public static WallpaperSettings loadSetting(Context ctx, int i) {
        SharedPreferences settingsPreferences = ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE);
        int length = settingsPreferences.getInt(WALLPAPERSETTINGS_NUMBEROFSETTINGS, 0);

        if (i < 0 || i >= length) {
            throw new IndexOutOfBoundsException("Index: " + i + "; Length: " + length);
        }

        String caption = settingsPreferences.getString(WALLPAPERSETTINGS_CAPTION_AT + i, "");
        boolean detectGestures = settingsPreferences.getBoolean(WALLPAPERSETTINGS_DETECTGESTURES_AT + i, WallpaperSettings.DEFAULT_DETECTGESTURES);
        boolean lockWallpaper = settingsPreferences.getBoolean(WALLPAPERSETTINGS_LOCKWALLPAPER_AT + i, WallpaperSettings.DEFAULT_LOCKWALLPAPER);
        int showWallpaperTime = settingsPreferences.getInt(WALLPAPERSETTINGS_SHOWWALLPAPERTIME_AT + i, WallpaperSettings.DEFAULT_SHOWPICTURETIME);
        int fallbackcolor = settingsPreferences.getInt(WALLPAPERSETTINGS_FALLBACKCOLOR_AT + i, WallpaperSettings.DEFAULT_FALLBACKCOLOR);
        int currentImage = settingsPreferences.getInt(WALLPAPERSETTINGS_CURRENTIMAGE_AT + i, WallpaperSettings.DEFAULT_CURRENTIMAGE);

        SharedPreferences imagesPreferences = ctx.getSharedPreferences(WALLPAPERSETTINGS_IMAGES_PREFERENCES_CODE_AT + i, Context.MODE_PRIVATE);

        int imgLength = imagesPreferences.getInt(WALLPAPERSETTINGS_IMAGES_LENGTH, 0);
        List<WallpaperImage> images = new ArrayList<>();

        for (int j = 0; j < imgLength; j++) {
            images.add(new WallpaperImage(imagesPreferences.getString(WALLPAPERSETTINGS_IMAGES_PATH_AT + j, "")));
        }

        return new WallpaperSettings(
                caption,
                detectGestures,
                lockWallpaper,
                showWallpaperTime,
                fallbackcolor,
                currentImage,
                images
        );
    }

    @SuppressLint("CommitPrefEdits")
    public static void saveSetting(Context ctx, WallpaperSettings setting, int i) {
        saveSetting(ctx, ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE).edit(), setting, i).apply();
    }

    public static SharedPreferences.Editor saveSetting(Context ctx, SharedPreferences.Editor edit, WallpaperSettings setting, int i) {
        List<WallpaperImage> imageList = setting.getImageList();

        SharedPreferences.Editor imgEditor = ctx.getSharedPreferences(WALLPAPERSETTINGS_IMAGES_PREFERENCES_CODE_AT + i, Context.MODE_PRIVATE).edit()
                .putInt(WALLPAPERSETTINGS_IMAGES_LENGTH, imageList.size());

        for (int j = 0; j < imageList.size(); j++) {
            imgEditor.putString(WALLPAPERSETTINGS_IMAGES_PATH_AT + j, imageList.get(j).getPath());
        }

        imgEditor.apply();

        return edit.putString(WALLPAPERSETTINGS_CAPTION_AT + i, setting.getCaption())
                .putBoolean(WALLPAPERSETTINGS_DETECTGESTURES_AT + i, setting.isDetectGestures())
                .putBoolean(WALLPAPERSETTINGS_LOCKWALLPAPER_AT + i, setting.isLockWallpaper())
                .putInt(WALLPAPERSETTINGS_SHOWWALLPAPERTIME_AT + i, setting.getShowPictureTime())
                .putInt(WALLPAPERSETTINGS_FALLBACKCOLOR_AT + i, setting.getFallbackcolor())
                .putInt(WALLPAPERSETTINGS_CURRENTIMAGE_AT + i, setting.getCurrentImage());
    }

    public static List<WallpaperSettings> loadAllSettings(Context ctx) {
        SharedPreferences settingsPreferences = ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE);
        int length = settingsPreferences.getInt(WALLPAPERSETTINGS_NUMBEROFSETTINGS, 0);
        List<WallpaperSettings> list = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            list.add(loadSetting(ctx, i));
        }

        return list;
    }

    public static void saveAllSettings(Context ctx, List<WallpaperSettings> settings) {
        SharedPreferences.Editor settingsPreferencesEditor = ctx.getSharedPreferences(WALLPAPERSETTINGS_PREFERENCES_CODE, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .putInt(WALLPAPERSETTINGS_NUMBEROFSETTINGS, settings.size());

        for (int i = 0; i < settings.size(); i++) {
            settingsPreferencesEditor = saveSetting(ctx, settingsPreferencesEditor, settings.get(i), i);
        }

        settingsPreferencesEditor.apply();
    }
}
