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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.List;

import de.jeanpierrehotz.severalpictureswallpaper.utils.WallpaperPictureSelector;
import de.jeanpierrehotz.severalpictureswallpaper.views.WallpaperImageAdapter;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperImage;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperImageManager;

/**
 *
 */
public class ChangeWallpaperActivity extends AppCompatActivity{

    ///
    /// BottomSheet
    ///

    // Views & Objects
    private LinearLayout bottomSheet;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehaviour;

    private TextView furtherSettings_Value_TextView;
    private SeekBar furtherSettings_Value_SeekBar;

    private Switch furtherSettings_Swipe_Switch;

    // Listener
    private SeekBar.OnSeekBarChangeListener furtherSettings_Value_SeekBar_OnChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b){
            furtherSettings_Value_TextView.setText(String.format("%1$.1fs", ((double) furtherSettings_Value_SeekBar.getProgress())));
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar){}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar){}
    };

    ///
    /// Main content
    ///

    // Views & Objects
    private RecyclerView recyclerView;
    private WallpaperImageAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerManager;

    // Listener
    private WallpaperImageAdapter.OnItemNormalButtonClickListener recyclerNormalButtonClickListener = new WallpaperImageAdapter.OnItemNormalButtonClickListener(){
        @Override
        public void onClick(RecyclerView.ViewHolder vh, int pos){
//            images.get(pos).releaseImage();
//            images.remove(pos);
//            recyclerAdapter.notifyItemRemoved(pos);

            getSharedPreferences(getString(R.string.preferencecode_miscellanous) + wallpaperindex, MODE_PRIVATE)
                    .edit()
                    .putInt(getString(R.string.prefs_currentIndex), pos)
                    .apply();

        }
    };

    private ItemTouchHelper wallpaperImageHelper;
    private ItemTouchHelper.Callback wallpaperImageHelperCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN,  ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            int from = source.getAdapterPosition();
            int to = target.getAdapterPosition();

            Collections.swap(images, from, to);
            recyclerAdapter.notifyItemMoved(from, to);

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();

            images.remove(pos);
            recyclerAdapter.notifyItemRemoved(pos);
        }
    };

    ///
    /// Data
    ///

    private int wallpaperindex;

    private WallpaperPictureSelector mWallpaperSelector;
    private WallpaperPictureSelector.Callback mWallpaperSelectorCallback = new WallpaperPictureSelector.Callback(){
        @Override
        public void onSelectedResult(String file){}

        @Override
        public void onCropperResult(WallpaperPictureSelector.CropResult result, File srcFile, File outFile){
            if(result == WallpaperPictureSelector.CropResult.success){
                images.add(new WallpaperImage(outFile.getAbsolutePath()));
                recyclerAdapter.notifyItemInserted(images.size() - 1);
            }
        }
    };

    private List<WallpaperImage> images;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changewallpaper);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wallpaperindex = getIntent().getIntExtra(getString(R.string.prefs_wallpaperindex), 0);

        bottomSheet = (LinearLayout) findViewById(R.id.layout_content_main_bottomsheet_rootlinearlayout);
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet);

        furtherSettings_Value_TextView = (TextView) findViewById(R.id.furthersettings_time_value);
        furtherSettings_Value_SeekBar = (SeekBar) findViewById(R.id.furthersettings_time_seekbar);
        furtherSettings_Value_SeekBar.setOnSeekBarChangeListener(furtherSettings_Value_SeekBar_OnChangeListener);

        furtherSettings_Swipe_Switch = (Switch) findViewById(R.id.furthersettings_swipetoswitch_switch);

        mWallpaperSelector = new WallpaperPictureSelector(this);
        mWallpaperSelector.setCallback(mWallpaperSelectorCallback);

        images = WallpaperImageManager.loadFromSharedPreferences(getSharedPreferences(getString(R.string.preferencecode_wallpaperimages) + wallpaperindex, MODE_PRIVATE));

        recyclerView = (RecyclerView) findViewById(R.id.images_recyclerview);
        recyclerAdapter = new WallpaperImageAdapter(images);
        recyclerAdapter.setOnItemNormalButtonClickListener(recyclerNormalButtonClickListener);
        recyclerManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(recyclerManager);
        recyclerView.setAdapter(recyclerAdapter);

        wallpaperImageHelper = new ItemTouchHelper(wallpaperImageHelperCallback);
        wallpaperImageHelper.attachToRecyclerView(recyclerView);

        SharedPreferences miscprefs = getSharedPreferences(getString(R.string.preferencecode_miscellanous) + wallpaperindex, MODE_PRIVATE);

        furtherSettings_Value_SeekBar.setProgress(0);
        furtherSettings_Value_SeekBar.setProgress(1);
        furtherSettings_Value_SeekBar.setProgress(miscprefs.getInt(getString(R.string.prefs_showPictureTime), 60));

        furtherSettings_Swipe_Switch.setChecked(miscprefs.getBoolean(getString(R.string.prefs_detectGestures), true));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                selectImage();
            }
        });
    }

    private static final int CODE_TRIED_SELECTING_IMAGE = 0x12345;

    private void selectImage(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, CODE_TRIED_SELECTING_IMAGE);
            return;
        }
        mWallpaperSelector.selectImage(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CODE_TRIED_SELECTING_IMAGE && grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectImage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_changewallpaper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.menu_changewallpaper_selectwallpaper){
            getSharedPreferences(getString(R.string.preferencecode_wallpapersinfo), MODE_PRIVATE).edit().putInt(getString(R.string.prefs_wallpaperindex), wallpaperindex).apply();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        mWallpaperSelector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWallpaperSelector.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWallpaperSelector.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause(){
        super.onPause();

        WallpaperImageManager.saveToSharedPreferences(images, getSharedPreferences(getString(R.string.preferencecode_wallpaperimages) + wallpaperindex, MODE_PRIVATE));

        getSharedPreferences(getString(R.string.preferencecode_miscellanous) + wallpaperindex, MODE_PRIVATE)
                .edit()
                .putInt(getString(R.string.prefs_showPictureTime), furtherSettings_Value_SeekBar.getProgress())
                .putBoolean(getString(R.string.prefs_detectGestures), furtherSettings_Swipe_Switch.isChecked())
                .apply();
    }

}
