package com.example.aber;

import static com.example.aber.Utils.AndroidUtil.replaceFragment;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.aber.Activities.Main.Fragment.Booking.BookingDetailFragment;
import com.example.aber.Activities.Main.Fragment.Home.ConfirmBookingFragment;
import com.example.aber.Activities.Main.Fragment.Home.MainHomeFragment;
import com.example.aber.Activities.Main.Fragment.Profile.Edit.ProfileEditFragment;
import com.example.aber.Adapters.NotificationAdapter;
import com.example.aber.Adapters.UserHomeAdapter;
import com.example.aber.Models.Booking.Booking;
import com.example.aber.Models.Notification.InAppNotification;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.User;
import com.example.aber.Utils.AndroidUtil;
import com.example.aber.Utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationListFragment extends Fragment implements NotificationAdapter.RecyclerViewClickListener {
    private String userID;
    private User user;
    private FirebaseUtil firebaseManager;
    private ProgressDialog progressDialog;
    private List<InAppNotification> inAppNotificationList;
    private NotificationAdapter adapter;
    private ImageView buttonBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_notification_list, container, false);
        firebaseManager = new FirebaseUtil();

        userID = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        Log.d("userID", userID);
        firebaseManager.fetchNotifications(userID, new FirebaseUtil.OnFetchListListener<InAppNotification>() {
            @Override
            public void onFetchSuccess(List<InAppNotification> object) {
                Log.d("userID", String.valueOf(object.size()));
                inAppNotificationList = object;
                updateUI(inAppNotificationList);
            }

            @Override
            public void onFetchFailure(String message) {

            }
        });

        buttonBack = root.findViewById(R.id.buttonBack);

        RecyclerView recyclerView = root.findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(new ArrayList<>(),this);
        recyclerView.setAdapter(adapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AndroidUtil.replaceFragment(new MainHomeFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
            }
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<InAppNotification> notificationList){
        AndroidUtil.showLoadingDialog(progressDialog);
        adapter.setNotificationList(notificationList);
        adapter.notifyDataSetChanged();
        AndroidUtil.hideLoadingDialog(progressDialog);
    }


    @Override
    public void onNotificationClick(int position) {
        InAppNotification inAppNotification = inAppNotificationList.get(position);
        inAppNotification.setIsRead(true);
        firebaseManager.updateNotification(inAppNotification);

        BookingDetailFragment fragment = new BookingDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("previous", "notification");
        bundle.putString("bookingID", inAppNotification.getBookingID());
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentTransaction.replace(R.id.fragment_main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}