package com.example.aber.Activities.Main.Fragment;

import static com.example.aber.Utils.AndroidUtil.hideLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aber.Activities.Main.Fragment.Home.MainHomeFragment;
import com.example.aber.Activities.Main.Fragment.Profile.HelpActivity;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.User.User;
import com.example.aber.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingDetailFragment extends Fragment {
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private User user;
    private Booking booking;
    private String bookingID;
    private TextView pickUpTextView, destinationTextView, bookingTimeTextView, statusTextView, brandTextView, vehicleNameTextView, colorTextView, seatTextView, plateTextView, amountTextView, methodTextView, driverNameTextView, driverGenderTextView, licenseNumberTextView, realPickUpTimeTextView;
    private CircleImageView avatar;
    private ImageView backButton, imageVIew;
    private Button helpButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        firebaseManager = new FirebaseManager();

        Bundle args = getArguments();
        if (args != null) {
            bookingID = args.getString("bookingID", "");
        }

        String id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseManager.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                user = object;
                for (Booking bookingInList: user.getBookings()){
                    if(bookingID.equals(bookingInList.getId())){
                        booking = bookingInList;
                        updateUI(booking);
                    }
                }
            }

            @Override
            public void onFetchFailure(String message) {
                hideLoadingDialog(progressDialog);
                showToast(requireContext(), message);
            }
        });

        backButton = root.findViewById(R.id.back);
        pickUpTextView = root.findViewById(R.id.pick_up);
        destinationTextView = root.findViewById(R.id.destination);
        bookingTimeTextView = root.findViewById(R.id.booking_time);
        statusTextView = root.findViewById(R.id.status);
        brandTextView = root.findViewById(R.id.brand);
        vehicleNameTextView = root.findViewById(R.id.name);
        colorTextView = root.findViewById(R.id.color);
        seatTextView = root.findViewById(R.id.seat);
        plateTextView = root.findViewById(R.id.plate);
        amountTextView = root.findViewById(R.id.amount);
        methodTextView = root.findViewById(R.id.method);
        avatar = root.findViewById(R.id.avatar);
        driverNameTextView = root.findViewById(R.id.driver_name);
        driverGenderTextView = root.findViewById(R.id.gender);
        licenseNumberTextView = root.findViewById(R.id.license_number);
        realPickUpTimeTextView = root.findViewById(R.id.real_pick_up_time);
        imageVIew = root.findViewById(R.id.image);
        helpButton = root.findViewById(R.id.help_button);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                fragmentTransaction.replace(R.id.fragment_main_container, new MainBookingFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), HelpActivity.class).putExtra("bookingId", bookingID));
            }
        });

        return root;
    }

    private void updateUI(Booking booking){
        pickUpTextView.setText(booking.getPickUp());
        destinationTextView.setText(booking.getDestination().getAddress());
        bookingTimeTextView.setText(booking.getBookingTime());
        statusTextView.setText(booking.getStatus());
        brandTextView.setText(booking.getVehicle().getBrand());
        vehicleNameTextView.setText(booking.getVehicle().getName());
        colorTextView.setText(booking.getVehicle().getColor());
        seatTextView.setText(booking.getVehicle().getSeatCapacity());
        plateTextView.setText(booking.getVehicle().getNumberPlate());
        String amount = booking.getPayment().getAmount() + " " + booking.getPayment().getCurrency();
        amountTextView.setText(amount);
        methodTextView.setText("Card");

        hideLoadingDialog(progressDialog);
    }
}