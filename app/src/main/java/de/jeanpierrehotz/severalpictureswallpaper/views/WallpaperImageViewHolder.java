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

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.blunderer.materialdesignlibrary.views.CardView;

import de.jeanpierrehotz.severalpictureswallpaper.R;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperImage;

/**
 *
 */
public class WallpaperImageViewHolder extends RecyclerView.ViewHolder{

    private CardView mRootCardView;
    private int position;

    public WallpaperImageViewHolder(View itemView){
        super(itemView);

        mRootCardView = (CardView) itemView.findViewById(R.id.cardview_item_root);

        // Retrieve the caption and set its ellipsize and singleline attributes, since there are some
        // bugs with this by defining it in the styles.xml :(
        AppCompatTextView tv0 = (AppCompatTextView) ((LinearLayout) ((LinearLayout) mRootCardView.getChildAt(0)).getChildAt(1)).getChildAt(0);
        tv0.setEllipsize(TextUtils.TruncateAt.END);
        tv0.setSingleLine(true);
    }

    public void onBind(int pos, WallpaperImage img){
        position = pos;

        img.loadAsPreview(mRootCardView);
        mRootCardView.setTitle(img.getFileName());
        mRootCardView.setDescription(img.getResolution());
    }

    public int getNumber(){
        return position;
    }

}
