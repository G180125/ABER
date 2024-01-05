package com.example.aber.Activities.Main.Fragment;

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
import com.example.aber.FirebaseManager;
import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.User.User;
import com.example.aber.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainBookingFragment extends Fragment implements BookingAdapter.RecyclerViewClickListener{
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private List<Booking> bookingList;
    private BookingAdapter adapter;
    private User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main_booking, container, false);
        firebaseManager = new FirebaseManager();

        String id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseManager.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                user = object;
                bookingList = user.getBookings();
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

    }
}