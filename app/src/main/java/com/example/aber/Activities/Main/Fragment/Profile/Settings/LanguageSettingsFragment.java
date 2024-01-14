package com.example.aber.Activities.Main.Fragment.Profile.Settings;

import static com.example.aber.Utils.AndroidUtil.replaceFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aber.Activities.Main.Fragment.Profile.MainProfileFragment;
import com.example.aber.R;

import java.util.Locale;


public class LanguageSettingsFragment extends Fragment {
    private TextView english,vietnamese;
    private ImageView buttonBack;

    private ProgressDialog progressDialog;

    private Handler handler;

    private TextView name;

    private RadioButton englishButton,vietnameseButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_language_settings, container, false);

        english = root.findViewById(R.id.english);
        vietnamese = root.findViewById(R.id.vietnamese);
        englishButton = root.findViewById(R.id.english_radio);
        vietnameseButton = root.findViewById(R.id.vietnamese_radio);
        buttonBack = root.findViewById(R.id.back);
        name = root.findViewById(R.id.name);

        handler = new Handler();

        checkLocale();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                fragmentTransaction.replace(R.id.fragment_main_container, new ProfileSettingsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLocal("en");
                name.setText("Language");
                englishButton.setChecked(true);
                vietnameseButton.setChecked(false);

            }
        });

        vietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLocal("vi");
                name.setText("Ngôn Ngữ");
                englishButton.setChecked(false);
                vietnameseButton.setChecked(true);
            }
        });




        return root;
    }


    public void setLocal(String langCode) {
        if (getActivity() != null) {
            Locale locale = new Locale(langCode);
            Resources resources = getActivity().getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config,resources.getDisplayMetrics());


        } else {
            Log.d("Language","There is no activity");
            Toast.makeText(requireContext(), "No Activity", Toast.LENGTH_SHORT).show();


        }
    }

    private void showLoadingDialog() {
        requireActivity().runOnUiThread(() -> {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        });
    }

    private void hideLoadingDialog() {
        requireActivity().runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }

    private void checkLocale(){
        Locale currentLocale = Locale.getDefault();
        String currentLanguageCode = currentLocale.getLanguage();

        if (currentLanguageCode.equals("en")) {
            englishButton.setChecked(true);
            vietnameseButton.setChecked(false);

        } else if (currentLanguageCode.equals("vi")) {
            englishButton.setChecked(false);
            vietnameseButton.setChecked(true);

        }
    }
}