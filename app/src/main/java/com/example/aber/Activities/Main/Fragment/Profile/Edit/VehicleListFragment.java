package com.example.aber.Activities.Main.Fragment.Profile.Edit;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.aber.Activities.Main.Fragment.Profile.Edit.ProfileEditFragment;
import com.example.aber.Adapters.UserVehicleAdapter;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.User;
import com.example.aber.Models.User.Vehicle;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class VehicleListFragment extends Fragment implements UserVehicleAdapter.RecyclerViewClickListener{
    private ImageView buttonBack;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressBar;
    private String id;
    private User user;
    private List<Vehicle> vehicleList;
    private UserVehicleAdapter adapter;
    private PopupWindow popupWindow;
    private Button addButton;
    private View root;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressBar = new ProgressDialog(requireContext());
        AndroidUtil.showLoadingDialog(progressBar);
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        firebaseManager = new FirebaseManager();

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
                replaceFragment(new ProfileEditFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
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
        adapter.setVehicleList(vehicleList);
        adapter.notifyDataSetChanged();
        AndroidUtil.hideLoadingDialog(progressBar);
    }

    @Override
    public void onSetDefaultButtonClick(int position) {
        if (position > 0 && position < vehicleList.size()) {
            AndroidUtil.showLoadingDialog(progressBar);
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
        ImageView vehicleImageView = popupView.findViewById(R.id.vehicle_image_view);
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

                String brand = brandEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String color = colorEditText.getText().toString();
                String selectedSeatCapacity = seatCapacitySpinner.getSelectedItem().toString();
                String plate = plateEditText.getText().toString();

                if (title.equals("Edit Vehicle")) {
                    // Update existing home
                    if (vehicle != null) {
                        vehicle.setBrand(brand);
                        vehicle.setName(name);
                        vehicle.setColor(color);
                        vehicle.setSeatCapacity(selectedSeatCapacity);
                        vehicle.setNumberPlate(plate);

                        vehicleList.set(position, vehicle);
                    }
                } else {

                    Vehicle newVehicle = new Vehicle(brand, name, color, selectedSeatCapacity, plate, new ArrayList<>());
                    vehicleList.add(0, newVehicle);
                }


                updateList(user, vehicleList, "Update Successful");

                // Dismiss the PopupWindow after updating
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(root, 0, 0);

    }

    private void updateList(User user, List<Vehicle> vehicleList, String successMessage){
        user.setVehicles(vehicleList);
        firebaseManager.updateUser(id, user, new FirebaseManager.OnTaskCompleteListener() {
            @Override
            public void onTaskSuccess(String message) {
                AndroidUtil.showToast(getContext(), successMessage);
                updateUI(vehicleList);
            }

            @Override
            public void onTaskFailure(String message) {
                AndroidUtil.showToast(getContext(), message);
                AndroidUtil.hideLoadingDialog(progressBar);
            }
        });
    }
}