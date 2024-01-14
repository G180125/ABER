package com.example.aber.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.Utils.FirebaseUtil;
import com.example.aber.Models.Staff.Driver;
import com.example.aber.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverChatAdapter extends RecyclerView.Adapter<DriverChatAdapter.DriverViewHolder>{
    private List<Driver> driverList;
    private DriverChatAdapter.RecyclerViewClickListener mListener;
    private FirebaseUtil firebaseManager;

    public DriverChatAdapter(List<Driver> driverList, DriverChatAdapter.RecyclerViewClickListener listener){
        this.driverList = driverList;
        this.mListener = listener;
        firebaseManager = new FirebaseUtil();
    }

    public void setDriverList(List<Driver> driverList) {
        this.driverList = driverList;
    }

    @NonNull
    @Override
    public DriverChatAdapter.DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_container, parent, false);
        return new DriverChatAdapter.DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Driver driver = driverList.get(position);
        firebaseManager.retrieveImage(driver.getAvatar(), new FirebaseUtil.OnRetrieveImageListener() {
            @Override
            public void onRetrieveImageSuccess(Bitmap bitmap) {
                holder.bind(driver, position, bitmap);
            }

            @Override
            public void onRetrieveImageFailure(String message) {
                holder.bind(driver, position, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class DriverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView avatar;
        TextView nameTextView;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            nameTextView = itemView.findViewById(R.id.name);

            // Set click listener on the itemView
            itemView.setOnClickListener(this);
        }

        public void bind(Driver driver, int position, Bitmap bitmap) {
            if(bitmap != null) {
                avatar.setImageBitmap(bitmap);
            }
            nameTextView.setText(driver.getName());
        }

        @Override
        public void onClick(View v) {
            mListener.onCardClick(getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onCardClick(int position);
    }
}

