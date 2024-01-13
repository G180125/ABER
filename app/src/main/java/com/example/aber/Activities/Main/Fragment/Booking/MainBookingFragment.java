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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private List<Booking> bookingList;
    private BookingAdapter adapter;
    private User user;
    private String id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main_booking, container, false);
        firebaseManager = new FirebaseUtil();

        id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseUtil.OnFetchListener<User>() {
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
            adapter = new BookingAdapter(new ArrayList<>(),this);
            recyclerView.setAdapter(adapter);

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
        firebaseManager.fetchBookings(new FirebaseUtil.OnFetchBookingListListener<BookingResponse>() {
            @Override
            public void onDataChanged(List<BookingResponse> object) {
                for (BookingResponse bookingResponse : object) {
                    if (bookingList.get(position).getId().equals(bookingResponse.getBooking().getId())) {
                        if (bookingResponse.getBooking().getStatus().equals("Driver Accept")) {
                            callback.onCheckCancel("Driver Already Accept The Booking");
                            return;
                        }

                        if(bookingResponse.getBooking().getStatus().equals("Cancel")){
                            callback.onCheckCancel("This booking is already canceled");
                            return;
                        }
                    }
                }
                callback.onCheckCancel("OK");
            }
        });
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

        firebaseManager.updateUser(id, user, new FirebaseUtil.OnTaskCompleteListener() {
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