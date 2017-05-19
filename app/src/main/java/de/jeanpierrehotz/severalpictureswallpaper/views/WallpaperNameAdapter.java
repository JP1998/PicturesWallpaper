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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.jeanpierrehotz.severalpictureswallpaper.R;
import de.jeanpierrehotz.severalpictureswallpaper.wallpaper.data.WallpaperSettings;

/**
 *
 */
public class WallpaperNameAdapter extends RecyclerView.Adapter<WallpaperNameViewHolder> {

    private Context c;

    private List<WallpaperSettings> settings;
    private int selected;
    private OnItemClickListener clickListener;

    public WallpaperNameAdapter(Context ctx, List<WallpaperSettings> settings, int selected) {
        this.c = ctx;

        this.settings = settings;
        this.selected = selected;
    }

    public void notifySelectedChanged(int sel) {
        this.notifyItemChanged(this.selected);
        this.selected = sel;
        this.notifyItemChanged(this.selected);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    @Override
    public WallpaperNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_wallpaper, parent, false);

        final WallpaperNameViewHolder vh = new WallpaperNameViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemClicked(vh, vh.getAdapterPosition());
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(WallpaperNameViewHolder holder, int position) {
        holder.setCaption(settings.get(position).getCaption());
        holder.setSelected(selected, position);
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(WallpaperNameViewHolder vh, int pos);
    }

}