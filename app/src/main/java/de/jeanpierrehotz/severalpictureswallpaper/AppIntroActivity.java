/*
 *      Copyright 2016 Jean-Pierre Hotz
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Admin on 02.09.2016.
 */
public class AppIntroActivity extends AppIntro{

    /*
     * Since I've gotten too lazy to keep two AppIntros running I just save whether it's
     * started from the first launch of the app.
     */
    private boolean firstLaunch;

    @Override
    public void init(@Nullable Bundle savedInstanceState){
        firstLaunch = getIntent().getBooleanExtra(getString(R.string.prefs_firstLaunch), false);

        if(firstLaunch)
            showSkipButton(false);

        /* The slide which shows you how to add a wallpaper */
        addSlide(
                AppIntroFragment.newInstance(
                        getString(R.string.appIntro_addSettings_caption),
                        getString(R.string.appIntro_addSettings_description),
                        R.drawable.appintro_addsettingpicture,
                        0xFF3F51B5
                )
        );
        /* The slide which shows you how to open the context menu */
        addSlide(
                AppIntroFragment.newInstance(
                        getString(R.string.appIntro_contextMenu_caption),
                        getString(R.string.appIntro_contextMenu_description),
                        R.drawable.appintro_contextmenupicture,
                        0xFF3F51B5
                )
        );
        /* The slide which tells you about the background image */ // writestorage - 4
        addSlide(
                AppIntroFragment.newInstance(
                        getString(R.string.appIntro_backgroundImage_caption),
                        getString(R.string.appIntro_backgroundImage_description),
                        R.drawable.appintro_backgroundimagepicture,
                        0xFF3F51B5
                )
        );
        /* The slide which shows you that you're done with the intro */
        addSlide(
                AppIntroFragment.newInstance(
                        getString(R.string.appIntro_doneWithIntro_caption),
                        getString((firstLaunch)? R.string.appIntro_doneWithIntro_description: R.string.appIntro_review_doneWithIntro_description),
                        R.drawable.appintro_donewithintropicture,
                        0xFF3F51B5
                )
        );

        /* Ask for the needed permissions in the app */
        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);

        /* Make it look all nice */
        setBarColor(0xFF303F9F);
        showStatusBar(false);
        setZoomAnimation();
    }

    @Override
    public void onSkipPressed(){
        /* when skipping we just start the first setting */
        startFirstSetting();
    }

    @Override
    public void onNextPressed(){

    }

    @Override
    public void onDonePressed(){
        /* when done we just start the first setting */
        startFirstSetting();
    }

    @Override
    public void onSlideChanged(){

    }

    /**
     * This method starts the first setting if this activity was started as a result of the
     * first app-launch
     */
    private void startFirstSetting(){
        if(firstLaunch){
            Intent intent = new Intent(this, ChangeWallpaperActivity.class);
            intent.putExtra(getString(R.string.prefs_wallpaperindex), 0);  //we need to give it the settings index
            startActivity(intent);
        }

        finish();                                                           // we don't want the user to come back here
    }
}