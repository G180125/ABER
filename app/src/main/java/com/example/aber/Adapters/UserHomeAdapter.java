package com.example.aber.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.Listeners.OnAddressClickListeners;
import com.example.aber.Models.User.Home;
import com.example.aber.R;

import java.util.List;

public class UserHomeAdapter extends RecyclerView.Adapter<UserHomeAdapter.UserHomeViewHolder>{
    private Context context;
    private List<Home> listOfHomes;
    private OnAddressClickListeners onAddressClickListeners;

    public UserHomeAdapter(Context context, List<Home> listOfHomes, OnAddressClickListeners onAddressClickListeners) {
        this.context = context;
        this.listOfHomes = listOfHomes;
        this.onAddressClickListeners = onAddressClickListeners;
    }

    @NonNull
    @Override
    public UserHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull UserHomeViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    public class UserHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView addressPostion, addressInfo;
        public UserHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            addressInfo = itemView.findViewById(R.id.address_edit_text);

        }

        public void bind(Home home) {
            addressInfo.setText(home.getAddress());
        }
        @Override
        public void onClick(View v) {

        }
    }


}









