package com.example.aber.Adapters;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.R;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.imageview.ShapeableImageView;


public class InfoWindowViewHolder {
    private ShapeableImageView image;
    private TextView nameTextView, addressTextView;
    private Button bookNowButton;

    public InfoWindowViewHolder(View itemView) {
        nameTextView = itemView.findViewById(R.id.textViewName);
        addressTextView = itemView.findViewById(R.id.textViewAddress);
        bookNowButton = itemView.findViewById(R.id.book_button);
    }

    public void bind(String name, String address) {
        nameTextView.setText(name);
        addressTextView.setText(address);

        bookNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click action
            }
        });
    }
}



