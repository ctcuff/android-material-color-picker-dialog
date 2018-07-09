package com.cameron.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cameron.materialcolorpicker.ColorPicker;
import com.cameron.materialcolorpicker.ColorPickerCallback;

public class MainActivity extends AppCompatActivity implements ColorPickerCallback {

    private final String COLOR_VALUE = "colorValue";
    private ColorPicker colorPicker;
    private View colorView;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.toolbar_title));
        Button openColorDialog = findViewById(R.id.open_color_picker);

        int defaultColor = ContextCompat.getColor(this, R.color.colorPrimary);
        currentColor = savedInstanceState != null ? savedInstanceState.getInt(COLOR_VALUE) : defaultColor;

        colorView = findViewById(R.id.color_image);
        colorPicker = new ColorPicker(
                this,   // Context
                0,      // Default Alpha value, this can be omitted
                0,      // Default Red value
                0,      // Default Green value
                0       // Default Blue value
        );

        // Various configurations, all of the below are optional
        colorPicker.setCloseOnDialogButtonPressed(true)
                .setDialogButtonText("CONFIRM")
                .setCloseOnBackPressed(false)
                .showButtonAsTransparent(true)
                // Since this activity already implements the ColorPickerCallback,
                // this last configuration is technically unnecessary
                .setCallback(this);

        // The dialog will be reset on orientation change. This is an
        // example of how to retain the color value in such a case
        colorPicker.setColor(savedInstanceState == null ? defaultColor : currentColor);
        colorView.setBackgroundColor(savedInstanceState == null ? defaultColor : currentColor);

        openColorDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPicker.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COLOR_VALUE, currentColor);
    }

    /**
     * Thanks to android's window leaks, we need to dismiss the
     * dialog when the device is rotated
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (colorPicker != null) colorPicker.dismiss();
    }

    /**
     * One way to get values from the Color Picker is by implementing the
     * {@link ColorPickerCallback} on a class level, as can be seen here.
     */
    @Override
    public void onColorChosen(@ColorInt int color, String hex, String hexNoAlpha) {
        Log.d("Pure color", String.valueOf(color));
        Log.d("Alpha", Integer.toString(Color.alpha(color)));
        Log.d("Red", Integer.toString(Color.red(color)));
        Log.d("Green", Integer.toString(Color.green(color)));
        Log.d("Blue", Integer.toString(Color.blue(color)));

        Log.d("Hex with alpha", hex);
        Log.d("Hex no alpha", hexNoAlpha);
        // Once the dialog's select button has been pressed, we
        // can get the selected color and use it for the
        // background of our view
        colorView.setBackgroundColor(color);
        Toast.makeText(this, "ARGB: " + hex + " | RGB: " + hexNoAlpha, Toast.LENGTH_LONG).show();
    }

    /**
     * When the color values from the dialog are changed, this method will
     * be called. Here, we'll just change the color of the dialog's button.
     */
    @Override
    public void onColorChanged(@ColorInt int color, String hex, String hexNoAlpha) {
        Log.d("Color", String.valueOf(color));
        Log.d("Hex", hex);
        Log.d("Hex no alpha", hexNoAlpha);
        // Save the color selected so we can retrieve it again
        // when the device is rotated
        currentColor = color;
        colorPicker.setDialogButtonTextColor(color);
    }
}
