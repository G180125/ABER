package com.example.aber.Activities.Main.Fragment.Profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aber.Adapters.MessageAdapter;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.Message.MyMessage;
import com.example.aber.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileChatFragment extends Fragment {
    private final String ADMIN_ID ="u0SkgoA4j5YboEVkP4qXQWIXFrY2";
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private TextView nameTextView;
    private ImageView backImageView;
    private ImageButton sendButton;
    private EditText sendText;
    private MessageAdapter messageAdapter;
    private List<MyMessage> messageList;
    private RecyclerView recyclerView;
    private boolean firstLoad;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showLoadingDialog();
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_profile_chat, container, false);
        firebaseManager = new FirebaseManager();
        recyclerView = root.findViewById(R.id.recycler_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        messageAdapter = new MessageAdapter(new ArrayList<>(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_admin));
        recyclerView.setAdapter(messageAdapter);

        String userID = firebaseManager.mAuth.getCurrentUser().getUid();
        backImageView = root.findViewById(R.id.back);
        nameTextView = root.findViewById(R.id.name);
        sendText = root.findViewById(R.id.send_text);
        sendButton = root.findViewById(R.id.send_button);

        nameTextView.setText("Admin");
        firstLoad = true;

        firebaseManager.readMessage(userID, ADMIN_ID, new FirebaseManager.OnReadingMessageListener() {
                    @Override
                    public void OnMessageDataChanged(List<MyMessage> messageList) {
                        updateMessageList(messageList);
                    }
                }
        );

        backImageView.setOnClickListener(new View.OnClickListener() {
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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sendText.getText().toString();
                if (!message.isEmpty()) {
                    String sender = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
                    firebaseManager.sendMessage(sender, ADMIN_ID, message);
                } else {
                    showToast("You haven't typed anything");
                }
                sendText.setText("");
            }

        });

        return root;
    }

    private void updateMessageList(List<MyMessage> newMessageList) {
        messageAdapter.updateMessages(newMessageList);
        recyclerView.scrollToPosition(newMessageList.size() - 1);
        if(firstLoad){
            hideLoadingDialog();
            firstLoad = false;
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

    private void showToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}