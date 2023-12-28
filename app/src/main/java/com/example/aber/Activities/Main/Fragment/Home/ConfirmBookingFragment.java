package com.example.aber.Activities.Main.Fragment.Home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.aber.FirebaseManager;
import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.Booking.Card;
import com.example.aber.Models.Booking.Payment;
import com.example.aber.Models.Booking.PaymentStatus;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.SOS;
import com.example.aber.Models.User.User;
import com.example.aber.Models.User.Vehicle;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

import java.util.Objects;

public class ConfirmBookingFragment extends Fragment {
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private TextView nameTextView, destinationAddressTextView, homeAddressTextView, plateTextview, sosTextView;
    private RadioButton bookNowRadioButton;
    private CardView bookingTimeCardView, homeCardView, vehicleCardView, sosCardView, cardCardView;
    private ImageView backButton;
    private TimePicker timePicker;
    private String id, name, address;
    private Button nextButton;
    private boolean check;
    private User currentUser;
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
        destinationAddressTextView = root.findViewById(R.id.destination_address);
        bookNowRadioButton = root.findViewById(R.id.book_now_button);
        bookingTimeCardView = root.findViewById(R.id.booking_time_card_view);
        timePicker = root.findViewById(R.id.time_picker);
        homeAddressTextView = root.findViewById(R.id.address);
        plateTextview = root.findViewById(R.id.plate);
        sosTextView = root.findViewById(R.id.sos_name);
        homeCardView = root.findViewById(R.id.home_card_view);
        vehicleCardView = root.findViewById(R.id.vehicle_card_view);
        sosCardView = root.findViewById(R.id.sos_card_view);
        cardCardView = root.findViewById(R.id.card_card_view);
        nextButton = root.findViewById(R.id.next_button);

        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("name","");
            address = args.getString("address","");
        }

        id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseManager.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                currentUser = object;
                updateUI(name, address, check, currentUser);
            }

            @Override
            public void onFetchFailure(String message) {
                AndroidUtil.showToast(requireContext(), message);
                AndroidUtil.hideLoadingDialog(progressDialog);
            }
        });

        bookNowRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = !check;
                setRadioButton(check);
            }
        });

        homeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showToast(requireContext(), "Home card view is clicked");
            }
        });

        vehicleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showToast(requireContext(), "Vehicle card view is clicked");
            }
        });

        sosCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showToast(requireContext(), "SOS card view is clicked");
            }
        });

        cardCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showToast(requireContext(), "Card card view is clicked");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showLoadingDialog(progressDialog);
                String bookingTime = getTimeFromPicker();
                Home home = currentUser.getHomes().get(0);
                Payment payment = new Payment("id", 100000.0, "VND", PaymentStatus.PROCESSING, new Card());
                SOS sos;
                if (!currentUser.getEmergencyContacts().isEmpty()){
                    sos = currentUser.getEmergencyContacts().get(0);
                } else {
                    sos = new SOS();
                }
                Vehicle vehicle = currentUser.getVehicles().get(0);

                Booking booking = new Booking(address, home, "ETA", bookingTime, "", "", payment, sos, vehicle);

                currentUser.getBookings().add(booking);
                firebaseManager.updateUser(id, currentUser, new FirebaseManager.OnTaskCompleteListener() {
                    @Override
                    public void onTaskSuccess(String message) {
                        AndroidUtil.showToast(requireContext(), "Booking Successfully");
                        AndroidUtil.hideLoadingDialog(progressDialog);
                        navigateToMatchingDriver(booking);
                    }

                    @Override
                    public void onTaskFailure(String message) {
                        AndroidUtil.showToast(requireContext(), message);
                        AndroidUtil.hideLoadingDialog(progressDialog);
                    }
                });
            }
        });

        return root;
    }

    private void updateUI(String name, String address, boolean check, User user){
        nameTextView.setText(name);
        destinationAddressTextView.setText(address);

        setRadioButton(check);

        homeAddressTextView.setText(user.getHomes().get(0).getAddress());
        plateTextview.setText(user.getVehicles().get(0).getNumberPlate());
        if(!user.getEmergencyContacts().isEmpty()) {
            sosTextView.setText(user.getEmergencyContacts().get(0).getName());
        }

        AndroidUtil.hideLoadingDialog(progressDialog);
    }

    private void setRadioButton(boolean check){
        bookNowRadioButton.setChecked(check);

        if(bookNowRadioButton.isChecked()){
            bookingTimeCardView.setVisibility(View.GONE);
        } else {
            bookingTimeCardView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("DefaultLocale")
    private String getTimeFromPicker() {
        int hour, minute;

        hour = timePicker.getHour();
        minute = timePicker.getMinute();

        String amPm;

        if (hour >= 12) {
            amPm = "PM";
            if (hour > 12) {
                hour -= 12;
            }
        } else {
            amPm = "AM";
            if (hour == 0) {
                hour = 12;
            }
        }
        return String.format("%02d:%02d %s", hour, minute, amPm);
    }

    private void navigateToMatchingDriver(Booking booking){
        MatchingDriverFragment fragment = new MatchingDriverFragment();
        Bundle bundle = new Bundle();
        bundle.putString("bookingID", booking.getId());
        bundle.putString("name", name);
        bundle.putString("address", address);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AndroidUtil.replaceFragment(fragment, fragmentManager, fragmentTransaction, R.id.fragment_main_container);
    }
}