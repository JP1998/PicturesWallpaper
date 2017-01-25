package de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import de.jeanpierrehotz.severalpictureswallpaper.R;

/**
 * Created by Jonny on 23.01.2017.
 */

public class WallpaperImageManager {

    private static final String PATH_AT = "This is the image at index ";
    private static final String TOTAL_NUMBER = "This is the length of the pictures.";

    public static void saveToSharedPreferences(List<WallpaperImage> imgs, SharedPreferences prefs){
        SharedPreferences.Editor edit = prefs.edit().clear().putInt(TOTAL_NUMBER, imgs.size());

        for(int i = 0; i < imgs.size(); i++){
            edit.putString(PATH_AT + i, imgs.get(i).getPath());
        }

        edit.apply();
    }

    public static List<WallpaperImage> loadFromSharedPreferences(SharedPreferences prefs){
        int length = prefs.getInt(TOTAL_NUMBER, 0);
        ArrayList<WallpaperImage> images = new ArrayList<>();

        for(int i = 0; i < length; i++){
            images.add(new WallpaperImage(prefs.getString(PATH_AT + i, "")));
        }

        return images;
    }

    public static List<String> getUsedImageFiles(Context ctx) {
        List<String> files = new ArrayList<>();

        SharedPreferences settingsPrefs = ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpapersinfo), Context.MODE_PRIVATE);
        int length = settingsPrefs.getInt(ctx.getString(R.string.prefs_wallpapercount), 0);

        for(int i = 0; i < length; i++){
            SharedPreferences wallpaperpreferences = ctx.getSharedPreferences(ctx.getString(R.string.preferencecode_wallpaperimages) + i, Context.MODE_PRIVATE);
            int wallpaperamount = wallpaperpreferences.getInt(TOTAL_NUMBER, 0);

            for(int j = 0; j < wallpaperamount; j++){
                files.add(wallpaperpreferences.getString(PATH_AT + j, ""));
            }
        }

        return files;
    }

}
