package com.example.aber.Activities.Main.Fragment.Profile.Edit;

import static com.example.aber.Utils.AndroidUtil.replaceFragment;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.aber.Activities.Main.Fragment.Home.ConfirmBookingFragment;
import com.example.aber.Adapters.UserHomeAdapter;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.User;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeListFragment extends Fragment implements UserHomeAdapter.RecyclerViewClickListener{
    private static final String STORAGE_PATH = "home/";
    private ImageView buttonBack;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private String id, previous, name, address, imagePath;
    private User user;
    private List<Home> homeList;
    private UserHomeAdapter adapter;
    private PopupWindow popupWindow;
    private Button addButton;
    private View root;
    private Bitmap cropped;
    private ImageView homeImageView;
    private final ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                launchImageCropper(imageUri);
            }
        }
    });

    private final ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            cropped = BitmapFactory.decodeFile(result.getUriFilePath(requireContext(), true));
            updateHomeImage(cropped);
        }
    });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        AndroidUtil.showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home_list, container, false);
        firebaseManager = new FirebaseManager();

        Bundle args = getArguments();
        if (args != null) {
            previous = args.getString("previous","");
            name = args.getString("name","");
            address = args.getString("address","");
        }

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
                if(previous.equals("Profile Edit")) {
                    replaceFragment(new ProfileEditFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
                } else if (previous.equals("Confirm Booking")){
                    ConfirmBookingFragment fragment = new ConfirmBookingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("address", address);
                    fragment.setArguments(bundle);

                    replaceFragment(fragment, fragmentManager, fragmentTransaction, R.id.fragment_main_container);
                }
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
        AndroidUtil.hideLoadingDialog(progressDialog);
    }

    @Override
    public void onSetDefaultButtonClick(int position) {
        if (position > 0 && position < homeList.size()) {
            AndroidUtil.showLoadingDialog(progressDialog);
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

        homeImageView = popupView.findViewById(R.id.home_image);
        TextView titleTextView = popupView.findViewById(R.id.title);
        EditText addressEditText = popupView.findViewById(R.id.address_edit_text);
        Button submitButton = popupView.findViewById(R.id.submitNewAddressBtn);
        ImageView cancelBtn = popupView.findViewById(R.id.cancelBtn);

        titleTextView.setText(title);

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        if (home != null) {
            addressEditText.setText(home.getAddress());

            if(home.getAddress() != null) {
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
                Log.d("submit", "submit button clicked");
                AndroidUtil.showLoadingDialog(progressDialog);

                String newAddress = addressEditText.getText().toString();

                if (cropped != null && validateInputs(newAddress)) {
                    // Handle the case when only the avatar is changed
                    imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
                    Log.d("submit", "path: " + imagePath);
                    firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                        @Override
                        public void onTaskSuccess(String message) {
                            AndroidUtil.hideLoadingDialog(progressDialog);
                            updateHome(title, home, newAddress, imagePath, position);
                        }

                        @Override
                        public void onTaskFailure(String message) {
                            AndroidUtil.hideLoadingDialog(progressDialog);
                            showToast(requireContext(), "Upload Image failed");
                        }
                    });
                } else {
                    AndroidUtil.hideLoadingDialog(progressDialog);
                }

                // Dismiss the PopupWindow after updating the homeList
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(root, 0, 0);
    }

    private void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    private void selectImage() {
        getImageFile();
    }

    private void getImageFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        getImage.launch(intent);
    }

    private String generateUniquePath() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void updateHomeImage(Bitmap bitmap){
        homeImageView.setImageBitmap(bitmap);
    }

    private boolean validateInputs(String address){
        if(address.isEmpty()){
            showToast(requireContext(),"Address can not be empty");
            return false;
        }
        return true;
    }

    private void updateHome(String title, Home home, String address, String path, int position){
        if (title.equals("Edit Home")) {
            // Update existing home
            if (home != null) {
                home.setAddress(address);
                home.setImage(path);
                homeList.set(position, home);
            }
        } else {
            // Add a new home
            Home newHome = new Home(address, path);
            homeList.add(0, newHome);
        }

        // Update the user with the modified homeList
        updateList(user, homeList, "Update Successful");
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
                AndroidUtil.hideLoadingDialog(progressDialog);
            }
        });
    }
}