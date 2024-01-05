package com.example.aber.Activities.Main.Fragment.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aber.Activities.Main.Fragment.Chat.MainChatFragment;
import com.example.aber.Activities.Main.Fragment.Home.MainHomeFragment;
import com.example.aber.Activities.Main.Fragment.MainBookingFragment;
import com.example.aber.Activities.Main.Fragment.Profile.MainProfileFragment;
import com.example.aber.R;

public class BookingSuccessFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_booking_success, container, false);
        Button backButton = root.findViewById(R.id.back_button);
        Button bookingBUtton = root.findViewById(R.id.booking_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new MainHomeFragment());
            }
        });

        bookingBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new MainBookingFragment());
            }
        });

        return root;
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentTransaction.replace(R.id.fragment_main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}