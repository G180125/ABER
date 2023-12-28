package com.example.aber.Activities.Main.Fragment.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.aber.FirebaseManager;
import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.User.User;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

import java.util.Objects;

public class MatchingDriverFragment extends Fragment {
    private String bookingID, id, name, address;
    private ProgressBar progressBar;
    private Button cancelButton;
    private User user;
    private FirebaseManager firebaseManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_matching_driver, container, false);
        firebaseManager = new FirebaseManager();

        Bundle args = getArguments();
        if(args != null){
            bookingID = args.getString("bookingID", "");
            Log.d("BookingID", bookingID);
            name = args.getString("name","");
            address = args.getString("address","");
        }

        id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseManager.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                user = object;
            }

            @Override
            public void onFetchFailure(String message) {
                AndroidUtil.showToast(requireContext(), message);
            }
        });

        progressBar = root.findViewById(R.id.progress_bar);
        cancelButton = root.findViewById(R.id.cancel_button);

        progressBar.setVisibility(View.VISIBLE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               cancelBookingByID(user, bookingID);
                firebaseManager.updateUser(id, user, new FirebaseManager.OnTaskCompleteListener() {
                    @Override
                    public void onTaskSuccess(String message) {
                        AndroidUtil.showToast(requireContext(), "Booking Cancelled");
                        backToConfirmBookingFragment();
                    }

                    @Override
                    public void onTaskFailure(String message) {
                        AndroidUtil.showToast(requireContext(), message);
                    }
                });
            }
        });

        return root;
    }

    private void cancelBookingByID(User user, String bookingID){
        for(Booking booking: user.getBookings()){
            if(booking.getId().equals(bookingID)){
                booking.setStatus("Cancel");
            }
        }
    }

    private void backToConfirmBookingFragment(){
        ConfirmBookingFragment fragment = new ConfirmBookingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("address", address);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AndroidUtil.replaceFragment(fragment, fragmentManager, fragmentTransaction, R.id.fragment_main_container);
    }
}