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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.aber.Activities.Main.Fragment.Home.ConfirmBookingFragment;
import com.example.aber.Adapters.UserVehicleAdapter;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.User;
import com.example.aber.Models.User.Vehicle;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class VehicleListFragment extends Fragment implements UserVehicleAdapter.RecyclerViewClickListener{
    private final String[] PROVINCE_NUMBER = {
            "11", "65", "12", "66", "14", "67", "15", "16", "68", "17",
            "69", "18", "70", "19", "71", "20", "72", "21", "73", "22",
            "74", "23", "75", "24", "76", "25", "77", "26", "78", "27",
            "79", "28", "80", "29", "30", "31", "32", "33", "40", "81",
            "34", "82", "35", "83", "36", "84", "37", "85", "38", "86",
            "43", "88", "47", "89", "48", "90", "49", "92", "41", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59", "93",
            "39", "60", "94", "61", "95", "62", "97", "63", "98", "64", "99"
    };
    private static final String STORAGE_PATH = "vehicle/";
    private ImageView buttonBack;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private String id, previous, name, address, imagePath;
    private User user;
    private List<Vehicle> vehicleList;
    private UserVehicleAdapter adapter;
    private PopupWindow popupWindow;
    private Button addButton;
    private View root;
    private Bitmap cropped;
    private ImageView vehicleImageView;
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
            updateVehicleImage(cropped);
        }
    });
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
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
                vehicleList = user.getVehicles();
                updateUI(vehicleList);
            }

            @Override
            public void onFetchFailure(String message) {

            }
        });

        buttonBack = root.findViewById(R.id.buttonBack);
        addButton = root.findViewById(R.id.add_button);

        RecyclerView recyclerView = root.findViewById(R.id.vehicleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserVehicleAdapter(new ArrayList<>(),this);
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
                initPopupWindow(null, "Enter Additional Vehicle", 0);
                popupWindow.showAsDropDown(root, 0, 0);
            }
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<Vehicle> vehicleList){
        AndroidUtil.showLoadingDialog(progressDialog);
        adapter.setVehicleList(vehicleList);
        adapter.notifyDataSetChanged();
        AndroidUtil.hideLoadingDialog(progressDialog);
    }

    @Override
    public void onSetDefaultButtonClick(int position) {
        if (position > 0 && position < vehicleList.size()) {
            AndroidUtil.showLoadingDialog(progressDialog);
            Vehicle selectedVehicle = vehicleList.get(position);
            vehicleList.remove(position);
            vehicleList.add(0, selectedVehicle);
            updateList(user, vehicleList,"This Vehicle is set to default." );
        } else {
            AndroidUtil.showToast(getContext(), "Error! Please Try Again.");
        }
    }

    @Override
    public void onEditButtonClicked(int position) {
        Vehicle vehicle = vehicleList.get(position);
        initPopupWindow(vehicle, "Edit Vehicle", position);
        popupWindow.showAsDropDown(root, 0, 0);
        updateUI(vehicleList);
    }

    @Override
    public void onDeleteButtonClicked(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this vehicle?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        vehicleList.remove(position);
                        updateList(user, vehicleList, "Delete Home Successful");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing
                    }
                })
                .show();
    }

    public void initPopupWindow(Vehicle vehicle, String title, int position) {
        String[] seatCapacityOptions = getResources().getStringArray(R.array.seat_capacity_options);
        List<String> seatCapacitySpinnerValues = Arrays.asList(seatCapacityOptions);

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_vehicle_form, null);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        // Set the background color with alpha transparency
        popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

        TextView titleTextView = popupView.findViewById(R.id.title);
        EditText brandEditText = popupView.findViewById(R.id.vehicle_brand_edit_text);
        EditText nameEditText = popupView.findViewById(R.id.vehicle_name_edit_text);
        EditText colorEditText = popupView.findViewById(R.id.vehicle_color_edit_text);
        Spinner seatCapacitySpinner = popupView.findViewById(R.id.seat_capacity_spinner);
        EditText plateEditText = popupView.findViewById(R.id.vehicle_number_plate_edit_text);
        vehicleImageView = popupView.findViewById(R.id.vehicle_image_view);
        Button submitButton = popupView.findViewById(R.id.submitNewVehicleBtn);
        ImageView cancelBtn = popupView.findViewById(R.id.cancelBtn);

        titleTextView.setText(title);
        //Initialize seat capacity spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.seat_capacity_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seatCapacitySpinner.setAdapter(adapter);

        if (vehicle != null) {
            brandEditText.setText(vehicle.getBrand());
            nameEditText.setText(vehicle.getName());
            colorEditText.setText(vehicle.getColor());
            plateEditText.setText(vehicle.getNumberPlate());

            int selectionIndex = seatCapacitySpinnerValues.indexOf(String.valueOf(vehicle.getSeatCapacity()));
            seatCapacitySpinner.setSelection(selectionIndex);
            firebaseManager.retrieveImage(vehicle.getImages().get(0), new FirebaseManager.OnRetrieveImageListener() {
                @Override
                public void onRetrieveImageSuccess(Bitmap bitmap) {
                    vehicleImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onRetrieveImageFailure(String message) {

                }
            });
        }

        vehicleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showLoadingDialog(progressDialog);

                String brand = brandEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String color = colorEditText.getText().toString();
                String selectedSeatCapacity = seatCapacitySpinner.getSelectedItem().toString();
                String plate = plateEditText.getText().toString();

                if(title.equals("Edit Vehicle")){
                    handeEdit(brand, name, color,selectedSeatCapacity, plate, cropped, vehicle, position);
                } else {
                    handleAdd(brand, name, color, selectedSeatCapacity, plate, cropped);
                }
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

    private void updateVehicleImage(Bitmap bitmap){
        vehicleImageView.setImageBitmap(bitmap);
    }

    private boolean validateInputs(String brand, String name, String color, String selectedSeatCapacity, String plate){
        if(brand.isEmpty()){
            showToast(requireContext(),"Vehicle Brand can not be empty");
            return false;
        }
        if(name.isEmpty()){
            showToast(requireContext(),"Vehicle Name can not be empty");
            return false;
        }
        if(color.isEmpty()){
            showToast(requireContext(),"Vehicle Color can not be empty");
            return false;
        }
        if(selectedSeatCapacity.isEmpty()){
            showToast(requireContext(),"Vehicle Seat Capacity can not be empty");
            return false;
        }
        return validatePlate(plate);
    }

    private boolean isDataChanged(String brand, String name, String color, String selectedSeatCapacity, String plate, Vehicle vehicle, Bitmap cropped) {
        // Check if any of the details are changed or the image is changed
        return !brand.equals(vehicle.getBrand()) ||
                !name.equals(vehicle.getName()) ||
                !color.equals(vehicle.getColor()) ||
                !selectedSeatCapacity.equals(vehicle.getSeatCapacity()) ||
                !plate.equals(vehicle.getNumberPlate()) ||
                cropped != null;
    }


    private boolean validatePlate(String plate) {
        String plateRegex = "^[0-9]{2}-[A-Z]\\s[0-9]{5}$";

        if (plate.matches(plateRegex)) {
            String provinceNumber = plate.substring(0, 2);
            if (isValidProvinceNumber(provinceNumber)) {
                return true;
            } else {
                showToast(requireContext(),"Invalid province number");
                return false;
            }
        } else {
            showToast(requireContext(),"Invalid plate format");
            return false;
        }
    }

    private boolean isValidProvinceNumber(String provinceNumber) {
        for (String validProvince : PROVINCE_NUMBER) {
            if (validProvince.equals(provinceNumber)) {
                return true;
            }
        }
        return false;
    }

    private void handleAdd(String brand, String name, String color, String selectedSeatCapacity, String plate, Bitmap cropped){
        if(cropped == null){
            showToast(requireContext(),"You have to upload an image for your vehicle");
            AndroidUtil.hideLoadingDialog(progressDialog);
            return;
        }

        if(validateInputs(brand, name, color, selectedSeatCapacity, plate)){
            imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
            firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                @Override
                public void onTaskSuccess(String message) {
                    List<String> images = new ArrayList<>();
                    images.add(imagePath);
                    Vehicle vehicle = new Vehicle(brand, name, color, selectedSeatCapacity, plate, images);
                    vehicleList.add(0,vehicle);
                    updateList(user, vehicleList, "Add new vehicle successful");
                }

                @Override
                public void onTaskFailure(String message) {
                    AndroidUtil.hideLoadingDialog(progressDialog);
                    showToast(requireContext(), "Upload Image failed");
                }
            });
        }
        AndroidUtil.hideLoadingDialog(progressDialog);
    }

    private void handeEdit(String brand, String name, String color, String selectedSeatCapacity, String plate, Bitmap cropped, Vehicle vehicle, int position){
        if(!validateInputs(brand, name, color, selectedSeatCapacity, plate)){
            AndroidUtil.hideLoadingDialog(progressDialog);
            return;
        }

        if(!isDataChanged(brand, name, color, selectedSeatCapacity, plate, vehicle, cropped)){
            showToast(requireContext(),"You haven't changed anything");
            AndroidUtil.hideLoadingDialog(progressDialog);
            return;
        }

        if(cropped != null){
            imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
            firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                @Override
                public void onTaskSuccess(String message) {
                    vehicle.getImages().set(0, imagePath);
                    vehicle.setBrand(brand);
                    vehicle.setName(name);
                    vehicle.setColor(color);
                    vehicle.setSeatCapacity(selectedSeatCapacity);
                    vehicle.setNumberPlate(plate);
                    vehicleList.set(position, vehicle);
                    updateList(user, vehicleList, "Update Successful");
                }

                @Override
                public void onTaskFailure(String message) {
                    AndroidUtil.hideLoadingDialog(progressDialog);
                    showToast(requireContext(), "Upload Image failed");
                }
            });
        } else {
            vehicle.setBrand(brand);
            vehicle.setName(name);
            vehicle.setColor(color);
            vehicle.setSeatCapacity(selectedSeatCapacity);
            vehicle.setNumberPlate(plate);
            vehicleList.set(position, vehicle);
            updateList(user, vehicleList, "Update Successful");
        }
    }

    private void updateList(User user, List<Vehicle> vehicleList, String successMessage){
        user.setVehicles(vehicleList);
        firebaseManager.updateUser(id, user, new FirebaseManager.OnTaskCompleteListener() {
            @Override
            public void onTaskSuccess(String message) {
                AndroidUtil.showToast(getContext(), successMessage);
                AndroidUtil.hideLoadingDialog(progressDialog);
                // Dismiss the PopupWindow after updating
                popupWindow.dismiss();
                updateUI(vehicleList);
            }

            @Override
            public void onTaskFailure(String message) {
                AndroidUtil.showToast(getContext(), message);
                AndroidUtil.hideLoadingDialog(progressDialog);
            }
        });
    }
}