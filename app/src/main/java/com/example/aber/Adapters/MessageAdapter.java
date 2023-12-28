package com.example.aber.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.FirebaseManager;
import com.example.aber.Models.Message.MyMessage;
import com.example.aber.R;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 1;

    private List<MyMessage> messageList;
    private Bitmap leftAvatar;
    private FirebaseManager firebaseManager;

    public MessageAdapter(List<MyMessage> messageList, Bitmap bitmap) {
        this.messageList = messageList;
        this.leftAvatar = bitmap;
        firebaseManager = new FirebaseManager();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateMessages(List<MyMessage> newMessages) {
        messageList.clear();
        messageList.addAll(newMessages);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAvatar(Bitmap avatar) {
        leftAvatar = avatar;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_TYPE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MyMessage message = messageList.get(position);
        holder.bind(message.getMessage(), leftAvatar);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSender().equals(Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid())) {
            return MESSAGE_TYPE_RIGHT;
        }
        return MESSAGE_TYPE_LEFT;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            messageTextView = itemView.findViewById(R.id.message);
        }

        public void bind(String message, Bitmap bitmap) {
            if(bitmap != null){
                avatar.setImageBitmap(bitmap);
            }
            messageTextView.setText(message);
        }
    }
}

