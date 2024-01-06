package com.example.aber.Activities.Main.Fragment.Home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.aber.Activities.Main.Fragment.Profile.Edit.HomeListFragment;
import com.example.aber.Activities.Main.Fragment.Profile.Edit.SOSListFragment;
import com.example.aber.Activities.Main.Fragment.Profile.Edit.VehicleListFragment;
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
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.common.base.Charsets;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ConfirmBookingFragment extends Fragment {
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private TextView nameTextView, destinationAddressTextView, homeAddressTextView, plateTextview, sosTextView;
    private RadioButton bookNowRadioButton;
    private CardView bookingTimeCardView, homeCardView, vehicleCardView, sosCardView, paymentCardView;
    private ImageView backButton;
    private TimePicker timePicker;
    private String id, name, address;
    private Button nextButton;
    private boolean check;
    private User currentUser;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        AndroidUtil.showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_confirm_booking, container, false);
        firebaseManager = new FirebaseManager();
        firebaseManager = new FirebaseManager();
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
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
        paymentCardView = root.findViewById(R.id.card_card_view);
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
                Log.d("userInfo", currentUser.toString());
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                fragmentTransaction.replace(R.id.fragment_main_container, new MainHomeFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        homeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                HomeListFragment fragment = new HomeListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("previous", "Confirm Booking");
                bundle.putString("name", name);
                bundle.putString("address", address);
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        vehicleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                VehicleListFragment fragment = new VehicleListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("previous", "Confirm Booking");
                bundle.putString("name", name);
                bundle.putString("address", address);
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        sosCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                SOSListFragment fragment = new SOSListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("previous", "Confirm Booking");
                bundle.putString("name", name);
                bundle.putString("address", address);
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        RadioButton card = root.findViewById(R.id.card);
        RadioButton cash = root.findViewById(R.id.cash);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject requestData = new JSONObject();
                try {
                    requestData.put("customerId", currentUser.getStripeCusId());
                    requestData.put("action", "paymentIntent");
                    requestData.put("amount", 10000);
                    Log.d("Checkout", requestData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Fuel.INSTANCE.post("http://10.0.2.2:4242/payment-sheet", null)
                        .header("Content-Type", "application/json")
                        .body(requestData.toString(), Charsets.UTF_8)
                        .responseString(new Handler<String>() {
                            @Override
                            public void success(String s) {
                                try {
                                    final JSONObject result = new JSONObject(s);
                                    customerConfig = new PaymentSheet.CustomerConfiguration(
                                            result.getString("customer"),
                                            result.getString("ephemeralKey")
                                    );
                                    paymentIntentClientSecret = result.getString("clientSecret");
                                    PaymentConfiguration.init(getActivity().getApplicationContext(), result.getString("publishableKey"));

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            presentPaymentSheetPayment();
                                        }
                                    });
                                } catch (JSONException e) {
                                    Log.e("Checkout", "Error parsing JSON: " + e.getMessage());
                                }
                            }
                            @Override
                            public void failure(@NonNull FuelError fuelError) { /* handle error */ }
                        });
            }
        });
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showLoadingDialog(progressDialog);
                String bookingTime = getTimeFromPicker();
                String ETA = getETA(bookingTime);
                Home home = currentUser.getHomes().get(0);
                Payment payment = new Payment("id", 100000.0, "VND", PaymentStatus.PROCESSING, new Card());
                SOS sos;
                if (!currentUser.getEmergencyContacts().isEmpty()){
                    sos = currentUser.getEmergencyContacts().get(0);
                } else {
                    sos = new SOS();
                }
                Vehicle vehicle = currentUser.getVehicles().get(0);

                Booking booking = new Booking(address, home, ETA, bookingTime, "", "", payment, sos, vehicle, id, getCurrentDateFormatted("yyyy-MM-dd"));

                if(currentUser.getBookings() != null) {
                    currentUser.getBookings().add(booking);
                } else {
                    List<Booking> newBookingList = new ArrayList<>();
                    newBookingList.add(booking);
                    currentUser.setBookings(newBookingList);
                }

                firebaseManager.updateUser(id, currentUser, new FirebaseManager.OnTaskCompleteListener() {
                    @Override
                    public void onTaskSuccess(String message) {
                        AndroidUtil.showToast(requireContext(), "Booking Successfully");
                        AndroidUtil.hideLoadingDialog(progressDialog);
                        firebaseManager.addBooking(id, booking);
                        navigateToBookingSuccess();
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

    private String getETA(String bookingTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            // Parse the booking time
            Date parsedBookingTime = sdf.parse(bookingTime);

            // Add 2 hours to the booking time
            Calendar calendar = Calendar.getInstance();
            assert parsedBookingTime != null;
            calendar.setTime(parsedBookingTime);
            calendar.add(Calendar.HOUR_OF_DAY, 2);

            // Format the new time as ETA
            Date etaTime = calendar.getTime();
            return sdf.format(etaTime);

        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Handle parsing error
        }
    }

    private static String getCurrentDateFormatted(String format) {
        Date currentDate = new Date();

        // Format the date using the provided format
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        return formatter.format(currentDate);
    }

    private void navigateToBookingSuccess(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AndroidUtil.replaceFragment(new BookingSuccessFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
    }

    private void presentPaymentSheetPayment() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("ABER")
                .customer(customerConfig)
                // Set `allowsDelayedPaymentMethods` to true if your business handles payment methods
                // delayed notification payment methods like US bank accounts.
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );
    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d("Checkout", "Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e("Checkout", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            Log.d("Checkout", "Completed");
        }
    }
}