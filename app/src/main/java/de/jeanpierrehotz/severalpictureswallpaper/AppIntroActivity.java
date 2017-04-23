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

package de.jeanpierrehotz.severalpictureswallpaper;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;

import de.jeanpierrehotz.severalpictureswallpaper.views.SoftPermissionSlide;

/**
 *
 */
public class AppIntroActivity extends IntroActivity {

    /**
     * Since I've gotten too lazy to keep two AppIntros running I just save whether it's
     * started from the first launch of the app.
     */
    private boolean firstLaunch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firstLaunch = getIntent().getBooleanExtra(getString(R.string.prefs_firstLaunch), false);
        setFullscreen(true);

        super.onCreate(savedInstanceState);

        setButtonBackFunction(BUTTON_BACK_FUNCTION_SKIP);
        setButtonBackVisible(!firstLaunch);

        setButtonNextFunction(BUTTON_NEXT_FUNCTION_NEXT_FINISH);

        /* The slide which shows you how to add a wallpaper */
        addSlide(
                new SoftPermissionSlide.Builder()
                        .title(R.string.appIntro_addSettings_caption)
                        .description(R.string.appIntro_addSettings_description)
                        .image(R.drawable.appintro_addsettingpicture)
                        .background(R.color.intro)
                        .backgroundDark(R.color.intro_dark)
                        .build()
        );
        /* The slide which shows you how to open the context menu */
        addSlide(
                new SoftPermissionSlide.Builder()
                        .title(R.string.appIntro_contextMenu_caption)
                        .description(R.string.appIntro_contextMenu_description)
                        .image(R.drawable.appintro_contextmenupicture)
                        .background(R.color.intro)
                        .backgroundDark(R.color.intro_dark)
                        .build()
        );
        /* The slide which tells you about the background image */ // writestorage - 4
        addSlide(
                new SoftPermissionSlide.Builder()
                        .title(R.string.appIntro_backgroundImage_caption)
                        .description(R.string.appIntro_backgroundImage_description)
                        .image(R.drawable.appintro_backgroundimagepicture)
                        .background(R.color.intro)
                        .backgroundDark(R.color.intro_dark)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .canGoForward(true)
                        .build()
        );
        /* The slide which shows you that you're done with the intro */
        addSlide(
                new SoftPermissionSlide.Builder()
                        .title(R.string.appIntro_doneWithIntro_caption)
                        .description((firstLaunch) ? R.string.appIntro_doneWithIntro_description : R.string.appIntro_review_doneWithIntro_description)
                        .image(R.drawable.appintro_donewithintropicture)
                        .background(R.color.intro)
                        .build()
        );
    }

    @Override
    public void finish() {
        startFirstSetting();
    }

    /**
     * This method starts the first setting if this activity was started as a result of the
     * first app-launch
     */
    private void startFirstSetting() {
        if (firstLaunch) {
            Intent intent = new Intent(this, ChangeWallpaperActivity.class);
            intent.putExtra(getString(R.string.prefs_wallpaperindex), 0); //we need to give it the settings index
            startActivity(intent);
        }

        super.finish();// we don't want the user to come back here
    }
}
