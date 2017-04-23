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

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * This class is a convenience class for changing colors of a Snackbar
 */
public class ColoredSnackbar {

    /**
     * This method creates a Snackbar with given background-color
     *
     * @param bg     the background color that should be applied to the Snackbar
     * @param v      The view used according to {@link Snackbar#make(View, int, int)}
     * @param id     The string id used according to {@link Snackbar#make(View, int, int)}
     * @param length The length used according to {@link Snackbar#make(View, int, int)}
     * @return the Snackbar with given informations and given background color
     */
    public static Snackbar make(int bg, View v, int id, int length) {
        Snackbar bar = Snackbar.make(v, id, length);
        bar.getView().setBackgroundColor(bg);
        return bar;
    }

    /**
     * This method creates a Snackbar with given background-color
     *
     * @param bg     the background color that should be applied to the Snackbar
     * @param v      The view used according to {@link Snackbar#make(View, CharSequence, int)}
     * @param text   The CharSequence used according to {@link Snackbar#make(View, CharSequence, int)}
     * @param length The length used according to {@link Snackbar#make(View, CharSequence, int)}
     * @return the Snackbar with given informations and given background color
     */
    public static Snackbar make(int bg, View v, CharSequence text, int length) {
        Snackbar bar = Snackbar.make(v, text, length);
        bar.getView().setBackgroundColor(bg);
        return bar;
    }

    /**
     * This method creates a Snackbar with given font-color
     *
     * @param v      The view used according to {@link Snackbar#make(View, int, int)}
     * @param id     The string id used according to {@link Snackbar#make(View, int, int)}
     * @param length The length used according to {@link Snackbar#make(View, int, int)}
     * @param font   the font color that should be applied to the Snackbar
     * @return the Snackbar with given informations and given font color
     */
    public static Snackbar make(View v, int id, int length, int font) {
        Snackbar bar = Snackbar.make(v, id, length);
        ((TextView) bar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(font);
        return bar;
    }

    /**
     * This method creates a Snackbar with given font-color
     *
     * @param v      The view used according to {@link Snackbar#make(View, CharSequence, int)}
     * @param text   The CharSequence used according to {@link Snackbar#make(View, CharSequence, int)}
     * @param length The length used according to {@link Snackbar#make(View, CharSequence, int)}
     * @param font   the font color that should be applied to the Snackbar
     * @return the Snackbar with given informations and given font color
     */
    public static Snackbar make(View v, CharSequence text, int length, int font) {
        Snackbar bar = Snackbar.make(v, text, length);
        ((TextView) bar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(font);
        return bar;
    }

    /**
     * This method creates a Snackbar with given background- and font-color.
     *
     * @param bg     the background color that should be applied to the Snackbar
     * @param v      The view used according to {@link Snackbar#make(View, int, int)}
     * @param id     The string id used according to {@link Snackbar#make(View, int, int)}
     * @param length The length used according to {@link Snackbar#make(View, int, int)}
     * @param font   the font color that should be applied to the Snackbar
     * @return the Snackbar with given informations and given background- and font-color
     */
    public static Snackbar make(int bg, View v, int id, int length, int font) {
        Snackbar bar = Snackbar.make(v, id, length);
        bar.getView().setBackgroundColor(bg);
        ((TextView) bar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(font);
        return bar;
    }

    /**
     * This method creates a Snackbar with given background- and font-color.
     *
     * @param bg     the background color that should be applied to the Snackbar
     * @param v      The view used according to {@link Snackbar#make(View, CharSequence, int)}
     * @param text   The CharSequence used according to {@link Snackbar#make(View, CharSequence, int)}
     * @param length The length used according to {@link Snackbar#make(View, CharSequence, int)}
     * @param font   the font color that should be applied to the Snackbar
     * @return the Snackbar with given informations and given background- and font-color
     */
    public static Snackbar make(int bg, View v, CharSequence text, int length, int font) {
        Snackbar bar = Snackbar.make(v, text, length);
        bar.getView().setBackgroundColor(bg);
        ((TextView) bar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(font);
        return bar;
    }
}
