package com.example.aber.Activities.Main.Fragment.Profile.Settings;

import static com.example.aber.Utils.AndroidUtil.replaceFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aber.Activities.Main.Fragment.Profile.MainProfileFragment;
import com.example.aber.R;

import org.w3c.dom.Text;


public class ProfileSettingsFragment extends Fragment {
    private ImageView buttonBack;
    private TextView languageSetting,darkSetting;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_profile_settings, container, false);
        buttonBack = root.findViewById(R.id.back);
        languageSetting = root.findViewById(R.id.setting_language);
        darkSetting = root.findViewById(R.id.setting_dark);


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                fragmentTransaction.replace(R.id.fragment_main_container, new MainProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        languageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                replaceFragment(new LanguageSettingsFragment(),fragmentManager,fragmentTransaction,R.id.fragment_main_container);
            }
        });

        darkSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Dark have been clicked", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }
}