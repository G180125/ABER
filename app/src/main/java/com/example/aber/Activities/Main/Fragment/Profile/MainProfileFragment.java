package com.example.aber.Activities.Main.Fragment.Profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aber.Activities.LoginActivity;
import com.example.aber.Activities.Main.Fragment.MainHomeFragment;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.User;
import com.example.aber.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainProfileFragment extends Fragment {
    private FirebaseManager firebaseManager;
    private User currentUser;
    private String userID;
    private ProgressDialog progressDialog;
    private CircleImageView avatar;
    private TextView nameTextView, emailTextView;
    private CardView profileCardView, walletCardView, historyCardView, aboutUsCardView, logoutCardView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showLoadingDialog();
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_main_profile, container, false);
        firebaseManager = new FirebaseManager();

        userID = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(userID, new FirebaseManager.OnFetchUserListener() {
            @Override
            public void onFetchUserSuccess(User user) {
                currentUser = user;
                updateUI(currentUser);
            }

            @Override
            public void onFetchUserFailure(String message) {
                hideLoadingDialog();
                showToast(message);
            }
        });

        avatar = root.findViewById(R.id.avatar);
        nameTextView = root.findViewById(R.id.name);
        emailTextView = root.findViewById(R.id.email);
        profileCardView = root.findViewById(R.id.profile);
        walletCardView = root.findViewById(R.id.wallet);
        historyCardView = root.findViewById(R.id.history);
        aboutUsCardView = root.findViewById(R.id.about_us);
        logoutCardView = root.findViewById(R.id.logout);

        profileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                replaceFragment(new ProfileEditFragment(), fragmentManager, fragmentTransaction);
            }
        });

        walletCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("wallet card is clicked");
            }
        });

        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("history card is clicked");
            }
        });

        aboutUsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("about us card is clicked");
            }
        });

        logoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseManager.mAuth.signOut();
                requireActivity().finish();
                startActivity(new Intent(requireContext(), LoginActivity.class));

            }
        });

        return root;
    }

    private void updateAvatar(Bitmap bitmap){
        avatar.setImageBitmap(bitmap);
    }

    private void updateUI(User user){
        if(!user.getAvatar().isEmpty()){
            firebaseManager.retrieveImage(user.getAvatar(), new FirebaseManager.OnRetrieveImageListener() {
                @Override
                public void onRetrieveImageSuccess(Bitmap bitmap) {
                    updateAvatar(bitmap);
                    hideLoadingDialog();
                }

                @Override
                public void onRetrieveImageFailure(String message) {
                    showToast(message);
                    hideLoadingDialog();
                }
            });
        } else {
            hideLoadingDialog();
        }
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
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

    private void replaceFragment(Fragment fragment, FragmentManager fragmentManager, FragmentTransaction fragmentTransaction) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentTransaction.replace(R.id.fragment_main_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}