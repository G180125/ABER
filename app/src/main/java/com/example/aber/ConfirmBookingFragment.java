package com.example.aber;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.aber.Utils.AndroidUtil;

public class ConfirmBookingFragment extends Fragment {
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private TextView nameTextView, addressTextView;
    private RadioButton bookNowRadioButton;
    private CardView bookingTimeCardView;
    private ImageView backButton;
    private String name, address;
    private boolean check;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        AndroidUtil.showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_confirm_booking, container, false);
        firebaseManager = new FirebaseManager();
        check = true;

        backButton = root.findViewById(R.id.back);
        nameTextView = root.findViewById(R.id.destination_name);
        addressTextView = root.findViewById(R.id.destination_address);
        bookNowRadioButton = root.findViewById(R.id.book_now_button);
        bookingTimeCardView = root.findViewById(R.id.booking_time_card_view);

        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("name","");
            address = args.getString("address","");
            updateUI(name, address, check);
        }

        bookNowRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = !check;
                setRadioButton(check);
            }
        });

        return root;
    }

    private void updateUI(String name, String address, boolean check){
        nameTextView.setText(name);
        addressTextView.setText(address);

        setRadioButton(check);
    }

    private void setRadioButton(boolean check){
        bookNowRadioButton.setChecked(check);

        if(bookNowRadioButton.isChecked()){
            bookingTimeCardView.setVisibility(View.GONE);
        } else {
            bookingTimeCardView.setVisibility(View.VISIBLE);
        }
    }
}