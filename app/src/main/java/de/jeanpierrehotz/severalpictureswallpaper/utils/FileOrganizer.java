package de.jeanpierrehotz.severalpictureswallpaper.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonny on 23.01.2017.
 */
public class FileOrganizer {
    private FileOrganizer() {
    }

    public static boolean keep(Context ctx, List<String> filesToKeep) {
        for (String filePath : getAllAppFiles(ctx)) {
            if (!contains(filesToKeep, filePath)) {
                File file = new File(filePath);
                if (file.isFile() && file.exists() && !file.delete()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static List<String> getAllAppFiles(Context ctx) {
        return listFiles(getExternalCacheDir(ctx));
    }

    private static List<String> listFiles(File f) {
        List<String> filePaths = new ArrayList<>();
        for (File currentFile : f.listFiles()) {
            if (currentFile.isDirectory()) {
                filePaths.addAll(listFiles(currentFile));
            } else if (currentFile.isFile()) {
                filePaths.add(currentFile.getAbsolutePath());
            }
        }
        return filePaths;
    }

    public static boolean contains(List<String> filesToKeep, String fileToCheck) {
        for (Object aFilesToKeep : filesToKeep) {
            if (aFilesToKeep.equals(fileToCheck)) {
                return true;
            }
        }
        return false;
    }

    private static File getExternalCacheDir(Context context) {
        File file = context.getExternalCacheDir();
        if (file != null) {
            return file;
        }
        return new File(Environment.getExternalStorageDirectory().getPath() + ("/Android/data/" + context.getPackageName() + "/cache"));
    }
}
