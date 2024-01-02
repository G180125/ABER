package com.example.aber.Adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.SOS;
import com.example.aber.Models.User.Vehicle;
import com.example.aber.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UserSOSAdapter extends RecyclerView.Adapter<UserSOSAdapter.UserSOSViewHolder>{
    private List<SOS> sosList;
    private RecyclerViewClickListener mListener;

    public UserSOSAdapter(List<SOS> sosList, RecyclerViewClickListener listener) {
        this.sosList = sosList;
        this.mListener = listener;
    }

    public void setSosList(List<SOS> sosList){
        this.sosList = sosList;
    }

    @NonNull
    @Override
    public UserSOSAdapter.UserSOSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_sos_card_view, parent, false);
        return new UserSOSAdapter.UserSOSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSOSAdapter.UserSOSViewHolder holder, int position) {
        SOS sos = sosList.get(position);
        holder.bind(sos, position);
    }

    @Override
    public int getItemCount() {
        return sosList.size();
    }

    public class UserSOSViewHolder extends RecyclerView.ViewHolder {
        TextView nameTExtVIew;
        Button editButton, deleteButton;
        MaterialButton setDefaultButton;
        public UserSOSViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTExtVIew = itemView.findViewById(R.id.isDefaultTextView);
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

        public void bind(SOS sos, int position) {
            nameTExtVIew.setText(sos.getName());
        }
    }

    public interface RecyclerViewClickListener  {
        void onSetDefaultButtonClick(int position);
        void onEditButtonClicked(int position);
        void onDeleteButtonClicked(int position);
    }
}
