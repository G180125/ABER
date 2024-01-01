package com.example.aber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aber.Activities.Main.Fragment.Chat.DriverChatListFragment;
import com.example.aber.Activities.Main.Fragment.Profile.MainProfileFragment;
import com.example.aber.Activities.Main.MainActivity;
import com.example.aber.Adapters.MessageAdapter;
import com.example.aber.Models.Message.MyMessage;
import com.example.aber.Utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelpActivity extends AppCompatActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(HelpActivity.this);
        AndroidUtil.showLoadingDialog(progressDialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        firebaseManager = new FirebaseManager();
        recyclerView = findViewById(R.id.recycler_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(HelpActivity.this));
        messageAdapter = new MessageAdapter(new ArrayList<>(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_admin));
        recyclerView.setAdapter(messageAdapter);

        String userID = firebaseManager.mAuth.getCurrentUser().getUid();
        backImageView = findViewById(R.id.back);
        nameTextView = findViewById(R.id.name);
        sendText = findViewById(R.id.send_text);
        sendButton = findViewById(R.id.send_button);

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
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_main_chat_container, new MainProfileFragment())
                        .addToBackStack(null)
                        .commit();
                finish();
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
                    AndroidUtil.showToast(HelpActivity.this,"You haven't typed anything");
                }
                sendText.setText("");
            }

        });
    }

    private void updateMessageList(List<MyMessage> newMessageList) {
        messageAdapter.updateMessages(newMessageList);
        recyclerView.scrollToPosition(newMessageList.size() - 1);
        if(firstLoad){
            AndroidUtil.hideLoadingDialog(progressDialog);
            firstLoad = false;
        }
    }
}