package com.cameron.materialcolorpicker;

import android.support.annotation.ColorInt;

/**
 * Created by Patrick Geselbracht on 2017-03-04
 *
 * @author Patrick Geselbracht
 */
public interface ColorPickerCallback {
    /**
     * Gets called whenever a user chooses a color from the ColorPicker, i.e., presses the
     * "Choose" button.
     *
     * @param color      Color chosen
     * @param hex        The color chosen as a hex value (with alpha included).
     *                   Ex: #FF000000
     * @param hexNoAlpha The color chosen as a hex value (without alpha).
     *                   Ex: #FF0000
     */
    void onColorChosen(@ColorInt int color, String hex, String hexNoAlpha);

    /**
     * Gets called whenever the value of the SeekBars have changed.
     *
     * @param color      The current color
     * @param hex        The current color in hex value, with alpha included, i.e., #FF00FF00
     * @param hexNoAlpha The current color in RGB hex format, without alpha, i.e., #00FF00
     */
    void onColorChanged(@ColorInt int color, String hex, String hexNoAlpha);
}

