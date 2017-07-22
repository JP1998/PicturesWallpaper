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

package de.jeanpierrehotz.severalpictureswallpaper.views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.jeanpierrehotz.severalpictureswallpaper.R;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperImage;

/**
 *
 */
public class WallpaperImageViewHolder extends RecyclerView.ViewHolder {

    private LinearLayout rootLayout;

    private CardView itemCardView;

    private ImageView imageView;
    private TextView captionView;
    private TextView resolutionView;

    private Button selectButton;

    private WallpaperImage.PreviewLoaderTask loader;

    public WallpaperImageViewHolder(View itemView) {
        super(itemView);

        this.rootLayout = (LinearLayout) itemView;
        this.itemCardView = (CardView) itemView.findViewById(R.id.leyout_item_wallpaperimage_cardview);
        this.imageView = (ImageView) itemView.findViewById(R.id.leyout_item_wallpaperimage_image_imageview);
        this.captionView = (TextView) itemView.findViewById(R.id.leyout_item_wallpaperimage_imagefilename_textview);
        this.resolutionView = (TextView) itemView.findViewById(R.id.leyout_item_wallpaperimage_imagefileresolution_textview);
        this.selectButton = (Button) itemView.findViewById(R.id.leyout_item_wallpaperimage_selectimage_button);
    }

    public void onBind(WallpaperImage img) {
        this.loader = img.loadAsPreview(this.imageView);
        this.captionView.setText(img.getFileName());
        this.resolutionView.setText(img.getResolution());
    }

    public void onUnbind() {
        if (this.loader != null) {
            this.loader.cancel();
        }
        this.imageView.setImageDrawable(null);
    }

    public Button getSelectButton() {
        return selectButton;
    }
}
