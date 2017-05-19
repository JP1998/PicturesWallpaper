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
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.jeanpierrehotz.severalpictureswallpaper.utils.FileOrganizer;
import de.jeanpierrehotz.severalpictureswallpaper.views.ColoredSnackbar;
import de.jeanpierrehotz.severalpictureswallpaper.views.WallpaperNameAdapter;
import de.jeanpierrehotz.severalpictureswallpaper.views.WallpaperNameViewHolder;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.SeveralPicturesWallpaperService;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperDataManager;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperSettings;

public class MainActivity extends AppCompatActivity {

    private List<WallpaperSettings> settings;

    private int selectedSetting;

    private ChandelierLayout swipeActionLayout;
    private int action; // 0 - edit; 1 - select; 2 - rename; 3 - delete

    private RecyclerView settingsList;
    private WallpaperNameAdapter settingsAdapter;
    private RecyclerView.LayoutManager settingsLayoutManager;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                AlertDialog dialog;

                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        settings.add(new WallpaperSettings(""));

                        dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.selectTitle_caption)
                                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int hay) {
                                        EditText newNameET = (EditText) dialog.findViewById(R.id.dialog_newwallpaper_newnameEditText);
                                        settings.get(settings.size() - 1).setCaption(newNameET.getText().toString());

                                        settingsAdapter.notifyDataSetChanged();
                                        settingsList.smoothScrollToPosition(settings.size() - 1);

                                        modifySetting(settings.size() - 1);
                                    }
                                })
                                .setNegativeButton(R.string.dialog_abort, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        settings.remove(settings.size() - 1);
                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        settings.remove(settings.size() - 1);
                                    }
                                })
                                .setView(R.layout.layout_dialog_newwallpaper)
                                .show();
                    }

                }
            });
        }
    }

    public void showCurrentAction() {
        ColoredSnackbar.make(Color.WHITE, settingsList, getActionString(), Snackbar.LENGTH_LONG).show();
    }

    private String getActionString() {
        switch (action) {
            case 0:
                return getString(R.string.actionstring_edit);
            case 1:
                return getString(R.string.actionstring_select);
            case 2:
                return getString(R.string.actionstring_rename);
            case 3:
                return getString(R.string.actionstring_delete);
            default:
                return getString(R.string.actionstring_fucku);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (WallpaperDataManager.transcribeFromOldArchitecture(this)) {
            ColoredSnackbar.make(Color.WHITE, findViewById(R.id.toolbar), "Successfully transcribed internal values to a new architecture.", Snackbar.LENGTH_LONG).show();
        }
        loadSettings();
        initializeLayout();

        if (getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE).getBoolean(getString(R.string.prefs_firstLaunch), true)) {
            getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE).edit().putBoolean(getString(R.string.prefs_firstLaunch), false).apply();

            settings.add(new WallpaperSettings(getString(R.string.firstSettingName)));
            selectedSetting = 0;
            saveSettings();

            Intent firstLaunchIntent = new Intent(this, AppIntroActivity.class);
            firstLaunchIntent.putExtra(getString(R.string.prefs_firstLaunch), true);
            startActivity(firstLaunchIntent);
        }

        int deletedFiles = FileOrganizer.keep(this, WallpaperDataManager.getUsedImageFiles(this));

        if (deletedFiles != -1) {
            if (deletedFiles > 0) {
                ColoredSnackbar.make(Color.WHITE, settingsList, String.format(Locale.getDefault(), getString(R.string.managefiles_success), deletedFiles), Snackbar.LENGTH_LONG).show();
            }
        } else {
            ColoredSnackbar.make(Color.WHITE, settingsList, getString(R.string.managefiles_error), Snackbar.LENGTH_LONG).show();
        }
    }

    private void initializeLayout() {
        settingsList = (RecyclerView) findViewById(R.id.wallpapersList);
        settingsLayoutManager = new LinearLayoutManager(this);
        settingsList.setLayoutManager(settingsLayoutManager);

        swipeActionLayout = (ChandelierLayout) findViewById(R.id.swipe_action_layout);

        settingsAdapter = new WallpaperNameAdapter(this, settings, selectedSetting);
        settingsList.setAdapter(settingsAdapter);

        settingsAdapter.setOnItemClickListener(new WallpaperNameAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(WallpaperNameViewHolder vh, int pos) {
                switch (action) {
                    case 0:
                        modifySetting(pos);
                        break;
                    case 1:
                        selectSetting(pos);
                        break;
                    case 2:
                        renameSetting(pos);
                        break;
                    case 3:
                        deleteSetting(pos);
                        break;
                }
            }
        });

        swipeActionLayout.setOnActionSelectedListener(new ChandelierLayout.OnActionListener() {
            @Override
            public void onActionSelected(int index, Ornament act) {
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

    private void modifySetting(int i) {
        WallpaperDataManager.saveAllSettings(this, this.settings);

        Intent intent = new Intent(this, ChangeWallpaperActivity.class);
        intent.putExtra(getString(R.string.prefs_wallpaperindex), i);
        startActivity(intent);
    }

    private void selectSetting(int i) {
        settingsAdapter.notifySelectedChanged(selectedSetting = i);
    }

    private void renameSetting(final int i) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.selectTitle_caption)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int hay) {
                            EditText newNameET = (EditText) dialog.findViewById(R.id.dialog_newwallpaper_newnameEditText);
                            settings.get(i).setCaption(newNameET.getText().toString());
                            settingsAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.dialog_abort, null)
                    .setView(R.layout.layout_dialog_newwallpaper)
                    .show();
        }

        EditText newNameET = (EditText) dialog.findViewById(R.id.dialog_newwallpaper_newnameEditText);
        newNameET.setText(settings.get(i).getCaption());
    }

    private void deleteSetting(final int index) {
        if (index == selectedSetting) {
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.deleteChosenSetting_caption)
                    .setMessage(R.string.deleteChosenSetting_message)
                    .setPositiveButton(R.string.dialog_ok, null)
                    .setNegativeButton(R.string.dialog_fuckyou, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ColoredSnackbar.make(Color.WHITE, settingsList, R.string.dialog_fuckyou_answer, Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .show();
        } else {
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.deleteWallpaper_caption)
                    .setMessage(R.string.deleteWallpaper_description)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            settings.remove(index);

                            if (index < selectedSetting) {
                                settingsAdapter.notifySelectedChanged(--selectedSetting);
                            }
                            settingsAdapter.notifyItemRemoved(index);
                        }
                    })
                    .setNegativeButton(R.string.dialog_abort, null)
                    .show();
        }
    }

    private void loadSettings() {
        settings = WallpaperDataManager.loadAllSettings(this);
        selectedSetting = WallpaperDataManager.loadSelectedSettingsIndex(this, 0);
    }

    private void saveSettings() {
        WallpaperDataManager.saveAllSettings(this, settings);
        WallpaperDataManager.saveSelectedSettingsIndex(this, selectedSetting);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.menu_main_reviewappintro) {
            Intent reviewIntroIntent = new Intent(this, AppIntroActivity.class);
            reviewIntroIntent.putExtra(getString(R.string.prefs_firstLaunch), false);
            startActivity(reviewIntroIntent);

            return true;
        } else if (id == R.id.menu_main_setwallpaper) {
            Intent i = new Intent();

            if (Build.VERSION.SDK_INT > 15) {
                i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

                String pkg = this.getPackageName();
                String cls = SeveralPicturesWallpaperService.class.getCanonicalName();

                i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(pkg, cls));
            } else {
                i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            }

            startActivityForResult(i, 0);
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
