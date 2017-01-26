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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.prolificinteractive.chandelier.widget.ChandelierLayout;
import com.prolificinteractive.chandelier.widget.Ornament;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jeanpierrehotz.severalpictureswallpaper.utils.FileOrganizer;
import de.jeanpierrehotz.severalpictureswallpaper.views.ColoredSnackbar;
import de.jeanpierrehotz.severalpictureswallpaper.views.WallpaperNameAdapter;
import de.jeanpierrehotz.severalpictureswallpaper.views.WallpaperNameViewHolder;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperImage;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperImageManager;

public class MainActivity extends AppCompatActivity{

    private ArrayList<String> settings_caption;
    private ArrayList<Integer> settings_indexes;

    private boolean deleted;
    private int selectedSetting;

    private ChandelierLayout swipeActionLayout;
    private int action; // 0 - edit; 1 - select; 2 - rename; 3 - delete

    private RecyclerView settingsList;
    private WallpaperNameAdapter settingsAdapter;
    private RecyclerView.LayoutManager settingsLayoutManager;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settings_caption = new ArrayList<>();
        settings_indexes = new ArrayList<>();
        deleted = false;

        if(getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE).getBoolean(getString(R.string.prefs_firstLaunch), true)){
            getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE).edit().putBoolean(getString(R.string.prefs_firstLaunch), false).apply();

            settings_caption.add(getString(R.string.firstSettingName));
            selectedSetting = 0;
            saveSettings();

            Intent firstLaunchIntent = new Intent(this, AppIntroActivity.class);
            firstLaunchIntent.putExtra(getString(R.string.prefs_firstLaunch), true);
            startActivity(firstLaunchIntent);
        }else{
            loadSettings();
        }

        initializeLayout();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null){
            fab.setOnClickListener(new View.OnClickListener(){
                AlertDialog dialog;

                @Override
                public void onClick(View view){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        settings_caption.add("");

                        dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.selectTitle_caption)
                                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int hay){
                                        EditText newNameET = (EditText) dialog.findViewById(R.id.dialog_newwallpaper_newnameEditText);
                                        settings_caption.set(settings_caption.size() - 1, newNameET.getText().toString());

                                        settingsAdapter.notifyDataSetChanged();
                                        settingsList.smoothScrollToPosition(settings_caption.size() - 1);

                                        modifySetting(settings_caption.size() - 1);
                                    }
                                })
                                .setNegativeButton(R.string.dialog_abort, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i){
                                        settings_caption.remove(settings_caption.size() - 1);
                                    }
                                })
                                .setView(R.layout.layout_dialog_newwallpaper)
                                .show();
                    }

                }
            });
        }
    }

    public void showCurrentAction(){
        ColoredSnackbar.make(Color.WHITE, settingsList, getActionString(), Snackbar.LENGTH_LONG).show();
    }

    private String getActionString(){
        switch(action){
            case 0: return getString(R.string.actionstring_edit);
            case 1: return getString(R.string.actionstring_select);
            case 2: return getString(R.string.actionstring_rename);
            case 3: return getString(R.string.actionstring_delete);
            default: return getString(R.string.actionstring_fucku);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        settings_caption = new ArrayList<>();
        settings_indexes = new ArrayList<>();
        deleted = false;

        loadSettings();

        initializeLayout();

        if(FileOrganizer.keep(this, WallpaperImageManager.getUsedImageFiles(this))){
            ColoredSnackbar.make(Color.WHITE, settingsList, "Successfully organized app files.", Snackbar.LENGTH_LONG).show();
        } else {
            ColoredSnackbar.make(Color.WHITE, settingsList, "Something went wrong while organizing the app files.", Snackbar.LENGTH_LONG).show();
        }

//        exampleListView = (ListView) findViewById(R.id.settingsListView);
//        ad = new SettingsAdapter(this, R.layout.layout_listitem_setting, settings_caption, selectedSetting);
//
//        exampleListView.setAdapter(ad);
//
//        exampleListView.setOnItemClickListener(changeSettingListener);
//        exampleListView.setOnItemLongClickListener(createContextMenuListener);
    }

    private void initializeLayout(){
        settingsList = (RecyclerView) findViewById(R.id.wallpapersList);
        settingsLayoutManager = new LinearLayoutManager(this);
        settingsList.setLayoutManager(settingsLayoutManager);

        swipeActionLayout = (ChandelierLayout) findViewById(R.id.swipe_action_layout);

        settingsAdapter = new WallpaperNameAdapter(this, settings_caption, selectedSetting);
        settingsList.setAdapter(settingsAdapter);

        settingsAdapter.setOnItemClickListener(new WallpaperNameAdapter.OnItemClickListener(){
            @Override
            public void onItemClicked(WallpaperNameViewHolder vh, int pos){
                switch(action){
                    case 0:
                        modifySetting(pos);
                        break;
                    case 1:
                        selectSetting(pos);
                        break;
                    case 2:
                        renameSettings(pos);
                        break;
                    case 3:
                        deleteSetting(pos);
                        break;
                }
            }
        });

        swipeActionLayout.setOnActionSelectedListener(new ChandelierLayout.OnActionListener(){
            @Override
            public void onActionSelected(int index, Ornament act){
                action = index;
                showCurrentAction();
            }
        });

        swipeActionLayout.populateActionItems(Arrays.asList(
                new Ornament(R.drawable.ic_edit),
                new Ornament(R.drawable.ic_select),
                new Ornament(R.drawable.ic_rename),
                new Ornament(R.drawable.ic_delete)
        ));
    }

    private void modifySetting(int i){
        Intent intent = new Intent(this, ChangeWallpaperActivity.class);
        intent.putExtra(getString(R.string.prefs_wallpaperindex), i);
        startActivity(intent);
    }

    private void selectSetting(int i){
        settingsAdapter.notifySelectedChanged(selectedSetting = i);
    }

    private void renameSettings(final int i){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.selectTitle_caption)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int hay){
                            EditText newNameET = (EditText) dialog.findViewById(R.id.dialog_newwallpaper_newnameEditText);
                            settings_caption.set(i, newNameET.getText().toString());
                            settingsAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.dialog_abort, null)
                    .setView(R.layout.layout_dialog_newwallpaper)
                    .show();
        }

        EditText newNameET = (EditText) dialog.findViewById(R.id.dialog_newwallpaper_newnameEditText);
        newNameET.setText(settings_caption.get(i));
    }

    private void deleteSetting(final int index){
        if(index == selectedSetting){
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.deleteChosenSetting_caption)
                    .setMessage(R.string.deleteChosenSetting_message)
                    .setPositiveButton(R.string.dialog_ok, null)
                    .setNegativeButton(R.string.dialog_fuckyou, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            ColoredSnackbar.make(Color.WHITE, settingsList, R.string.dialog_fuckyou_answer, Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .show();
        }else{
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.deleteWallpaper_caption)
                    .setMessage(R.string.deleteWallpaper_description)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i){
                            settings_caption.remove(index);
                            settings_indexes.remove(index);
                            deleted = true;

                            if(index < selectedSetting){
                                settingsAdapter.notifySelectedChanged(--selectedSetting);
                            }else{
                                settingsAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_abort, null)
                    .show();
        }

    }

    private void loadSettings(){
        SharedPreferences settingsPrefs = getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE);
        int length = settingsPrefs.getInt(getString(R.string.prefs_wallpapercount), 0);

        for(int i = 0; i < length; i++){
            settings_caption.add(settingsPrefs.getString(getString(R.string.prefs_wallpapername) + i, ""));
            settings_indexes.add(i);
        }
        selectedSetting = settingsPrefs.getInt(getString(R.string.prefs_wallpaperindex), 0);
    }

    private void saveSettings(){
        boolean firsttime = getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE).getBoolean(getString(R.string.prefs_firstLaunch), true);
        SharedPreferences.Editor edit = getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE).edit();
        edit.clear()
                .putInt(getString(R.string.prefs_wallpapercount), settings_caption.size())
                .putBoolean(getString(R.string.prefs_firstLaunch), firsttime);

        for(int i = 0; i < settings_caption.size(); i++){
            edit.putString(getString(R.string.prefs_wallpapername) + i, settings_caption.get(i));
        }

        edit.putInt(getString(R.string.prefs_wallpaperindex), selectedSetting);
        edit.apply();
    }

    private void saveSettingChanges(){
        for(int i = 0; i < settings_caption.size(); i++){
            /**
             * LOAD THE SETTING AT THE PREVIOUS INDEX
             */
            List<WallpaperImage> imgs = WallpaperImageManager.loadFromSharedPreferences(getSharedPreferences(getString(R.string.preferencecode_wallpaperimages) + settings_indexes.get(i), MODE_PRIVATE));

            SharedPreferences currentMiscPrefs = getSharedPreferences(getString(R.string.preferencecode_miscellanous) + settings_indexes.get(i), MODE_PRIVATE);

            int waittime = currentMiscPrefs.getInt(getString(R.string.prefs_showPictureTime), 30);
            boolean detectGestures = currentMiscPrefs.getBoolean(getString(R.string.prefs_detectGestures), true);

            /**
             * DELETE THE PREFERENCES
             */

            currentMiscPrefs.edit().clear().apply();
            getSharedPreferences(getString(R.string.preferencecode_wallpaperimages) + settings_indexes.get(i), MODE_PRIVATE).edit().clear().apply();

            /**
             * AND SAVE THE VALUES AT THE NEW INDEX:
             */
            WallpaperImageManager.saveToSharedPreferences(imgs, getSharedPreferences(getString(R.string.preferencecode_wallpaperimages) + settings_indexes.get(i), MODE_PRIVATE));

            getSharedPreferences(getString(R.string.preferencecode_miscellanous) + i, MODE_PRIVATE)
                    .edit()
                    .putInt(getString(R.string.prefs_showPictureTime), waittime)
                    .putBoolean(getString(R.string.prefs_detectGestures), detectGestures)
                    .apply();
        }

        /**
         * THEN DELETE ALL THE SETTINGS, THAT WERE DELETED
         *  -> In [0 .. caption.length[ the deleted ones are overwritten
         *  => Delete in [caption.length .. length[
         */
        int length = getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE).getInt(getString(R.string.prefs_wallpapercount), 0);
        for(int i = settings_caption.size(); i < length; i++){
            getSharedPreferences(getString(R.string.preferencecode_wallpaperimages) + i, MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(getString(R.string.preferencecode_miscellanous) + i, MODE_PRIVATE).edit().clear().apply();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(deleted){
            saveSettingChanges();
        }
        saveSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == R.id.menu_main_reviewappintro){
            Intent reviewIntroIntent = new Intent(this, AppIntroActivity.class);
            reviewIntroIntent.putExtra(getString(R.string.prefs_firstLaunch), false);
            startActivity(reviewIntroIntent);

            return true;
        }

//        else if(id == R.id.menu_main_about){
//            startActivity(new Intent(this, AboutActivity.class));
//            return true;
//        }

//        else if(id == R.id.menu_main_show_material){
//            startActivity(new Intent(this, ChangeSettingsMaterialTryHard.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}
