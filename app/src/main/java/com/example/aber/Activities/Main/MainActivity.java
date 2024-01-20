package com.example.aber.Activities.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.aber.Activities.Main.Fragment.Booking.MainBookingFragment;
import com.example.aber.Activities.Main.Fragment.Home.MainHomeFragment;
import com.example.aber.Activities.Main.Fragment.Chat.MainChatFragment;
import com.example.aber.Activities.Main.Fragment.Profile.MainProfileFragment;
import com.example.aber.Models.User.SOS;
import com.example.aber.Models.User.SOSActiveResponse;
import com.example.aber.Models.User.User;
import com.example.aber.R;
import com.example.aber.Services.SOS.SensorService;
import com.example.aber.Utils.FirebaseUtil;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 2;
    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;
    private static final int PICK_CONTACT = 1;
    private final int ID_HOME = 1;
    private final int ID_BOOKING = 2;
    private final int ID_CHAT = 3;
    private final int ID_PROFILE = 4;
    private MeowBottomNavigation bottomNavigation;
    private FirebaseUtil firebaseManager;
    private String token, userId;
    private User currentUer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = new FirebaseUtil();

        userId = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getFCMToken(new FirebaseUtil.OnFetchListener<String>() {
            @Override
            public void onFetchSuccess(String object) {
                token = object;
            }

            @Override
            public void onFetchFailure(String message) {

            }
        });

        firebaseManager.getUserByID(userId, new FirebaseUtil.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                currentUer = object;
                object.setFcmToken(token);
                firebaseManager.updateUser(userId, object, new FirebaseUtil.OnTaskCompleteListener() {
                    @Override
                    public void onTaskSuccess(String message) {

                    }

                    @Override
                    public void onTaskFailure(String message) {

                    }
                });
            }

            @Override
            public void onFetchFailure(String message) {

            }
        });

        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_BOOKING, R.drawable.ic_booking));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_CHAT, R.drawable.ic_chat));
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
                    case ID_CHAT:
                        replaceFragment(new MainChatFragment(), fragmentManager, fragmentTransaction);
                        break;
                    case ID_PROFILE:
                        replaceFragment(new MainProfileFragment(), fragmentManager, fragmentTransaction);
                        break;
                }
                return null;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS
        }, NOTIFICATION_PERMISSION_REQUEST_CODE);

        // check for BatteryOptimization,
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
            askIgnoreOptimization();
        }

//        firebaseManager.isSOSActive(userId, new FirebaseUtil.OnCheckingSOSActiveListener() {
//            @Override
//            public void OnDataChanged(SOSActiveResponse object) {
//                SensorService sensorService;
//                if(object != null){
//                    // start the service
//                    if(object.getEmergencyContact() != null) {
//                        sensorService = new SensorService(object.getEmergencyContact());
//                    } else {
//                        sensorService = new SensorService(currentUer.getEmergencyContacts().get(0));
//                    }
//                    Intent intent = new Intent(MainActivity.this, sensorService.getClass());
//                    if (!isMyServiceRunning(sensorService.getClass())) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            startForegroundService(intent);
//                        } else {
//                            startService(intent);
//                        }
//
//                    }
//                } else {
//                    sensorService = new SensorService(new SOS());
//                    Intent intent = new Intent(MainActivity.this, sensorService.getClass());
//                    if (isMyServiceRunning(sensorService.getClass())) {
//                        stopService(intent);
//                    }
//                }
//            }
//        });
    }

    private void replaceFragment(Fragment fragment, FragmentManager fragmentManager, FragmentTransaction fragmentTransaction) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentTransaction.replace(R.id.fragment_main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void askIgnoreOptimization() {
        @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST);

    }


}