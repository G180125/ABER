package com.example.aber.Activities.Main.Fragment.Chat;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aber.FirebaseManager;
import com.example.aber.R;

public class MainChatFragment extends Fragment {
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_main_chat, container, false);
        firebaseManager = new FirebaseManager();
        DriverChatListFragment fragment = new DriverChatListFragment();

        getParentFragmentManager().beginTransaction()
                .add(R.id.fragment_main_chat_container, fragment)
                .addToBackStack(null)
                .commit();

        return root;
    }
}