package com.example.aber.Activities.Main.Fragment.Profile.Settings;

import static com.example.aber.Utils.AndroidUtil.replaceFragment;

import android.os.Bundle;


import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.aber.Activities.Main.Fragment.Profile.MainProfileFragment;
import com.example.aber.Adapters.SettingsAdapter;
import com.example.aber.Models.Settings.SettingItem;
import com.example.aber.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ProfileSettingsFragment extends Fragment {
    private ImageView buttonBack;

    private SearchView searchView;
    private ListView settingsListView;

    private List<SettingItem> allSettings;
    private List<SettingItem> filteredSettings;
    private ArrayAdapter<SettingItem> settingsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_profile_settings, container, false);
        buttonBack = root.findViewById(R.id.back);
        searchView = root.findViewById(R.id.search_view);
        settingsListView = root.findViewById(R.id.settings_list_view);
        allSettings = new ArrayList<>();
        allSettings.add(new SettingItem("Language", R.drawable.ic_language24));
        allSettings.add(new SettingItem("Dark Mode", R.drawable.ic_dark_mode24));

        filteredSettings = new ArrayList<>(allSettings);

        settingsAdapter = new SettingsAdapter(requireContext(), filteredSettings);
        settingsListView.setAdapter(settingsAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSettings(newText);
                return false;
            }
        });

        settingsListView.setOnItemClickListener((parent, view, position, id) -> {
            SettingItem selectedSetting = filteredSettings.get(position);
            handleSettingClick(selectedSetting);
        });

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



        return root;
    }

    private void filterSettings(String query) {
        filteredSettings.clear();

        if (query.isEmpty()) {
            filteredSettings.addAll(allSettings);
        } else {
            for (SettingItem setting : allSettings) {
                if (setting.getText().toLowerCase().contains(query.toLowerCase())) {
                    filteredSettings.add(setting);
                }
            }
        }

        settingsAdapter.notifyDataSetChanged();
    }
    private void handleSettingClick(SettingItem setting) {
        if (setting.getText().equals("Language")) {
            // Handle language setting click
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            replaceFragment(new LanguageSettingsFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
        } else if (setting.getText().equals("Dark Mode")) {
            // Handle dark mode setting click
            Toast.makeText(requireContext(), "Dark mode clicked", Toast.LENGTH_SHORT).show();
        }
    }
}