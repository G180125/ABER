package com.example.aber.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aber.Models.Settings.SettingItem;
import com.example.aber.R;

import java.util.List;

public class SettingsAdapter extends ArrayAdapter<SettingItem> {

    public SettingsAdapter(Context context, List<SettingItem> settingsList) {
        super(context, 0, settingsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_setting, parent, false);
        }

        SettingItem settingItem = getItem(position);

        if (settingItem != null) {
            TextView settingText = convertView.findViewById(R.id.setting_text);
            ImageView settingIcon = convertView.findViewById(R.id.icon);

            settingText.setText(settingItem.getText());
            settingIcon.setImageResource(settingItem.getDrawableResId());
        }

        return convertView;
    }
}
