package com.example.aber.Adapters;

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

import com.example.aber.Utils.FirebaseUtil;
import com.example.aber.Models.User.Home;
import com.example.aber.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UserHomeAdapter extends RecyclerView.Adapter<UserHomeAdapter.UserHomeViewHolder> {
    private List<Home> homeList;
    private RecyclerViewClickListener mListener;

    public UserHomeAdapter(List<Home> addressList, RecyclerViewClickListener listener) {
        this.homeList = addressList;
        this.mListener = listener;
    }

    public void setHomeList(List<Home> homeList){
        this.homeList = homeList;
    }

    @NonNull
    @Override
    public UserHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_address_card_view, parent, false);
        return new UserHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHomeViewHolder holder, int position) {
        Home home = homeList.get(position);
        holder.bind(home, position);
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public class UserHomeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView addressTextView,defaultTextView;
        CardView cardView;
        Button editButton, deleteButton;
        MaterialButton setDefaultButton;

        public UserHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTitle);
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

        public void bind(Home home, int position) {
            addressTextView.setText(home.getAddress());
            defaultTextView = itemView.findViewById(R.id.isDefaultTextView);
            // Set defaultTextView based on position
            if (position == 0) {
                defaultTextView.setText("Default Address");
            } else {
                defaultTextView.setText("");
            }

            addressTextView.setSelected(true);

            FirebaseUtil firebaseManager = new FirebaseUtil();
            firebaseManager.retrieveImage(home.getImage(), new FirebaseUtil.OnRetrieveImageListener() {
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
