package com.example.aber.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.Models.User.Home;
import com.example.aber.R;

import java.util.List;

public class UserHomeAdapter extends RecyclerView.Adapter<UserHomeAdapter.UserHomeViewHolder> {
    private Context context;
    private List<Home> addressList;

    public UserHomeAdapter(Context context, List<Home> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public UserHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_up_address, parent, false);
        return new UserHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHomeViewHolder holder, int position) {
        Home home = addressList.get(position);


        holder.addressTextView.setText(home.getAddress());

        holder.cardView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class UserHomeViewHolder extends RecyclerView.ViewHolder {

        TextView addressTextView;




        CardView cardView;

        public UserHomeViewHolder(@NonNull View itemView) {
            super(itemView);

            addressTextView = itemView.findViewById(R.id.addressTitle);

            addressTextView.setSelected(true);
            cardView = itemView.findViewById(R.id.address_list_container);
        }
    }
}
