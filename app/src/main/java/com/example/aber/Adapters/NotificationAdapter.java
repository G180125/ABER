package com.example.aber.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.Models.Notification.InAppNotification;
import com.example.aber.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<InAppNotification> notificationList;
    private RecyclerViewClickListener mListener;


    public NotificationAdapter(List<InAppNotification> notificationList, RecyclerViewClickListener listener) {
        this.notificationList = notificationList;
        this.mListener = listener;
    }

    public void setNotificationList(List<InAppNotification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card_view, parent, false);
        return  new NotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
        InAppNotification notification = notificationList.get(position);
        holder.bind(notification, position);
    }

    @Override
    public int getItemCount() {
        return this.notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView notificationDate, notificationTitle, notificationBody;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            notificationDate = itemView.findViewById(R.id.date);
            notificationTitle = itemView.findViewById(R.id.title);
            notificationBody = itemView.findViewById(R.id.body);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onNotificationClick(getAdapterPosition());
                }
            });
        }

        public void bind(InAppNotification notification, int position){
            notificationDate.setText(notification.getDate());
            notificationTitle.setText(notification.getTitle());
            notificationBody.setText(notification.getBody());

            if(notification.getIsRead()){
                layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                layout.setBackgroundColor(Color.parseColor("#48CEDC"));
            }
        }
    }

    public interface RecyclerViewClickListener  {
        void onNotificationClick(int position);
    }
}
