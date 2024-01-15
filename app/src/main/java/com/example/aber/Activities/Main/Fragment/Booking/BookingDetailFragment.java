package com.example.aber.Activities.Main.Fragment.Booking;

import static com.example.aber.Utils.AndroidUtil.hideLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.TextView;

import com.example.aber.Activities.Main.Fragment.Chat.DriverChatActivity;
import com.example.aber.Activities.Main.Fragment.Profile.HelpActivity;
import com.example.aber.Utils.FirebaseUtil;
import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.Staff.Driver;
import com.example.aber.Models.User.Gender;
import com.example.aber.Models.User.User;
import com.example.aber.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingDetailFragment extends Fragment {
    private FirebaseUtil firebaseManager;
    private ProgressDialog progressDialog;
    private User user;
    private Booking booking;
    private String bookingID;
    private TextView pickUpTextView, destinationTextView, bookingTimeTextView, statusTextView, brandTextView, vehicleNameTextView, colorTextView, seatTextView, plateTextView, amountTextView, methodTextView, driverNameTextView, driverGenderTextView, licenseNumberTextView, phoneNUmberTextView, realPickUpTimeTextView;
    private CircleImageView avatar;
    private ImageView backButton, imageVIew, vehicleExpand, paymentExpand, driverExpand, resourceExpand;
    private CardView vehicleCardView, paymentCardView, driverCardView, resourceCardView;
    private boolean[] imageViewClickStates = {false, false, false, false};
    private Button helpButton, chatButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        firebaseManager = new FirebaseUtil();

        Bundle args = getArguments();
        if (args != null) {
            bookingID = args.getString("bookingID", "");
        }

        String id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseUtil.OnFetchListener<User>() {
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
        phoneNUmberTextView = root.findViewById(R.id.phone_number);
        realPickUpTimeTextView = root.findViewById(R.id.real_pick_up_time);
        imageVIew = root.findViewById(R.id.image);
        chatButton = root.findViewById(R.id.chat_button);
        helpButton = root.findViewById(R.id.help_button);
        vehicleExpand = root.findViewById(R.id.vehicle_expand);
        paymentExpand = root.findViewById(R.id.payment_expand);
        driverExpand = root.findViewById(R.id.driver_expand);
        resourceExpand = root.findViewById(R.id.resource_expand);
        vehicleCardView = root.findViewById(R.id.vehicle_card_view);
        paymentCardView = root.findViewById(R.id.payment_card_view);
        driverCardView = root.findViewById(R.id.driver_card_view);
        resourceCardView = root.findViewById(R.id.resource_card_view);


        pickUpTextView.setSelected(true);
        destinationTextView.setSelected(true);
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

        vehicleExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewClickStates[0] = !imageViewClickStates[0];

                if (imageViewClickStates[0]) {
                    vehicleExpand.setImageResource(R.drawable.ic_arrow_up);
                    vehicleCardView.setVisibility(View.VISIBLE);
                } else {
                    vehicleExpand.setImageResource(R.drawable.ic_arrow_down);
                    vehicleCardView.setVisibility(View.GONE);
                }
            }
        });

        paymentExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewClickStates[1] = !imageViewClickStates[1];

                if (imageViewClickStates[1]) {
                    paymentExpand.setImageResource(R.drawable.ic_arrow_up);
                    paymentCardView.setVisibility(View.VISIBLE);
                } else {
                    paymentExpand.setImageResource(R.drawable.ic_arrow_down);
                    paymentCardView.setVisibility(View.GONE);
                }
            }
        });

        driverExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewClickStates[2] = !imageViewClickStates[2];

                if (imageViewClickStates[2]) {
                    driverExpand.setImageResource(R.drawable.ic_arrow_up);
                    driverCardView.setVisibility(View.VISIBLE);
                } else {
                    driverExpand.setImageResource(R.drawable.ic_arrow_down);
                    driverCardView.setVisibility(View.GONE);
                }
            }
        });

        resourceExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewClickStates[3] = !imageViewClickStates[3];

                if (imageViewClickStates[3]) {
                    resourceExpand.setImageResource(R.drawable.ic_arrow_up);
                    resourceCardView.setVisibility(View.VISIBLE);
                } else {
                    resourceExpand.setImageResource(R.drawable.ic_arrow_down);
                    resourceCardView.setVisibility(View.GONE);
                }
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String driverId = booking.getDriver();
                if (driverId != null && !driverId.isEmpty()) {
                    startActivity(new Intent(requireContext(), DriverChatActivity.class).putExtra("driverID", driverId));
                } else {
                    // Handle the case where driverId is null or empty
                    showToast(requireContext(), "Driver information not available");
                }
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
        if(Objects.equals(booking.getStatus(), "Cancel")){
            // Color red for cancel status
            statusTextView.setTextColor(Color.parseColor("#FA3737"));
        } else if(Objects.equals(booking.getStatus(), "Picked Up")){
            //Color orange for pick up status
            statusTextView.setTextColor(Color.parseColor("#EC5109"));
        } else if(Objects.equals(booking.getStatus(), "Pending")){
            //Color yellow for Pending
            statusTextView.setTextColor(Color.parseColor("#FFC107"));
        } else if(Objects.equals(booking.getStatus(), "Done") || Objects.equals(booking.getStatus(), "Driver Accepted")){
            statusTextView.setTextColor(Color.parseColor("##4CAF50"));
        }

        pickUpTextView.setText(booking.getPickUp().getAddress());
        destinationTextView.setText(booking.getDestination().getAddress());
        bookingTimeTextView.setText(booking.getBookingTime());
        statusTextView.setText(booking.getStatus());
        brandTextView.setText(booking.getVehicle().getBrand());
        vehicleNameTextView.setText(booking.getVehicle().getName());
        colorTextView.setText(booking.getVehicle().getColor());
        seatTextView.setText(booking.getVehicle().getSeatCapacity());
        plateTextView.setText(booking.getVehicle().getNumberPlate());
        String amount = booking.getPayment().getAmount().intValue() + " " + booking.getPayment().getCurrency();
        amountTextView.setText(amount);
        methodTextView.setText("Card **** **** ****" + booking.getPayment().getCard().getLast4());

        if(booking.getDriver() != null &&  !booking.getDriver().isEmpty()){
            firebaseManager.getDriverByID(booking.getDriver(), new FirebaseUtil.OnFetchListener<Driver>() {
                @Override
                public void onFetchSuccess(Driver object) {
                    driverNameTextView.setText(object.getName());
                    driverGenderTextView.setText(getGenderText(object.getGender()));
                    licenseNumberTextView.setText(object.getLicenseNumber());
                    phoneNUmberTextView.setText(object.getPhone());

                    firebaseManager.retrieveImage(object.getAvatar(), new FirebaseUtil.OnRetrieveImageListener() {
                        @Override
                        public void onRetrieveImageSuccess(Bitmap bitmap) {
                            avatar.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onRetrieveImageFailure(String message) {

                        }
                    });
                }

                @Override
                public void onFetchFailure(String message) {

                }
            });
        }

        hideLoadingDialog(progressDialog);
    }

    public static String getGenderText(Gender gender) {
        switch (gender) {
            case MALE:
                return "Male";
            case FEMALE:
                return "Female";
            default:
                return "Unknown";
        }
    }
}