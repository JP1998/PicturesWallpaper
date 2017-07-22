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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.jeanpierrehotz.severalpictureswallpaper.R;

/**
 *
 */
public class WallpaperNameViewHolder extends RecyclerView.ViewHolder {

    private RelativeLayout rootView;

    private TextView captionTextView;
    //    private TextView selectedTextView;
    private ImageView selectedImageView;

    public WallpaperNameViewHolder(View itemView) {
        super(itemView);

        rootView = (RelativeLayout) itemView.findViewById(R.id.rootlayout);

        captionTextView = (TextView) itemView.findViewById(R.id.setting_name_textview);
//        selectedTextView = (TextView) itemView.findViewById(R.id.setting_selected_textview);
        selectedImageView = (ImageView) itemView.findViewById(R.id.setting_selected_imageview);
    }

    public void setCaption(String capt) {
        captionTextView.setText(capt);
    }

    public void setSelected(int sel, int num) {
        if (sel == num) {
//            selectedTextView.setText(rootView.getContext().getString(R.string.selected_text));
            selectedImageView.setImageResource(R.drawable.ic_vector_selected_green);
        } else {
//            selectedTextView.setText("");
            selectedImageView.setImageDrawable(null);
        }
    }
}