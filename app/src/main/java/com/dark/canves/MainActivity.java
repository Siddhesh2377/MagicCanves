package com.dark.canves;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dark.canves.databinding.ActivityMainBinding;
import com.dark.canves.databinding.DialogInputTextBinding;
import com.dark.canves.databinding.PopupSpinnerBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderlined = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        binding.canvas.setOnTextEditRequestListener(new OnCanvasTouch() {
            @Override
            public void onRequestEdit(TextData data) {
                showAddTextDialog(data);
            }

            @Override
            public void onTextClick(TextData data) {
                binding.fontSizeSlider.setValue(data.paint.getTextSize());
            }
        });

        binding.openFontMenu.setOnClickListener(v -> showPopupWindow());

        binding.sizeResult.setText(String.valueOf(px(binding.canvas.getFontSize())));

        binding.minus.setOnClickListener(v -> {
            float size = binding.canvas.getFontSize();
            binding.canvas.setFontSize(size - 1f);
            binding.sizeResult.setText(String.valueOf(px(binding.canvas.getFontSize())));
        });

        binding.plus.setOnClickListener(v -> {
            float size = binding.canvas.getFontSize();
            binding.canvas.setFontSize(size + 1f);
            binding.sizeResult.setText(String.valueOf(px(binding.canvas.getFontSize())));
        });

        binding.addText.setOnClickListener(v -> {
            binding.canvas.addText("Hello");
        });

        binding.undo.setOnClickListener(v -> binding.canvas.undo());

        binding.redo.setOnClickListener(v -> binding.canvas.redo());

        binding.bold.setOnClickListener(v -> {
            isBold = !isBold;
            binding.canvas.setBold(isBold);
        });

        binding.italic.setOnClickListener(v -> {
            isItalic = !isItalic;
            binding.canvas.setItalic(isItalic);
        });

        binding.underline.setOnClickListener(v -> {
            isUnderlined = !isUnderlined;
            binding.canvas.setUnderline(isUnderlined);
        });

        binding.center.setOnClickListener(v -> binding.canvas.setTextCenter());

        binding.fontSizeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                binding.canvas.setFontSize(slider.getValue());
                binding.sizeResult.setText(String.valueOf(px(binding.canvas.getFontSize())));
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                binding.canvas.setFontSize(slider.getValue());
                binding.sizeResult.setText(String.valueOf(px(binding.canvas.getFontSize())));
            }
        });

        binding.fontSizeBg.setOnLongClickListener(v -> {
            if (binding.fontSizeSlider.getVisibility() == View.GONE) {
                binding.fontSizeSlider.setAlpha(0f);
                binding.fontSizeSlider.setVisibility(View.VISIBLE);
                binding.fontSizeSlider.animate().alpha(1f).setDuration(300).start();
                binding.plus.animate().alpha(0f).setDuration(300)
                        .withEndAction(() -> binding.plus.setVisibility(View.GONE)).start();
                binding.minus.animate().alpha(0f).setDuration(300)
                        .withEndAction(() -> binding.minus.setVisibility(View.GONE)).start();
            } else {
                binding.fontSizeSlider.animate().alpha(0f).setDuration(300)
                        .withEndAction(() -> binding.fontSizeSlider.setVisibility(View.GONE)).start();
                binding.plus.setAlpha(0f);
                binding.plus.setVisibility(View.VISIBLE);
                binding.plus.animate().alpha(1f).setDuration(300).start();
                binding.minus.setAlpha(0f);
                binding.minus.setVisibility(View.VISIBLE);
                binding.minus.animate().alpha(1f).setDuration(300).start();
            }
            return true;
        });
    }

    public void showAddTextDialog(TextData data) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        DialogInputTextBinding dialogInputTextBinding = DialogInputTextBinding.inflate(getLayoutInflater());
        View dialogView = dialogInputTextBinding.getRoot();
        dialogInputTextBinding.textInputEditText.setText(data.text);

        builder.setTitle("Add Text")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String typedText = Objects.requireNonNull(dialogInputTextBinding.textInputEditText.getText())
                            .toString()
                            .trim();
                    if (!typedText.isEmpty()) {
                        data.text = typedText;
                        binding.canvas.updateText(data);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Delete", (dialog, which) -> binding.canvas.delete(data))
                .show();
    }

    private int dp(float dipValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private int px(float px) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, metrics);
    }

    private void showPopupWindow() {
        PopupSpinnerBinding popupSpinnerBinding = PopupSpinnerBinding.inflate(getLayoutInflater());
        View popupView = popupSpinnerBinding.getRoot();
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                dp(150),
                dp(200),
                true
        );

        String[] items = {"cup", "mono", "mono_italic", "mono_bold_italic", "mono_bold", "slate", "slate_dark", "plane_slate"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        popupSpinnerBinding.list.setAdapter(adapter);

        popupSpinnerBinding.list.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = items[position];
            if (CanvasView.currentSelectedText == null) {
                binding.canvas.setDefaultFont(selectedItem);
            } else {
                binding.canvas.changeFontForSelected(selectedItem);
            }
            binding.currentFont.setText(selectedItem);
            Toast.makeText(this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAsDropDown(findViewById(R.id.canvas), 70, -420);
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        binding.canvas.setDefaultFont("cup");
        binding.canvas.setFontSize(dp(44));
        binding.canvas.addText("Hello");
        binding.currentFont.setText("cup");
        new Handler(getMainLooper()).postDelayed(() -> binding.canvas.setTextCenter(), 400);
        new Handler(getMainLooper()).postDelayed(() -> binding.canvas.setTextCenter(), 100);
    }
}
