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

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class WallpaperSettings {

    public static final boolean DEFAULT_DETECTGESTURES = true;
    public static final boolean DEFAULT_LOCKWALLPAPER = true;
    public static final int DEFAULT_SHOWPICTURETIME = 60;
    public static final int DEFAULT_FALLBACKCOLOR = 0xFF36465D;
    public static final int DEFAULT_CURRENTIMAGE = 0;

    private String caption;

    private boolean detectGestures;
    private boolean lockWallpaper;
    private int showPictureTime;
    private int fallbackcolor;
    private int currentImage;
    private List<WallpaperImage> images;

    public WallpaperSettings(String caption) {
        this(
                caption,
                DEFAULT_DETECTGESTURES,
                DEFAULT_LOCKWALLPAPER,
                DEFAULT_SHOWPICTURETIME,
                DEFAULT_FALLBACKCOLOR,
                DEFAULT_CURRENTIMAGE,
                new ArrayList<WallpaperImage>()
        );
    }

    WallpaperSettings(String capt, boolean dG, boolean lW, int sPT, int fBC, int cI, List<WallpaperImage> imgs) {
        this.caption = capt;
        this.detectGestures = dG;
        this.lockWallpaper = lW;
        this.showPictureTime = sPT;
        this.fallbackcolor = fBC;
        this.currentImage = cI;
        this.images = imgs;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isDetectGestures() {
        return detectGestures;
    }

    public void setDetectGestures(boolean detectGestures) {
        this.detectGestures = detectGestures;
    }

    public boolean isLockWallpaper() {
        return lockWallpaper;
    }

    public void setLockWallpaper(boolean lockWallpaper) {
        this.lockWallpaper = lockWallpaper;
    }

    public int getShowPictureTime() {
        return showPictureTime;
    }

    public void setShowPictureTime(int showPictureTime) {
        this.showPictureTime = showPictureTime;
    }

    public int getFallbackcolor() {
        return fallbackcolor;
    }

    public void setFallbackcolor(int fallbackcolor) {
        this.fallbackcolor = fallbackcolor;
    }

    public int getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(int currentImage) {
        this.currentImage = currentImage;
    }

    public void addImage(WallpaperImage img) {
        this.images.add(img);
    }

    public void removeImage(int i) {
        this.images.remove(i);
    }

    public List<WallpaperImage> getImageList() {
        return this.images;
    }
}
