package com.example.aber.Activities.Register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.aber.R;
import com.example.aber.Activities.Register.Fragment.RegisterAccountFragment;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState == null) {
                RegisterAccountFragment fragment = new RegisterAccountFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }
        }
    }
}