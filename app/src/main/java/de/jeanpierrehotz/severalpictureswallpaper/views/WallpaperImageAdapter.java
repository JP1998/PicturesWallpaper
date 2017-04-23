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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blunderer.materialdesignlibrary.views.CardView;

import java.util.List;

import de.jeanpierrehotz.severalpictureswallpaper.R;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperImage;

/**
 *
 */
public class WallpaperImageAdapter extends RecyclerView.Adapter<WallpaperImageViewHolder> {

    private List<WallpaperImage> dataSet;

    private OnItemNormalButtonClickListener mNormalButtonClickListener;
    private OnItemHighlightButtonClickListener mHighlightButtonclickListener;

    public WallpaperImageAdapter(List<WallpaperImage> data) {
        dataSet = data;
    }

    public void setOnItemNormalButtonClickListener(OnItemNormalButtonClickListener list) {
        mNormalButtonClickListener = list;
    }

    public void clearOnItemNormalButtonClickListener() {
        mNormalButtonClickListener = null;
    }

    public void setOnItemHighlightButtonClickListener(OnItemHighlightButtonClickListener list) {
        mHighlightButtonclickListener = list;
    }

    public void clearOnItemHighlightButtonClickListener() {
        mHighlightButtonclickListener = null;
    }


    @Override
    public WallpaperImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_wallpaperimage, parent, false);

        final WallpaperImageViewHolder vh = new WallpaperImageViewHolder(v);

        CardView cardView = (CardView) v.findViewById(R.id.cardview_item_root);

        cardView.setOnNormalButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNormalButtonClickListener != null) {
                    mNormalButtonClickListener.onClick(vh, vh.getAdapterPosition());
                }
            }
        });

        cardView.setOnHighlightButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHighlightButtonclickListener != null) {
                    mHighlightButtonclickListener.onClick(vh, vh.getAdapterPosition());
                }
            }
        });

        return vh;
    }

    @Override
    public void onViewRecycled(WallpaperImageViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder.getAdapterPosition() < dataSet.size() && holder.getAdapterPosition() >= 0) {
            dataSet.get(holder.getAdapterPosition()).releaseImage();
        }
    }

    @Override
    public void onBindViewHolder(WallpaperImageViewHolder holder, int position) {
        holder.onBind(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemNormalButtonClickListener {
        void onClick(RecyclerView.ViewHolder vh, int pos);
    }

    public interface OnItemHighlightButtonClickListener {
        void onClick(RecyclerView.ViewHolder vh, int pos);
    }
}
