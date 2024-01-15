package com.example.aber.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aber.Models.Booking.Booking;
import com.example.aber.R;

import java.util.List;
import java.util.Objects;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookingList;
    private RecyclerViewClickListener mListener;

    public BookingAdapter(List<Booking> bookingList, RecyclerViewClickListener listener) {
        this.bookingList = bookingList;
        this.mListener = listener;
    }

    public void setBookingList(List<Booking> bookingList){
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingAdapter.BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_card_view, parent, false);
        return new BookingAdapter.BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking, position);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder{
        TextView pickUpTextView, destinationTextView, bookingTimeTextView, vehicleTextView, statusTextView;
        Button viewButton, cancelButton;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            pickUpTextView = itemView.findViewById(R.id.pick_up);
            destinationTextView = itemView.findViewById(R.id.destination);
            bookingTimeTextView = itemView.findViewById(R.id.booking_time);
            vehicleTextView = itemView.findViewById(R.id.vehicle);
            statusTextView = itemView.findViewById(R.id.status);
            viewButton = itemView.findViewById(R.id.view_button);
            cancelButton = itemView.findViewById(R.id.cancel_button);

            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onViewButtonClick(getAdapterPosition());
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCancelButtonClicked(getAdapterPosition());
                }
            });
        }

        public void bind(Booking booking, int position) {
            pickUpTextView.setText(booking.getPickUp().getAddress());
            destinationTextView.setText(booking.getDestination().getAddress());
            bookingTimeTextView.setText(booking.getBookingTime());
            vehicleTextView.setText(booking.getVehicle().getNumberPlate());

            if(Objects.equals(booking.getStatus(), "Cancel")){
                // Color red for cancel status
                statusTextView.setTextColor(Color.parseColor("#FA3737"));
            } else if(Objects.equals(booking.getStatus(), "Picked Up")){
                //Color orange for pick up status
                statusTextView.setTextColor(Color.parseColor("#EC5109"));
            } else if(Objects.equals(booking.getStatus(), "Pending")){
                //Color yellow for Pending
                statusTextView.setTextColor(Color.parseColor("#FFC107"));
            } else if(Objects.equals(booking.getStatus(), "Done") || Objects.equals(booking.getStatus(), "Driver Accepted")){
                statusTextView.setTextColor(Color.parseColor("##4CAF50"));
            }

            statusTextView.setText(booking.getStatus());

            pickUpTextView.setSelected(true);
            pickUpTextView.setFocusable(true);
            destinationTextView.setFocusable(true);
            destinationTextView.setSelected(true);

        }
    }

    public interface RecyclerViewClickListener  {
        void onViewButtonClick(int position);
        void onCancelButtonClicked(int position);
    }
}
