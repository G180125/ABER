package com.example.aber.Activities.Main.Fragment.Chat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aber.Adapters.DriverChatAdapter;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.Staff.Driver;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

public class DriverChatListFragment extends Fragment implements DriverChatAdapter.RecyclerViewClickListener {
    private DriverChatAdapter adapter;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private List<Driver> driverList, filteredList;
    private SearchView searchView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        AndroidUtil.showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_driver_chat_list, container, false);
        firebaseManager = new FirebaseManager();

        firebaseManager.getAllDrivers(new FirebaseManager.OnFetchDriverListListener() {
            @Override
            public void onFetchDriverListSuccess(List<Driver> list) {
                driverList = list;
                updateUI(driverList);
                AndroidUtil.hideLoadingDialog(progressDialog);
            }

            @Override
            public void onFetchDriverListFailure(String message) {
                AndroidUtil.showToast(requireContext(), message);
                AndroidUtil.hideLoadingDialog(progressDialog);
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.recycler_chat_driver);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new DriverChatAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        searchView = root.findViewById(R.id.search_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchQuery = newText.toString().toLowerCase();
                filterList(searchQuery);
                return true;
            }
        });

        return root;
    }

    private void filterList(String searchQuery){
        filteredList = new ArrayList<>();
        for (Driver driver : driverList){
            if (driver.getName().toLowerCase().contains(searchQuery)){
                filteredList.add(driver);
            }
        }
        updateUI(filteredList);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<Driver> driverList){
        adapter.setDriverList(driverList);
        adapter.notifyDataSetChanged();
        AndroidUtil.hideLoadingDialog(progressDialog);
        if(filteredList != null){
            adapter.setDriverList(filteredList);
        }
    }

    @Override
    public void onCardClick(int position) {
        String id = driverList.get(position).getDocumentID();

        if(id != null){
            ChatDetailFragment fragment = new ChatDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("driverID", id);
            fragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_main_chat_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}