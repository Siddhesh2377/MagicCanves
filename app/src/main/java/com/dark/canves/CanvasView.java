package com.dark.canves;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CanvasView extends View {

    public static TextData currentSelectedText = null;
    private final ArrayList<TextData> dataList = new ArrayList<>();
    private final Stack<ArrayList<TextData>> undoStack = new Stack<>();
    private final Stack<ArrayList<TextData>> redoStack = new Stack<>();
    private float offsetX, offsetY;
    private Paint defaultPaint;
    private float fontSize;
    private float defaultTextSize;
    private Typeface defaultTypeface;
    private GestureDetector gestureDetector;
    private OnCanvasTouch onCanvasTouch;
    private boolean touchOnCanvas = true;

    @SuppressLint("ClickableViewAccessibility")
    private final OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            gestureDetector.onTouchEvent(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentSelectedText = findTextUnderTouch(x, y);
                    if (currentSelectedText != null) {
                        offsetX = x - currentSelectedText.x;
                        offsetY = y - currentSelectedText.y;
                        if (onCanvasTouch != null) {
                            onCanvasTouch.onTextClick(currentSelectedText);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (currentSelectedText != null) {
                        currentSelectedText.x = x - offsetX;
                        currentSelectedText.y = y - offsetY;
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (currentSelectedText != null) {
                        pushToUndoStack();
                        if (touchOnCanvas) {
                            currentSelectedText.paint.setColor(
                                    ContextCompat.getColor(getContext(), R.color.txt_color)
                            );
                        }
                    }
                    break;
            }
            return true;
        }
    };

    public CanvasView(Context context) {
        super(context);
        init(context);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_canves));
        setElevation(4);
        defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultPaint.setColor(Color.BLACK);
        fontSize = dp(context, 24);
        defaultPaint.setTextSize(fontSize);
        defaultTypeface = Typeface.SANS_SERIF;
        defaultTextSize = dp(context, 24);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                float x = e.getX();
                float y = e.getY();
                TextData longPressedText = findTextUnderTouch(x, y);
                if (longPressedText != null) {
                    Toast.makeText(getContext(), "Long-pressed text: " + longPressedText.text, Toast.LENGTH_SHORT).show();
                    if (onCanvasTouch != null) {
                        onCanvasTouch.onRequestEdit(longPressedText);
                    }
                }
            }
        });
        setOnTouchListener(touchListener);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (TextData td : dataList) {
            if (td.paint != null && td.text != null) {
                canvas.drawText(td.text, td.x, td.y, td.paint);
            }
        }
    }

    public void addText(String newText) {
        pushToUndoStack();
        TextData textData = new TextData(getContext());
        textData.text = newText;
        textData.x = 100f;
        textData.y = 100f;
        textData.font = null;
        textData.paint.setColor(defaultPaint.getColor());
        textData.paint.setTextSize(defaultPaint.getTextSize());
        textData.paint.setTypeface(defaultTypeface);
        dataList.add(textData);
        currentSelectedText = null;
        invalidate();
    }

    public void addText(String newText, String fontFileName) {
        pushToUndoStack();
        TextData textData = new TextData(getContext());
        textData.text = newText;
        textData.x = 100f;
        textData.y = 100f;
        textData.font = fontFileName;
        textData.paint.setColor(defaultPaint.getColor());
        textData.paint.setTextSize(defaultPaint.getTextSize());
        try {
            Typeface localTf = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontFileName + ".ttf");
            textData.paint.setTypeface(localTf);
        } catch (Exception e) {
            textData.paint.setTypeface(defaultTypeface);
        }
        dataList.add(textData);
        currentSelectedText = null;
        invalidate();
    }

    public void setDefaultColor(int color) {
        defaultPaint.setColor(color);
    }

    public void setDefaultTextSize(float sizeDp) {
        defaultTextSize = dp(getContext(), sizeDp);
        defaultPaint.setTextSize(defaultTextSize);
    }

    public void setDefaultFont(String fontFileNameWithoutExtension) {
        try {
            defaultTypeface = Typeface.createFromAsset(
                    getContext().getAssets(),
                    "fonts/" + fontFileNameWithoutExtension + ".ttf"
            );
        } catch (Exception e) {
            Log.e("CanvasView", "Failed to set default font: " + e.getMessage());
            defaultTypeface = Typeface.SANS_SERIF;
        }
    }

    public void changeFontForSelected(String fontFileName) {
        if (currentSelectedText != null) {
            pushToUndoStack();
            currentSelectedText.font = fontFileName;
            try {
                Typeface newTf = Typeface.createFromAsset(
                        getContext().getAssets(),
                        "fonts/" + fontFileName + ".ttf"
                );
                currentSelectedText.paint.setTypeface(newTf);
            } catch (Exception e) {
                currentSelectedText.paint.setTypeface(defaultTypeface);
            }
            invalidate();
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(cloneDataList(dataList));
            ArrayList<TextData> previousState = undoStack.pop();
            dataList.clear();
            dataList.addAll(previousState);
            invalidate();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(cloneDataList(dataList));
            ArrayList<TextData> nextState = redoStack.pop();
            dataList.clear();
            dataList.addAll(nextState);
            invalidate();
        }
    }

    public void delete(TextData data) {
        pushToUndoStack();
        dataList.remove(data);
        invalidate();
    }

    private void pushToUndoStack() {
        undoStack.push(cloneDataList(dataList));
        redoStack.clear();
    }

    private ArrayList<TextData> cloneDataList(List<TextData> original) {
        ArrayList<TextData> cloned = new ArrayList<>();
        for (TextData td : original) {
            TextData copy = new TextData(getContext());
            copy.x = td.x;
            copy.y = td.y;
            copy.text = td.text;
            copy.font = td.font;
            copy.bold = td.bold;
            copy.italic = td.italic;
            copy.underline = td.underline;
            copy.font = td.font;
            copy.paint = new Paint(td.paint);
            cloned.add(copy);
        }
        return cloned;
    }

    private TextData findTextUnderTouch(float x, float y) {
        for (int i = dataList.size() - 1; i >= 0; i--) {
            TextData td = dataList.get(i);
            if (td.text == null || td.text.isEmpty()) continue;
            float textWidth = td.paint.measureText(td.text);
            float textHeight = td.paint.getTextSize();
            if (x >= td.x && x <= td.x + textWidth && y <= td.y && y >= td.y - textHeight) {
                touchOnCanvas = false;
                return td;
            }
            touchOnCanvas = true;
        }
        touchOnCanvas = true;
        return null;
    }

    public void setOnTextEditRequestListener(OnCanvasTouch listener) {
        this.onCanvasTouch = listener;
    }

    public void updateText(TextData data) {
        for (TextData d : dataList) {
            if (data.id == d.id) d.text = data.text;
        }
        pushToUndoStack();
        invalidate();
    }

    public float getFontSize() {
        if (currentSelectedText != null) {
            return currentSelectedText.paint.getTextSize();
        } else {
            return fontSize;
        }
    }

    public void setFontSize(float size) {
        if (currentSelectedText != null) {
            currentSelectedText.paint.setTextSize(size);
        } else {
            fontSize = size;
            defaultPaint.setTextSize(size);
        }
        pushToUndoStack();
        invalidate();
    }

    public void setBold(boolean enable) {
        if (currentSelectedText != null) {
            currentSelectedText.bold = enable;
            updateTypefaceForSelected();
            pushToUndoStack();
            invalidate();
        }
    }

    public void setItalic(boolean enable) {
        if (currentSelectedText != null) {
            currentSelectedText.italic = enable;
            updateTypefaceForSelected();
            pushToUndoStack();
            invalidate();
        }
    }

    public void setUnderline(boolean enable) {
        if (currentSelectedText != null) {
            currentSelectedText.underline = enable;
            currentSelectedText.paint.setUnderlineText(enable);
            pushToUndoStack();
            invalidate();
        }
    }

    public void setTextCenter() {
        if (currentSelectedText != null) {
            currentSelectedText.paint.setTextAlign(Paint.Align.CENTER);
        } else {
            if (!dataList.isEmpty())
                currentSelectedText = dataList.get(0);
            else
                return;
        }

        // Get the midpoint of the view's width and height
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Calculate the vertical offset so the text is visually centered.
        // The baseline is at y, so we shift by half the text height.
        Paint.FontMetrics fontMetrics = currentSelectedText.paint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        float verticalOffset = (textHeight / 2f) - fontMetrics.descent;

        // Update the text coordinates
        currentSelectedText.x = centerX;
        currentSelectedText.y = centerY + verticalOffset;

        pushToUndoStack();
        invalidate();
    }


    private void updateTypefaceForSelected() {
        if (currentSelectedText == null) return;
        int style = Typeface.NORMAL;
        if (currentSelectedText.bold && currentSelectedText.italic) {
            style = Typeface.BOLD_ITALIC;
        } else if (currentSelectedText.bold) {
            style = Typeface.BOLD;
        } else if (currentSelectedText.italic) {
            style = Typeface.ITALIC;
        }
        String fileName = currentSelectedText.font;
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = "sans";
            }
            Typeface baseTf = Typeface.createFromAsset(
                    getContext().getAssets(),
                    "fonts/" + fileName + ".ttf"
            );
            currentSelectedText.paint.setTypeface(Typeface.create(baseTf, style));
        } catch (Exception e) {
            currentSelectedText.paint.setTypeface(Typeface.create(defaultTypeface, style));
        }
        invalidate();
    }

    private float dp(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
