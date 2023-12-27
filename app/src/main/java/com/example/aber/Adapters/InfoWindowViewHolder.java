package com.example.aber.Adapters;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.ConfirmBookingFragment;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.imageview.ShapeableImageView;


public class InfoWindowViewHolder {
    private Place place;
    private ShapeableImageView image;
    private TextView nameTextView, addressTextView;
    private Button bookNowButton;

    public InfoWindowViewHolder(View itemView, Place place) {
        this.place = place;
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
                ConfirmBookingFragment fragment = new ConfirmBookingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", place.getName());
                bundle.putString("address", place.getAddress());
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = fragment.getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AndroidUtil.replaceFragment(fragment, fragmentManager, fragmentTransaction, R.id.fragment_main_container);
            }
        });
    }
}



