package com.example.aber.Activities.Main.Fragment.Profile;

import static com.example.aber.Utils.AndroidUtil.hideLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.replaceFragment;
import static com.example.aber.Utils.AndroidUtil.showLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showToast;

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

import com.example.aber.Activities.LoginActivity;
import com.example.aber.Activities.Main.Fragment.AboutUs.AboutUsActivity;
import com.example.aber.Activities.Main.Fragment.Profile.Edit.ProfileEditFragment;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.User;
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
    private CardView profileCardView, walletCardView, historyCardView, aboutUsCardView, helpCardView, logoutCardView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_main_profile, container, false);
        firebaseManager = new FirebaseManager();

        userID = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(userID, new FirebaseManager.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User user) {
                currentUser = user;
                updateUI(currentUser);
            }

            @Override
            public void onFetchFailure(String message) {
                hideLoadingDialog(progressDialog);
                showToast(requireContext(),message);
            }
        });

        avatar = root.findViewById(R.id.avatar);
        nameTextView = root.findViewById(R.id.name);
        emailTextView = root.findViewById(R.id.email);
        profileCardView = root.findViewById(R.id.profile);
        walletCardView = root.findViewById(R.id.wallet);
        historyCardView = root.findViewById(R.id.history);
        aboutUsCardView = root.findViewById(R.id.about_us);
        helpCardView = root.findViewById(R.id.help);
        logoutCardView = root.findViewById(R.id.logout);

        profileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                replaceFragment(new ProfileEditFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
            }
        });

        walletCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(requireContext(),"wallet card is clicked");
            }
        });

        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(requireContext(),"history card is clicked");
            }
        });

        aboutUsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(requireContext(),"about us card is clicked");
                startActivity(new Intent(requireContext(), AboutUsActivity.class));
            }
        });

        helpCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), HelpActivity.class));
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
                    hideLoadingDialog(progressDialog);
                }

                @Override
                public void onRetrieveImageFailure(String message) {
                    showToast(requireContext(),message);
                    hideLoadingDialog(progressDialog);
                }
            });
        } else {
            hideLoadingDialog(progressDialog);
        }
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
    }
}