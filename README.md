# CanvasView

`CanvasView` is a customizable Android view that allows users to dynamically add, edit, and manipulate text elements on a canvas. It includes features for text styling, undo/redo operations, text alignment, font selection, and much more, offering a comprehensive interactive experience.

## Features

### 1. **Add and Edit Text**
- Add text dynamically to the canvas.
- Edit existing text via a dialog.
- Delete text from the canvas using the edit dialog.

### 2. **Text Styling**
- **Bold**: Toggle bold style for selected text.
- **Italic**: Toggle italic style for selected text.
- **Underline**: Toggle underline for selected text.
- **Center Alignment**: Center text both horizontally and vertically on the canvas.

### 3. **Font Management**
- Choose from multiple fonts using a popup font menu.
- Change the font for the selected text without affecting others.
- Set a default font for new text additions.

### 4. **Font Size Adjustment**
- **Slider**: Adjust font size using a smooth slider for precise control.
- **Plus/Minus Buttons**: Increment or decrement the font size step by step.
- Font size changes are reflected live on the canvas.

### 5. **Undo/Redo**
- Undo the last text operation, such as move, resize, or style change.
- Redo operations to restore undone actions.

### 6. **Text Movement**
- Drag and move text items across the canvas.
- Long-press functionality for entering text editing mode.

### 7. **Dynamic Interaction**
- **Long Click on Font Size Background**: Toggles between the slider and plus/minus buttons with a fade animation.
- Font size slider and buttons are mutually exclusive for better UX.

---

## How to Use

### Adding Text
1. Tap the **Add Text** button to add a new text item to the canvas.
2. A randomly generated identifier is appended to the text (`Hello <ID>`).

### Editing Text
1. **Select a Text**:
   - Tap on a text item to select it.
2. **Edit Text**:
   - Long-press on the selected text to open the edit dialog.
   - Modify the text and click "OK" to apply changes.
   - Optionally, delete the text using the **Delete** button in the dialog.

### Styling Text
1. **Bold/Italic/Underline**:
   - Use the **Bold**, **Italic**, and **Underline** buttons to toggle respective styles for the selected text.
2. **Center Alignment**:
   - Tap the **Center** button to align the selected text both horizontally and vertically on the canvas.

### Font Management
1. **Change Font**:
   - Tap the **Font Menu** button to open a popup with available fonts.
   - Select a font to apply it to the selected text or set it as the default font for new text.

### Adjusting Font Size
1. **Slider**:
   - Long-press the **Font Size Background** to reveal the font size slider.
   - Adjust the slider to change the font size of the selected text.
2. **Plus/Minus Buttons**:
   - Use the plus (`+`) and minus (`-`) buttons for stepwise font size adjustment.

### Undo/Redo
1. Tap the **Undo** button to reverse the last operation.
2. Tap the **Redo** button to restore an undone operation.

---

## Installation

### Step 1: Add `CanvasView` to Your Layout
```xml
<com.dark.canves.CanvasView
    android:id="@+id/canvas"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_margin="12dp" />
```

### Step 2: Initialize in Your Activity
```java
binding.canvas.addText("Hello World");
binding.canvas.setDefaultFont("mono");
```

## Known Issues

- **Baseline Centering**: When centering text, ensure proper `FontMetrics` calculations to avoid vertical misalignment.
- **Font Assets**: Ensure all font files are located in the `assets/fonts/` directory for seamless functionality.

---

## Contributing

Feel free to submit a pull request or open an issue for suggestions, bug reports, or feature requests.

---

## License

This project is licensed under the [MIT License](LICENSE).
