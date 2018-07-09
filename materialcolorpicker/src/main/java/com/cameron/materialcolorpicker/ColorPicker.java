package com.cameron.materialcolorpicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.cameron.materialcolorpicker.ColorFormatHelper.assertColorIsValid;
import static com.cameron.materialcolorpicker.ColorFormatHelper.assertColorValueInRange;
import static com.cameron.materialcolorpicker.ColorFormatHelper.formatColorValues;


/**
 * This is the only class of the project. It consists in a custom dialog that shows the GUI
 * used for choosing a color using three sliders or an input field.
 *
 * @author Simone Pessotto
 */
public class ColorPicker extends Dialog implements SeekBar.OnSeekBarChangeListener {

    private final Activity activity;

    private View colorView;
    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private EditText hexCode;
    private Button okColor;
    private ColorPickerCallback callback;
    private int alpha;
    private int red;
    private int green;
    private int blue;
    private boolean withAlpha = false;
    private boolean closeOnDialogButtonPressed = true;
    private boolean closeOnBackPressed = true;
    private boolean showButtonAsTransparent = false;
    private String buttonText = null;
    // Object wrappers are used so that we can determine
    // whether or not these values have been set
    private Integer buttonTextColor = null;
    private Integer buttonBackgroundColor = null;

    /**
     * Creator of the class. It will initialize the class with black color as default
     *
     * @param activity The reference to the activity where the color picker is called
     */
    public ColorPicker(Activity activity) {
        super(activity);

        this.activity = activity;

        if (activity instanceof ColorPickerCallback) {
            callback = (ColorPickerCallback) activity;
        }

        this.alpha = 255;
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    /**
     * Creator of the class. It will initialize the class with the rgb color passed as default
     *
     * @param activity The reference to the activity where the color picker is called
     * @param red      Red color for RGB values (0 - 255)
     * @param green    Green color for RGB values (0 - 255)
     * @param blue     Blue color for RGB values (0 - 255)
     *                 <p>
     *                 If the value of the colors it's not in the right range (0 - 255) it will
     *                 be set to 0.
     */
    public ColorPicker(Activity activity,
                       @IntRange(from = 0, to = 255) int red,
                       @IntRange(from = 0, to = 255) int green,
                       @IntRange(from = 0, to = 255) int blue) {
        this(activity);

        this.red = assertColorValueInRange(red);
        this.green = assertColorValueInRange(green);
        this.blue = assertColorValueInRange(blue);
    }

    /**
     * Creator of the class. It will initialize the class with the argb color passed as default
     *
     * @param activity The reference to the activity where the color picker is called
     * @param alpha    Alpha value (0 - 255)
     * @param red      Red color for RGB values (0 - 255)
     * @param green    Green color for RGB values (0 - 255)
     * @param blue     Blue color for RGB values (0 - 255)
     *                 <p>
     *                 If the value of the colors it's not in the right range (0 - 255) it will
     *                 be place at 0.
     * @since v1.1.0
     */
    public ColorPicker(Activity activity,
                       @IntRange(from = 0, to = 255) int alpha,
                       @IntRange(from = 0, to = 255) int red,
                       @IntRange(from = 0, to = 255) int green,
                       @IntRange(from = 0, to = 255) int blue) {
        this(activity);
        this.alpha = assertColorValueInRange(alpha);
        this.red = assertColorValueInRange(red);
        this.green = assertColorValueInRange(green);
        this.blue = assertColorValueInRange(blue);
        this.withAlpha = true;
    }

    /**
     * Enable or disable dismissal of the dialog when the dialog's button is pressed.
     * The default value is {@code true}.
     *
     * @param closeOnDialogButtonPressed Whether this dialog should close when the
     *                                   dialog's button is pressed.
     **/
    public ColorPicker setCloseOnDialogButtonPressed(boolean closeOnDialogButtonPressed) {
        this.closeOnDialogButtonPressed = closeOnDialogButtonPressed;
        return this;
    }

    /**
     * Enable or disable dismissal of the dialog when the back button is pressed.
     * The default value is {@code true}
     *
     * @param closeOnBackPressed Whether the dialog should close when the device's
     *                           back button is pressed.
     */
    public ColorPicker setCloseOnBackPressed(boolean closeOnBackPressed) {
        this.closeOnBackPressed = closeOnBackPressed;
        return this;
    }

    /**
     * Sets the callback for the dialog (the callback will be called when the dialog's button is pressed).
     *
     * @param listener The listener to be set for this dialog.
     */
    public ColorPicker setCallback(ColorPickerCallback listener) {
        callback = listener;
        return this;
    }

    /**
     * Sets the color of the dialog's color view. When set, the
     * dialog will show this color when{@link #show()} is called
     * (the SeekBars will reflect this value).
     *
     * @param color The color to be shown in the dialog.
     *
     */
    public ColorPicker setColor(@ColorInt int color) {
        alpha = Color.alpha(color);
        red = Color.red(color);
        green = Color.green(color);
        blue = Color.blue(color);
        return this;
    }

    /**
     * Sets the text of the dialog button.
     * The default value is {@code "SUBMIT"}.
     *
     * @param buttonText The text to be shown on the dialog's button.
     */
    public ColorPicker setDialogButtonText(String buttonText) {
        // The view cannot be changed before onCreate has been called so we'll
        // simply store the text as a string so the text can be changed after
        // the views have been initialized
        this.buttonText = buttonText;
        try {
            okColor.setText(buttonText);
        } catch (NullPointerException ignored) {
            // The text of the button cannot be changed
            // before setContentView is called. By catching this error,
            // we can set the text of the button after the dialog
            // has been created, as well as before
        }
        return this;
    }

    /**
     * Sets the text color of the dialog's button.
     *
     * @param buttonTextColor The text color to be used for the dialog's button.
     */
    public ColorPicker setDialogButtonTextColor(@ColorInt int buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
        try {
            okColor.setTextColor(buttonTextColor);
        } catch (NullPointerException ignored) {
            // Same reason as above
        }
        return this;
    }

    /**
     * Sets the background color of the dialog's button.
     *
     * @param buttonBackgroundColor The background color of the dialog's button.
     */
    public ColorPicker setDialogButtonBackgroundColor(@ColorInt int buttonBackgroundColor) {
        this.buttonBackgroundColor = buttonBackgroundColor;
        try {
            okColor.setBackgroundColor(buttonBackgroundColor);
        } catch (NullPointerException ignored) {
            // Still same reason
        }
        return this;
    }

    /**
     * Set whether or not the button should be transparent.
     * The default value is {@code false}
     *
     * @param transparentButton Whether thie dialog's button should show as transparent
     */
    public ColorPicker showButtonAsTransparent(boolean transparentButton) {
        this.showButtonAsTransparent = transparentButton;
        return this;
    }

    /**
     * Getter for the ALPHA value of the ARGB selected color
     *
     * @return ALPHA Value Integer (0 - 255)
     * @since v1.1.0
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Getter for the RED value of the RGB selected color
     *
     * @return RED Value Integer (0 - 255)
     */
    public int getRed() {
        return red;
    }

    /**
     * Getter for the GREEN value of the RGB selected color
     *
     * @return GREEN Value Integer (0 - 255)
     */
    public int getGreen() {
        return green;
    }


    /**
     * Getter for the BLUE value of the RGB selected color
     *
     * @return BLUE Value Integer (0 - 255)
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Getter for the color as Android Color class value.
     * <p>
     * From Android Reference: The Color class defines methods for creating and converting color
     * ints.
     * Colors are represented as packed ints, made up of 4 bytes: alpha, red, green, blue.
     * The values are unpremultiplied, meaning any transparency is stored solely in the alpha
     * component, and not in the color components.
     *
     * @return Selected color as Android Color class value.
     */
    public int getColor() {
        return withAlpha ? Color.argb(alpha, red, green, blue) : Color.rgb(red, green, blue);
    }

    private void initUi() {
        colorView.setBackgroundColor(getColor());

        alphaSeekBar.setProgress(alpha);
        redSeekBar.setProgress(red);
        greenSeekBar.setProgress(green);
        blueSeekBar.setProgress(blue);

        if (!withAlpha) {
            alphaSeekBar.setVisibility(View.GONE);
        }

        hexCode.setText(withAlpha
                ? formatColorValues(alpha, red, green, blue)
                : formatColorValues(red, green, blue)
        );
    }

    private void sendColor() {
        if (callback != null) {
            String hex = String.format("#%08X", getColor());
            String hexNoAlpha = String.format("#%06X", (0xFFFFFF & getColor()));
            callback.onColorChosen(getColor(), hex, hexNoAlpha);
        }
        if (closeOnDialogButtonPressed) {
            dismiss();
        }
    }

    /**
     * Method that synchronizes the color between the bars, the view, and the HEX code text.
     *
     * @param input HEX Code of the color.
     */
    private void updateColorView(String input) {
        try {
            final int color = Color.parseColor('#' + input);
            alpha = Color.alpha(color);
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);

            colorView.setBackgroundColor(getColor());

            alphaSeekBar.setProgress(alpha);
            redSeekBar.setProgress(red);
            greenSeekBar.setProgress(green);
            blueSeekBar.setProgress(blue);
        } catch (IllegalArgumentException ignored) {
            hexCode.setError(activity.getResources().getText(R.string.materialcolorpicker__errHex));
        }
    }

    /**
     * Simple onCreate function. Here there is the init of the GUI.
     *
     * @param savedInstanceState As usual ...
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        setContentView(R.layout.materialcolorpicker__layout_color_picker);

        colorView = findViewById(R.id.colorView);
        hexCode = findViewById(R.id.hexCode);
        alphaSeekBar = findViewById(R.id.alphaSeekBar);
        redSeekBar = findViewById(R.id.redSeekBar);
        greenSeekBar = findViewById(R.id.greenSeekBar);
        blueSeekBar = findViewById(R.id.blueSeekBar);

        alphaSeekBar.setOnSeekBarChangeListener(this);
        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);

        hexCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(withAlpha ? 8 : 6)});

        hexCode.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    updateColorView(v.getText().toString());
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(hexCode.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
        // Set the color of the colorView when the
        // text from the edit text is a valid color.
        // This way, the colorView updates in real time
        hexCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    // Once we know the color is valid, we can then update the color view
                    if (assertColorIsValid(s.toString(), withAlpha)) {
                        updateColorView(s.toString());
                    }
                } catch (IllegalArgumentException | StringIndexOutOfBoundsException ignored) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        okColor = findViewById(R.id.okColorButton);
        if (buttonText != null) okColor.setText(buttonText);
        if (showButtonAsTransparent) okColor.setBackgroundColor(Color.TRANSPARENT);
        if (buttonTextColor != null) okColor.setTextColor(buttonTextColor);
        if (buttonBackgroundColor != null) okColor.setBackgroundColor(buttonBackgroundColor);

        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendColor();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isShowing()) {
            if (closeOnBackPressed) {
                dismiss();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Method called when the user change the value of the bars. This sync the colors.
     *
     * @param seekBar  SeekBar that has changed
     * @param progress The new progress value
     * @param fromUser Whether the user is the reason for the method call
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.alphaSeekBar) alpha = progress;
        else if (seekBar.getId() == R.id.redSeekBar) red = progress;
        else if (seekBar.getId() == R.id.greenSeekBar) green = progress;
        else if (seekBar.getId() == R.id.blueSeekBar) blue = progress;

        colorView.setBackgroundColor(getColor());
        if (callback != null) {
            callback.onColorChanged(getColor(), formatColorValues(red, green, blue), formatColorValues(alpha, red, green, blue));
        }

        //Setting the inputText hex color
        hexCode.setText(withAlpha
                ? formatColorValues(alpha, red, green, blue)
                : formatColorValues(red, green, blue));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void show() {
        super.show();
        initUi();
    }
}