package com.example.aber.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.Vehicle;
import com.example.aber.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UserVehicleAdapter extends RecyclerView.Adapter<UserVehicleAdapter.UserVehicleViewHolder>{
    private List<Vehicle> vehicleList;
    private RecyclerViewClickListener mListener;

    public UserVehicleAdapter(List<Vehicle> vehicleList, RecyclerViewClickListener listener) {
        this.vehicleList = vehicleList;
        this.mListener = listener;
    }

    public void setVehicleList(List<Vehicle> vehicleList){
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public UserVehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_vehicle_card_view, parent, false);
        return new UserVehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.bind(vehicle, position);
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public class UserVehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView plateTextView;
        CardView cardView;
        Button editButton, deleteButton;
        MaterialButton setDefaultButton;

        public UserVehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            plateTextView = itemView.findViewById(R.id.vehiclePlate);
            imageView = itemView.findViewById(R.id.dishImageView);
            cardView = itemView.findViewById(R.id.address_list_container);
            setDefaultButton = itemView.findViewById(R.id.setDefaultBtn);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);

            setDefaultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSetDefaultButtonClick(getAdapterPosition());
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onEditButtonClicked(getAdapterPosition());
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDeleteButtonClicked(getAdapterPosition());
                }
            });
        }

        public void bind(Vehicle vehicle, int position) {
            plateTextView.setText(vehicle.getNumberPlate());

            FirebaseManager firebaseManager = new FirebaseManager();
            firebaseManager.retrieveImage(vehicle.getImages().get(0), new FirebaseManager.OnRetrieveImageListener() {
                @Override
                public void onRetrieveImageSuccess(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void onRetrieveImageFailure(String message) {

                }
            });
        }
    }

    public interface RecyclerViewClickListener  {
        void onSetDefaultButtonClick(int position);
        void onEditButtonClicked(int position);
        void onDeleteButtonClicked(int position);
    }
}
