package com.example.aber.Activities.Main.Fragment.Profile.Edit;

import static com.example.aber.Utils.AndroidUtil.replaceFragment;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.aber.Activities.Main.Fragment.Home.ConfirmBookingFragment;
import com.example.aber.Adapters.UserSOSAdapter;
import com.example.aber.Utils.FirebaseUtil;
import com.example.aber.Models.User.SOS;
import com.example.aber.Models.User.User;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SOSListFragment extends Fragment implements UserSOSAdapter.RecyclerViewClickListener{
    private ImageView buttonBack;
    private FirebaseUtil firebaseManager;
    private ProgressDialog progressBar;
    private String id, previous, name, address;
    private User user;
    private List<SOS> sosList;
    private UserSOSAdapter adapter;
    private PopupWindow popupWindow, confirmPopupWindow;
    private Button addButton;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressBar = new ProgressDialog(requireContext());
        AndroidUtil.showLoadingDialog(progressBar);
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_sos_list, container, false);
        firebaseManager = new FirebaseUtil();

        Bundle args = getArguments();
        if (args != null) {
            previous = args.getString("previous","");
            name = args.getString("name","");
            address = args.getString("address","");
        }

        id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseUtil.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                user = object;
                sosList = user.getEmergencyContacts();
                updateUI(sosList);
            }

            @Override
            public void onFetchFailure(String message) {

            }
        });

        buttonBack = root.findViewById(R.id.buttonBack);
        addButton = root.findViewById(R.id.add_button);

        RecyclerView recyclerView = root.findViewById(R.id.sosRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserSOSAdapter(new ArrayList<>(),this);
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
                initPopupWindow(null, "Enter Additional SOS", 0);
                    popupWindow.showAsDropDown(root, 0, 0);
            }
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<SOS> sosList) {
        if (sosList != null) {
            adapter.setSosList(sosList);
            adapter.notifyDataSetChanged();
        } else {
            adapter.setSosList(new ArrayList<>());
            adapter.notifyDataSetChanged();
        }
        AndroidUtil.hideLoadingDialog(progressBar);
    }

    @Override
    public void onSetDefaultButtonClick(int position) {
        if (position > 0 && position < sosList.size()) {
            AndroidUtil.showLoadingDialog(progressBar);
            SOS selectedContact = sosList.get(position);
            sosList.remove(position);
            sosList.add(0, selectedContact);
            updateList(user, sosList,"This Vehicle is set to default." );
        } else {
            AndroidUtil.showToast(getContext(), "Error! Please Try Again.");
        }

    }

    @Override
    public void onEditButtonClicked(int position) {
        SOS selectedContact = sosList.get(position);
        initPopupWindow(selectedContact, "Edit SOS", position);
        popupWindow.showAsDropDown(root, 0, 0);
        updateUI(sosList);
    }

    @Override
    public void onDeleteButtonClicked(int position) {
        initConfirmPopupWindow("Confirm Delete", "Are you sure you want to delete this sos?", position);
    }

    public void initConfirmPopupWindow(String title, String detail, int position) {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_confirm_dialog, null);

        confirmPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        confirmPopupWindow.setTouchable(true);
        // Set the background color with alpha transparency
        popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

        TextView titleTextVIew = popupView.findViewById(R.id.title);
        TextView detailTextView = popupView.findViewById(R.id.detail_text_view);
        Button confirmButton = popupView.findViewById(R.id.confirm_button);
        Button cancelBtn = popupView.findViewById(R.id.cancel_button);

        titleTextVIew.setText(title);
        detailTextView.setText(detail);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPopupWindow.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position != 0) {
                    sosList.remove(position);
                    updateList(user, sosList, "Delete Home Successful");
                    confirmPopupWindow.dismiss();
                } else {
                    showToast( requireContext(),"Your SOS cannot be empty");
                }
            }
        });

        confirmPopupWindow.showAsDropDown(root, 0, 0);
    }

    public void initPopupWindow(SOS sos, String title, int position) {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_sos_form, null);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        // Set the background color with alpha transparency
        popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

        TextView titleTextView = popupView.findViewById(R.id.title);
        EditText phoneEditText = popupView.findViewById(R.id.phone_edit_text);
        EditText nameEditText = popupView.findViewById(R.id.name_edit_text);
        Button submitButton = popupView.findViewById(R.id.submitNewSOSBtn);
        ImageView cancelBtn = popupView.findViewById(R.id.cancelBtn);



        titleTextView.setText(title);
        //Initialize seat capacity spinner
        if (sos != null) {
            phoneEditText.setText(sos.getPhoneNumber());
            nameEditText.setText(sos.getName());
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
                boolean update = false;
                AndroidUtil.showLoadingDialog(progressBar);

                String phone = phoneEditText.getText().toString();
                String name = nameEditText.getText().toString();

                if (title.equals("Edit SOS")) {
                    // Update existing SOS
                    if (!name.isEmpty() && !(phone.length() < 10)) {
                        if (sos != null) {
                            sos.setName(name);
                            sos.setPhoneNumber(phone);
                            update = true;
                        }
                    } else {
                        phoneEditText.setError("Phone number is invalid");
                        nameEditText.setError("Name is invalid");
                        AndroidUtil.hideLoadingDialog(progressBar);
                    }
                } else {
                    if (!name.isEmpty() && !(phone.length() < 10)) {
                        SOS newSOS = new SOS(name, phone);
                        update = true;
                        sosList.add(0, newSOS);
                    } else {
                        phoneEditText.setError("Phone number is invalid");
                        nameEditText.setError("Name is invalid");
                        AndroidUtil.hideLoadingDialog(progressBar);
                    }
                }

                if (update) {
                    updateList(user, sosList, "Update Successful");

                    // Dismiss the PopupWindow after updating
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.showAsDropDown(root, 0, 0);

    }

    private void updateList(User user, List<SOS> sosList, String successMessage){
        user.setEmergencyContacts(sosList);
        firebaseManager.updateUser(id, user, new FirebaseUtil.OnTaskCompleteListener() {
            @Override
            public void onTaskSuccess(String message) {
                AndroidUtil.showToast(getContext(), successMessage);
                updateUI(sosList);
            }

            @Override
            public void onTaskFailure(String message) {
                AndroidUtil.showToast(getContext(), message);
                AndroidUtil.hideLoadingDialog(progressBar);
            }
        });
    }

    public void newVehicle(View view) {
    }


}