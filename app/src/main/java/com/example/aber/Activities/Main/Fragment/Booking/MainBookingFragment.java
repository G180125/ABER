package com.example.aber.Activities.Main.Fragment.Booking;

import static com.example.aber.Utils.AndroidUtil.hideLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.aber.Adapters.BookingAdapter;
import com.example.aber.Utils.FirebaseUtil;
import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.Booking.BookingResponse;
import com.example.aber.Models.User.User;
import com.example.aber.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainBookingFragment extends Fragment implements BookingAdapter.RecyclerViewClickListener{
    private FirebaseUtil firebaseManager;
    private ProgressDialog progressDialog;
    private boolean isSpinnerTouched = false;

    private List<Booking> bookingList;
    private BookingAdapter adapter;
    private User user;
    private String userID;
    private Spinner bookingStatusSpinner;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        progressDialog = new ProgressDialog(requireContext());
        showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main_booking, container, false);
        firebaseManager = new FirebaseUtil();

        userID = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(userID, new FirebaseUtil.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                user = object;
                if (user.getBookings() == null) {
                    bookingList = new ArrayList<>();
                } else {
                    bookingList = user.getBookings();
                }
                updateUI(bookingList);
            }

            @Override
            public void onFetchFailure(String message) {
                hideLoadingDialog(progressDialog);
                showToast(requireContext(), message);
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.bookingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BookingAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        bookingStatusSpinner = root.findViewById(R.id.booking_status_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.booking_status_options,
                android.R.layout.simple_spinner_item

        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        int verticalOffsetPixels = getResources().getDimensionPixelOffset(R.dimen.dropdown_offset); // Set your desired offset



        bookingStatusSpinner.setBackgroundResource(R.drawable.bg_spinner_up);
        bookingStatusSpinner.setDropDownVerticalOffset(verticalOffsetPixels);
        bookingStatusSpinner.setAdapter(adapter);

        bookingStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showLoadingDialog(progressDialog);
                String status = parent.getItemAtPosition(position).toString();
                Log.d("status", status);
                isSpinnerTouched = false;
                bookingStatusSpinner.setBackgroundResource(R.drawable.bg_spinner_up);

                firebaseManager.getBookingsByStatus(userID, status, new FirebaseUtil.OnFetchBookingListListener<Booking>() {
                    @Override
                    public void onDataChanged(List<Booking> object) {
                        if (object == null) {
                            bookingList = new ArrayList<>();
                        } else {
                            bookingList = object;
                        }
                        updateUI(bookingList);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bookingStatusSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bookingStatusSpinner.setBackgroundResource(R.drawable.bg_spinner);
            }
            return false;
        });
        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<Booking> bookingList){
        adapter.setBookingList(bookingList);
        adapter.notifyDataSetChanged();
        hideLoadingDialog(progressDialog);
    }

    @Override
    public void onViewButtonClick(int position) {
        BookingDetailFragment fragment = new BookingDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("bookingID", bookingList.get(position).getId());
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentTransaction.replace(R.id.fragment_main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCancelButtonClicked(int position) {
        showLoadingDialog(progressDialog);
        isCancelable(position, new OnCheckCancelCallback() {
            @Override
            public void onCheckCancel(String message) {
                if (isAdded()) {
                    if (message.equals("OK")) {
                        cancelBooking(position);
                    } else {
                        showToast(requireContext(), message);
                        hideLoadingDialog(progressDialog);
                    }
                }
            }
        });
    }

    private void isCancelable(int position, OnCheckCancelCallback callback) {

        Booking booking = bookingList.get(position);
        if(booking.getStatus().equals("Cancel")){
            callback.onCheckCancel("This booking is already canceled");
            return;
        }
        if (!booking.getStatus().equals("Pending")) {
            callback.onCheckCancel("Driver Already Accept The Booking");
            return;
        }

        callback.onCheckCancel("OK");
    }

    // Define a callback interface
    public interface OnCheckCancelCallback {
        void onCheckCancel(String message);
    }


    private void cancelBooking(int position){
        Booking booking = bookingList.get(position);
        booking.setStatus("Cancel");

        firebaseManager.fetchBookings(new FirebaseUtil.OnFetchBookingListListener<BookingResponse>() {
            @Override
            public void onDataChanged(List<BookingResponse> object) {
                for(BookingResponse bookingResponse: object){
                    if(booking.getId().equals(bookingResponse.getBooking().getId())){
                        firebaseManager.cancelBooking(bookingResponse.getId(), booking);
                    }
                }
            }
        });

        for (Booking bookingInList: user.getBookings()){
            if(booking.getId().equals(bookingInList.getId())){
                bookingInList.setStatus("Cancel");
                break;
            }
        }

        firebaseManager.updateUser(userID, user, new FirebaseUtil.OnTaskCompleteListener() {
            @Override
            public void onTaskSuccess(String message) {
                showToast(requireContext(),"Cancel Booking Successfully");
                updateUI(user.getBookings());
                hideLoadingDialog(progressDialog);
            }

            @Override
            public void onTaskFailure(String message) {
                showToast(requireContext(),message);
                hideLoadingDialog(progressDialog);
            }
        });
    }
}