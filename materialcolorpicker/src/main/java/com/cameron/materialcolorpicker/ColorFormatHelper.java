package com.cameron.materialcolorpicker;

import android.graphics.Color;
import android.support.annotation.IntRange;

final class ColorFormatHelper {

    /**
     * Checks whether the specified value is between (including bounds) 0 and 255
     *
     * @param colorValue Color value
     * @return Specified input value if between 0 and 255, otherwise 0
     */
    static int assertColorValueInRange(@IntRange(from = 0, to = 255) int colorValue) {
        return ((0 <= colorValue) && (colorValue <= 255)) ? colorValue : 0;
    }

    /**
     * Checks if the specified hex value is a valid color.
     *
     * @param colorString The hex color to check (The '#' has already been added)
     * @param useAlpha    Whether this color uses ARGB
     */
    static boolean assertColorIsValid(String colorString, boolean useAlpha) {
        // If the color we're checking includes an alpha value, and color
        // less than 8 characters will automatically be invalid
        if (useAlpha && colorString.length() < 8) return false;
        try {
            Color.parseColor("#" + colorString);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    /**
     * Formats individual RGB values to be output as a HEX string.
     * <p>
     * Beware: If color value is lower than 0 or higher than 255, it's reset to 0.
     *
     * @param red   Red color value
     * @param green Green color value
     * @param blue  Blue color value
     * @return HEX String containing the three values
     */
    static String formatColorValues(
            @IntRange(from = 0, to = 255) int red,
            @IntRange(from = 0, to = 255) int green,
            @IntRange(from = 0, to = 255) int blue) {

        return String.format("%02X%02X%02X",
                assertColorValueInRange(red),
                assertColorValueInRange(green),
                assertColorValueInRange(blue)
        );
    }

    /**
     * Formats individual ARGB values to be output as an 8 character HEX string.
     * <p>
     * Beware: If any value is lower than 0 or higher than 255, it's reset to 0.
     *
     * @param alpha Alpha value
     * @param red   Red color value
     * @param green Green color value
     * @param blue  Blue color value
     * @return HEX String containing the three values
     * @since v1.1.0
     */
    static String formatColorValues(
            @IntRange(from = 0, to = 255) int alpha,
            @IntRange(from = 0, to = 255) int red,
            @IntRange(from = 0, to = 255) int green,
            @IntRange(from = 0, to = 255) int blue) {

        return String.format("%02X%02X%02X%02X",
                assertColorValueInRange(alpha),
                assertColorValueInRange(red),
                assertColorValueInRange(green),
                assertColorValueInRange(blue)
        );
    }
}