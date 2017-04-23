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
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to organize the apps internal storage.
 */
public class FileOrganizer {

    private FileOrganizer() {
    }

    /**
     * This method gives you the possibility to delete all the files in the apps internal storage while
     * keeping all the files still needed. The files still needed are thus to be given in filesToKeep.
     *
     * @param ctx         The Context to be able to delete all the files
     * @param filesToKeep a list of all the files to keep
     * @return the amount of files deleted; -1 if there were any errors
     */
    public static int keep(Context ctx, List<String> filesToKeep) {
        int ctr = 0;

        for (String filePath : getAllAppFiles(ctx)) {
            if (!filesToKeep.contains(filePath)) {
                File file = new File(filePath);
                if (file.isFile() && file.exists() && !file.delete()) {
                    return -1;
                }
                ctr++;
            }
        }
        return ctr;
    }

    /**
     * This method lists all the apps internal files.
     *
     * @param ctx The Context to be able to read all the files
     * @return a list of all the apps internal files
     */
    public static List<String> getAllAppFiles(Context ctx) {
        return listFiles(getExternalCacheDir(ctx));
    }

    /**
     * This method lists all the files within given directory, or (if a file was given)
     * the directory the file is in. Subdirectories are recursively added to the list.
     *
     * @param f the file to list
     * @return the list of all the files within the directory and all its subdirectories
     */
    private static List<String> listFiles(File f) {
        if (f.isDirectory()) {
            List<String> filePaths = new ArrayList<>();
            for (File currentFile : f.listFiles()) {
                if (currentFile.isDirectory()) {
                    filePaths.addAll(listFiles(currentFile));
                } else if (currentFile.isFile()) {
                    filePaths.add(currentFile.getAbsolutePath());
                }
            }
            return filePaths;
        } else {
            return listFiles(f.getParentFile());
        }
    }

    private static File getExternalCacheDir(Context context) {
        File file = context.getExternalCacheDir();
        if (file != null) {
            return file;
        }
        return new File(Environment.getExternalStorageDirectory().getPath() + ("/Android/data/" + context.getPackageName() + "/cache"));
    }
}
