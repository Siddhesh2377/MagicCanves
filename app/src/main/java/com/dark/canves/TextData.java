package com.dark.canves;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Stores data about a single text item, including style, coordinates, and the text itself.
 */
public class TextData {
    public int id = 0;
    public Paint paint;   // Paint configuration
    public float x, y;    // Coordinates: X, Y
    public String text;   // Actual string to display
    public String font;   // Font name (without .ttf extension)
    public boolean bold;
    public boolean italic;
    public boolean underline;

    public TextData(Context context) {
        id++;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(dp(context, 24)); // default size
        // You could also set font, bold, italic, etc. defaults here.
    }

    private float dp(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
