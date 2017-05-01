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

package de.jeanpierrehotz.severalpictureswallpaper.wallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import de.jeanpierrehotz.severalpictureswallpaper.utils.DoubleSwipeGestureDetector;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperDataManager;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperImage;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperSettings;

/**
 *
 */
public class SeveralPicturesWallpaperService extends WallpaperService {

    private final Handler handler = new Handler();

    @Override
    public Engine onCreateEngine() {
        return new SeveralPicturesWallpaper();
    }

    private enum FadeDirection {
        forward,
        backward
    }

    private static class FadeEnsemble {

        private int fallbackColor;

        private WallpaperImage from;
        private WallpaperImage to;

        private boolean locked;

        public FadeEnsemble() {
        }

        private static int applyAlpha(int color, int alpha) {
            return Color.argb(
                    alpha,
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
            );
        }

        public void lock() {
            locked = true;
        }

        public void unlock() {
            locked = false;
        }

        public void setSource(WallpaperImage src) {
            if (!locked) {
                from = src;
            }
        }

        public void setDestination(WallpaperImage dest) {
            if (!locked) {
                to = dest;
            }
        }

        public void setFallbackColor(int fallbackColor) {
            this.fallbackColor = fallbackColor;
        }

        public void draw(int alpha, float x, float y, Canvas c, Paint p) {
            if (from != null && from.drawable()) {
                p.setAlpha(alpha);
                from.drawImage(x, y, c, p);
            } else {
                p.setColor(applyAlpha(fallbackColor, alpha));
                p.setStyle(Paint.Style.FILL);
                c.drawRect(0, 0, x, y, p);
            }

            if (to != null && to.drawable()) {
                p.setAlpha(255 - alpha);
                to.drawImage(x, y, c, p);
            } else {
                p.setColor(applyAlpha(fallbackColor, 255 - alpha));
                p.setStyle(Paint.Style.FILL);
                c.drawRect(0, 0, x, y, p);
            }
        }
    }

    private class SeveralPicturesWallpaper extends Engine {

        private static final int DEBUG_ALPHASTEPS = 15;
        private static final int DEBUG_ALPHATIME = 1;
        private static final int TIME_STRETCH = 1000;

        private final Paint p;
        private final Runnable drawFading;
        private final Runnable drawSolid;

        private float x, y;
        private boolean visible;

        private int wallpaperindex;

        private WallpaperSettings settings;

        private int alpha;
        private boolean fading;

        private int previousImage;
        private int currentImage;
        private int nextImage;

        private FadeEnsemble fadingEnsemble;
        private FadeDirection fadeDirection;

        private DoubleSwipeGestureDetector mDoubleLeftSwipeGestureDetector;
        private DoubleSwipeGestureDetector.OnDoubleLeftSwipeListener mOnDoubleLeftSwipeListener = new DoubleSwipeGestureDetector.OnDoubleLeftSwipeListener() {
            @Override
            public void onDoubleLeftSwiped() {
                if (!fading && settings.isDetectGestures() && settings.getImageList().size() > 0) {
                    fadeDirection = FadeDirection.forward;

                    fadingEnsemble.setSource(settings.getImageList().get(currentImage));
                    fadingEnsemble.setDestination(settings.getImageList().get(nextImage));

                    handler.removeCallbacks(drawFading);
                    handler.removeCallbacks(drawSolid);
                    handler.post(drawFading);
                }
            }
        };
        private DoubleSwipeGestureDetector.OnDoubleRightSwipeListener mOnDoubleRightSwipeListener = new DoubleSwipeGestureDetector.OnDoubleRightSwipeListener() {
            @Override
            public void onDoubleRightSwiped() {
                if (!fading && settings.isDetectGestures() && settings.getImageList().size() > 0) {
                    fadeDirection = FadeDirection.backward;

                    fadingEnsemble.setSource(settings.getImageList().get(currentImage));
                    fadingEnsemble.setDestination(settings.getImageList().get(previousImage));

                    handler.removeCallbacks(drawFading);
                    handler.removeCallbacks(drawSolid);
                    handler.post(drawFading);
                }
            }
        };
        private DoubleSwipeGestureDetector.OnDoubleUpSwipeListener mOnDouleUpSwipeListener = new DoubleSwipeGestureDetector.OnDoubleUpSwipeListener() {
            @Override
            public void onDoubleUpSwiped() {
                if (!fading && !settings.isLockWallpaper()) {
                    skipToWallpaper(
                            (wallpaperindex + 1) % WallpaperDataManager.loadWallpaperSettingsAmount(SeveralPicturesWallpaperService.this)
                    );
                }
            }
        };
        private DoubleSwipeGestureDetector.OnDoubleDownSwipeListener mOnDouleDownSwipeListener = new DoubleSwipeGestureDetector.OnDoubleDownSwipeListener() {
            @Override
            public void onDoubleDownSwiped() {
                if (!fading && !settings.isLockWallpaper()) {
                    skipToWallpaper(
                            (wallpaperindex != 0) ?
                                    wallpaperindex - 1 :
                                    WallpaperDataManager.loadWallpaperSettingsAmount(SeveralPicturesWallpaperService.this)
                    );
                }
            }
        };


        private SeveralPicturesWallpaper() {
            p = new Paint();

            loadPreferences();

            mDoubleLeftSwipeGestureDetector = new DoubleSwipeGestureDetector();
            mDoubleLeftSwipeGestureDetector.setOnDoubleRightSwipeListener(mOnDoubleRightSwipeListener);
            mDoubleLeftSwipeGestureDetector.setOnDoubleLeftSwipeListener(mOnDoubleLeftSwipeListener);
            mDoubleLeftSwipeGestureDetector.setOnDoubleUpSwipeListener(mOnDouleUpSwipeListener);
            mDoubleLeftSwipeGestureDetector.setOnDoubleDownSwipeListener(mOnDouleDownSwipeListener);

            fadingEnsemble = new FadeEnsemble();

            drawFading = new Runnable() {
                @Override
                public void run() {
                    if (settings.getImageList().size() > 0 && !fading) {
                        fading = true;
                        alpha = 255;
                    }

                    draw();

                    handler.removeCallbacks(drawFading);
                    handler.removeCallbacks(drawSolid);

                    if (visible) {
                        if (alpha <= 0) {
                            handler.postDelayed(drawSolid, DEBUG_ALPHATIME);
                        } else {
                            handler.postDelayed(drawFading, DEBUG_ALPHATIME);
                        }
                    }

                }
            };
            drawSolid = new Runnable() {
                @Override
                public void run() {
                    draw();

                    handler.removeCallbacks(drawFading);
                    handler.removeCallbacks(drawSolid);

                    if (visible) {
                        handler.postDelayed(drawFading, settings.getShowPictureTime() * TIME_STRETCH);
                    }
                }
            };
        }

        private void loadPreferences() {
            if (fadingEnsemble == null) {
                fadingEnsemble = new FadeEnsemble();
            }

            wallpaperindex = WallpaperDataManager.loadSelectedSettingsIndex(SeveralPicturesWallpaperService.this, 0);
            settings = WallpaperDataManager.loadSetting(SeveralPicturesWallpaperService.this, wallpaperindex);

            if (settings.getImageList().size() > 0) {
                currentImage = settings.getCurrentImage();
                currentImage = getPreviousImageIndex();

                fadeDirection = FadeDirection.forward;

                fading = false;
                alpha = 0;

                refreshImages();

                fading = true;
            } else {
                fading = false;
            }
        }

        private void refreshImages() {
            previousImage = getPreviousImageIndex();
            nextImage = getNextImageIndex();

            for (int i = 0; i < settings.getImageList().size(); i++) {
                if (i == previousImage || i == currentImage || i == nextImage) {
                    settings.getImageList().get(i).loadImage(SeveralPicturesWallpaperService.this);
                } else {
                    settings.getImageList().get(i).releaseImage();
                }
            }

            fadingEnsemble.setSource(settings.getImageList().get(currentImage));
            fadingEnsemble.setDestination(settings.getImageList().get(nextImage));
        }

        private int getNextImageIndex() {
            return (currentImage == settings.getImageList().size() - 1) ? 0 : currentImage + 1;
        }

        private int getPreviousImageIndex() {
            return (currentImage == 0) ? settings.getImageList().size() - 1 : currentImage - 1;
        }

        private void skipToWallpaper(int newWallpaper) {
            boolean valid = true;

            if (settings.getImageList().size() > 0) {
                fadingEnsemble.setSource(settings.getImageList().get(currentImage));
            } else {
                valid = false;
            }
            fadingEnsemble.lock();

            wallpaperindex = newWallpaper;
            saveWallpaperIndex();
            loadPreferences();

            if (settings.getImageList().size() > 0) {
                currentImage = getPreviousImageIndex();
                settings.setCurrentImage(currentImage);
                saveImageIndex();
                refreshImages();
            }

            fadingEnsemble.unlock();
            if (settings.getImageList().size() > 0) {
                fadingEnsemble.setDestination(settings.getImageList().get(nextImage));
            } else {
                valid = false;

                fadingEnsemble.setDestination(null);
                fadingEnsemble.setSource(null);
            }

            if (valid) {
                fading = true;
                alpha = 255;
            } else {
                fading = false;
            }

            handler.removeCallbacks(drawFading);
            handler.removeCallbacks(drawSolid);
            handler.post(drawFading);
        }

        private void draw() {
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;

            boolean fadingDrawAttempt = this.fading;

            try {
                c = holder.lockCanvas();
                if (c != null) {
                    if (!fadingDrawAttempt) {
                        p.setAlpha(255);
                        if (settings.getImageList().size() > 0 && settings.getImageList().get(currentImage).drawable()) {
                            settings.getImageList().get(currentImage).drawImage(x, y, c, p);
                        } else {
                            p.setColor(settings.getFallbackcolor());
                            p.setStyle(Paint.Style.FILL);
                            c.drawRect(0, 0, x, y, p);
                        }
                    } else {
                        fadingEnsemble.draw(alpha, x, y, c, p);
                        refreshAlphas();
                    }
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);

            mDoubleLeftSwipeGestureDetector.onTouchEvent(event);
        }

        private void refreshAlphas() {
            alpha -= DEBUG_ALPHASTEPS;

            if (alpha <= 0) {
                if (fadeDirection == FadeDirection.forward) {
                    currentImage = getNextImageIndex();
                } else {
                    currentImage = getPreviousImageIndex();
                }
                settings.setCurrentImage(currentImage);
                saveImageIndex();

                fading = false;
                fadeDirection = FadeDirection.forward;

                refreshImages();
            }
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            loadPreferences();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            handler.removeCallbacks(drawFading);
            handler.removeCallbacks(drawSolid);
        }

        public void saveWallpaperIndex() {
            WallpaperDataManager.saveSelectedSettingsIndex(SeveralPicturesWallpaperService.this, wallpaperindex);
        }

        private void saveImageIndex() {
            WallpaperDataManager.saveSetting(SeveralPicturesWallpaperService.this, settings, wallpaperindex);
        }

        @Override
        public void onVisibilityChanged(boolean v) {
            if (visible = v) {
                loadPreferences();
                handler.post(drawFading);
            } else {
                handler.removeCallbacks(drawFading);
                handler.removeCallbacks(drawSolid);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            if (width < height) {
                x = width;
                y = height;
            }

            handler.post(drawFading);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);

            visible = false;

            handler.removeCallbacks(drawFading);
            handler.removeCallbacks(drawSolid);
        }
    }
}
