package com.example.aber.Activities.Main.Fragment.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aber.Adapters.MessageAdapter;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.Message.MyMessage;
import com.example.aber.Models.Staff.Driver;
import com.example.aber.Models.User.User;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverChatActivity extends AppCompatActivity {
    private String id;
    private User currentUser;
    private Driver currentDriver;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private TextView nameTextView;
    private ImageView backImageView;
    private CircleImageView avatar;
    private ImageButton sendButton;
    private EditText sendText;
    private MessageAdapter messageAdapter;
    private List<MyMessage> messageList;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(DriverChatActivity.this);
        AndroidUtil.showLoadingDialog(progressDialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_chat);

        firebaseManager = new FirebaseManager();
        currentUser = new User();
        currentDriver = new Driver();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("driverID")) {
            id = extras.getString("driverID");
        }

        recyclerView = findViewById(R.id.recycler_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(DriverChatActivity.this));

        // Initialize messageAdapter with an empty list and a placeholder avatar
        messageAdapter = new MessageAdapter(new ArrayList<>(), null);
        recyclerView.setAdapter(messageAdapter);

        fetchDriver(id);

        backImageView = findViewById(R.id.back);
        avatar = findViewById(R.id.avatar);
        nameTextView = findViewById(R.id.name);
        sendText = findViewById(R.id.send_text);
        sendButton = findViewById(R.id.send_button);

        firebaseManager.readMessage(
                Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid(), id, new FirebaseManager.OnReadingMessageListener() {
                    @Override
                    public void OnMessageDataChanged(List<MyMessage> messageList) {
                        updateMessageList(messageList);
                    }
                }
        );

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sendText.getText().toString();
                if (!message.isEmpty()) {
                    String sender = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
                    firebaseManager.sendMessage(sender, id, message);
                } else {
                    AndroidUtil.showToast(DriverChatActivity.this,"You haven't typed anything");
                }
                sendText.setText("");
            }

        });
    }

    private void fetchDriver(String id){
        firebaseManager.getDriverByID(id, new FirebaseManager.OnFetchListener<Driver>() {
            @Override
            public void onFetchSuccess(Driver object) {
                currentDriver = object;
                updateUI(currentDriver);
            }

            @Override
            public void onFetchFailure(String message) {
                AndroidUtil.hideLoadingDialog(progressDialog);
                AndroidUtil.showToast(DriverChatActivity.this, message);
            }
        });
    }

    private void updateUI(Driver driver) {
        firebaseManager.retrieveImage(driver.getAvatar(), new FirebaseManager.OnRetrieveImageListener() {
            @Override
            public void onRetrieveImageSuccess(Bitmap bitmap) {
                avatar.setImageBitmap(bitmap);
                messageAdapter.setAvatar(bitmap);
                AndroidUtil.hideLoadingDialog(progressDialog);
            }

            @Override
            public void onRetrieveImageFailure(String message) {
                AndroidUtil.showToast(DriverChatActivity.this, message);
                AndroidUtil.hideLoadingDialog(progressDialog);
            }
        });

        //Get the driver captilizing the first letter (just change to driver.getName() if don't need it
        nameTextView.setText(driver.getName().substring(0,1).toUpperCase() + driver.getName().substring(1).toLowerCase());
    }

    private void updateMessageList(List<MyMessage> newMessageList) {
        messageAdapter.updateMessages(newMessageList);

        recyclerView.scrollToPosition(newMessageList.size() - 1);
    }
}