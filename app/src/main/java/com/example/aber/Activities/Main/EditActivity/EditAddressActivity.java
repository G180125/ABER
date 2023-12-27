package com.example.aber.Activities.Main.EditActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.aber.R;

public class EditAddressActivity extends AppCompatActivity {
    private ImageView buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(v -> finish());
    }
}