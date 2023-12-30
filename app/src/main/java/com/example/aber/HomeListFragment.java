package com.example.aber;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import static com.example.aber.Utils.AndroidUtil.replaceFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aber.Activities.Main.Fragment.Profile.ProfileEditFragment;
import com.example.aber.Adapters.UserHomeAdapter;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.User;
import com.example.aber.Utils.AndroidUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeListFragment extends Fragment implements UserHomeAdapter.RecyclerViewClickListener{
    private ImageView buttonBack;
    private UserHomeAdapter userHomeAdapter;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressBar;
    private String id;
    private User user;
    private List<Home> homeList;
    private UserHomeAdapter adapter;
    private PopupWindow popupWindow;
    private Button addButton;
    private View root;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressBar = new ProgressDialog(requireContext());
        AndroidUtil.showLoadingDialog(progressBar);
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home_list, container, false);
        firebaseManager = new FirebaseManager();

        id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseManager.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                user = object;
                homeList = user.getHomes();
                updateUI(homeList);
            }

            @Override
            public void onFetchFailure(String message) {

            }
        });

        buttonBack = root.findViewById(R.id.buttonBack);
        addButton = root.findViewById(R.id.add_button);

        RecyclerView recyclerView = root.findViewById(R.id.addressRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserHomeAdapter(new ArrayList<>(),this);
        recyclerView.setAdapter(adapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                replaceFragment(new ProfileEditFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow(null, "Enter Additional Home", 0);
                popupWindow.showAsDropDown(root, 0, 0);
            }
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<Home> homeList){
        adapter.setHomeList(homeList);
        adapter.notifyDataSetChanged();
        AndroidUtil.hideLoadingDialog(progressBar);
    }

    @Override
    public void onSetDefaultButtonClick(int position) {
        if (position > 0 && position < homeList.size()) {
            AndroidUtil.showLoadingDialog(progressBar);
            Home selectedHome = homeList.get(position);
            homeList.remove(position);
            homeList.add(0, selectedHome);
            updateList(user, homeList,"This Home is set to default." );
        } else {
            AndroidUtil.showToast(getContext(), "Error! Please Try Again.");
        }

    }

    @Override
    public void onEditButtonClicked(int position) {
        Home home = homeList.get(position);
        initPopupWindow(home, "Edit Home", position);
        popupWindow.showAsDropDown(root, 0, 0);
        updateUI(homeList);
    }

    @Override
    public void onDeleteButtonClicked(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this home?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        homeList.remove(position);
                        updateList(user, homeList, "Delete Home Successful");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing
                    }
                })
                .show();
    }

    public void initPopupWindow(Home home, String title, int position) {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_address_form, null);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        // Set the background color with alpha transparency
        popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

        TextView titleTextView = popupView.findViewById(R.id.title);
        EditText addressEditText = popupView.findViewById(R.id.address_edit_text);
        ImageView homeImageView = popupView.findViewById(R.id.home_image);
        Button submitButton = popupView.findViewById(R.id.submitNewAddressBtn);
        ImageView cancelBtn = popupView.findViewById(R.id.cancelBtn);

        titleTextView.setText(title);

        if (home != null) {
            addressEditText.setText(home.getAddress());

            firebaseManager.retrieveImage(home.getAddress(), new FirebaseManager.OnRetrieveImageListener() {
                @Override
                public void onRetrieveImageSuccess(Bitmap bitmap) {
                    homeImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onRetrieveImageFailure(String message) {

                }
            });
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showLoadingDialog(progressBar);

                String newAddress = addressEditText.getText().toString();

                if (title.equals("Edit Home")) {
                    // Update existing home
                    if (home != null) {
                        home.setAddress(newAddress);
                        homeList.set(position, home);
                    }
                } else {
                    // Add a new home
                    Home newHome = new Home(newAddress, "path");
                    homeList.add(0, newHome);
                }

                // Update the user with the modified homeList
                updateList(user, homeList, "Update Successful");

                // Dismiss the PopupWindow after updating the homeList
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(root, 0, 0);

    }


    private void updateList(User user, List<Home> homeList, String successMessage){
        user.setHomes(homeList);
        firebaseManager.updateUser(id, user, new FirebaseManager.OnTaskCompleteListener() {
            @Override
            public void onTaskSuccess(String message) {
                AndroidUtil.showToast(getContext(), successMessage);
                updateUI(homeList);
            }

            @Override
            public void onTaskFailure(String message) {
                AndroidUtil.showToast(getContext(), message);
                AndroidUtil.hideLoadingDialog(progressBar);
            }
        });
    }






}