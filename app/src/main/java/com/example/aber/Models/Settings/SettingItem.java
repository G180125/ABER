package com.example.aber.Models.Settings;

public class SettingItem {
    private String text;
    private int drawableResId;

    public SettingItem(String text, int drawableResId) {
        this.text = text;
        this.drawableResId = drawableResId;
    }

    public String getText() {
        return text;
    }

    public int getDrawableResId() {
        return drawableResId;
    }
}

