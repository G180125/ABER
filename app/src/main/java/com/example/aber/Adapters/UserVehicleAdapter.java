package com.example.aber.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.Models.User.Vehicle;
import com.example.aber.R;

import java.util.List;

public class UserVehicleAdapter extends RecyclerView.Adapter<UserVehicleAdapter.UserVehicleViewHolder>{
    private Context context;
    private List<Vehicle> vehicleList;

    public UserVehicleAdapter(Context context, List<Vehicle> vehicleList) {
        this.context = context;
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public UserVehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_vehicle_card_view, parent, false);
        return new UserVehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        holder.vehicleNameTextView.setText(vehicle.getName());
        holder.cardView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public class UserVehicleViewHolder extends RecyclerView.ViewHolder {

        TextView vehicleNameTextView;
        CardView cardView;

        public UserVehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            vehicleNameTextView = itemView.findViewById(R.id.vehicleName);
            vehicleNameTextView.setSelected(true);
            cardView = itemView.findViewById(R.id.vehicle_list_container);
        }
    }
}
