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
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import de.jeanpierrehotz.severalpictureswallpaper.R;

/**
 *
 */
public class WallpaperImageManager {

    private static final String PATH_AT = "This is the image at index ";
    private static final String TOTAL_NUMBER = "This is the length of the pictures.";

    public static void saveToSharedPreferences(List<WallpaperImage> imgs, SharedPreferences prefs) {
        SharedPreferences.Editor edit = prefs.edit().clear().putInt(TOTAL_NUMBER, imgs.size());

        for (int i = 0; i < imgs.size(); i++) {
            edit.putString(PATH_AT + i, imgs.get(i).getPath());
        }

        edit.apply();
    }

    public static List<WallpaperImage> loadFromSharedPreferences(SharedPreferences prefs) {
        int length = prefs.getInt(TOTAL_NUMBER, 0);
        ArrayList<WallpaperImage> images = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            images.add(new WallpaperImage(prefs.getString(PATH_AT + i, "")));
        }

        return images;
    }

    public static List<String> getUsedImageFiles(Context ctx) {
        List<String> files = new ArrayList<>();

        SharedPreferences settingsPrefs = ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpapersinfo), Context.MODE_PRIVATE);
        int length = settingsPrefs.getInt(ctx.getString(R.string.prefs_wallpapercount), 0);

        for (int i = 0; i < length; i++) {
            SharedPreferences wallpaperpreferences = ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpaperimages) + i, Context.MODE_PRIVATE);
            int wallpaperamount = wallpaperpreferences.getInt(TOTAL_NUMBER, 0);

            for (int j = 0; j < wallpaperamount; j++) {
                files.add(wallpaperpreferences.getString(PATH_AT + j, ""));
            }
        }

        return files;
    }

}
