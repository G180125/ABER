package com.example.aber.Activities.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.aber.Activities.Main.Fragment.MainBookingFragment;
import com.example.aber.Activities.Main.Fragment.MainHomeFragment;
import com.example.aber.Activities.Main.Fragment.MainWalletFragment;
import com.example.aber.Activities.Main.Fragment.MainProfileFragment;
import com.example.aber.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    private final int ID_HOME = 1;
    private final int ID_BOOKING = 2;
    private final int ID_WALLET = 3;
    private final int ID_PROFILE = 4;
    private MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_BOOKING, R.drawable.ic_booking));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_WALLET, R.drawable.ic_wallet));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_PROFILE, R.drawable.ic_profile));

        bottomNavigation.show(ID_HOME, true);

        if (findViewById(R.id.fragment_main_container) != null) {
            if (savedInstanceState == null) {
                MainHomeFragment fragment = new MainHomeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_main_container, fragment);
                fragmentTransaction.commit();
            }
        }

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (model.getId()){
                    case ID_HOME:
                        replaceFragment(new MainHomeFragment(), fragmentManager, fragmentTransaction);
                        break;
                    case ID_BOOKING:
                        replaceFragment(new MainBookingFragment(), fragmentManager, fragmentTransaction);
                        break;
                    case ID_WALLET:
                        replaceFragment(new MainWalletFragment(), fragmentManager, fragmentTransaction);
                        break;
                    case ID_PROFILE:
                        replaceFragment(new MainProfileFragment(), fragmentManager, fragmentTransaction);
                        break;
                }
                return null;
            }
        });
    }

    private void replaceFragment(Fragment fragment, FragmentManager fragmentManager, FragmentTransaction fragmentTransaction) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentTransaction.replace(R.id.fragment_main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}