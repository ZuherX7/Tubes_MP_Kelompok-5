package com.example.tubesmp3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private Context context;
    private List<BookingHistoryManager.Booking> bookingList;

    public BookingHistoryAdapter(Context context, List<BookingHistoryManager.Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingHistoryManager.Booking booking = bookingList.get(position);

        holder.tvCarName.setText(booking.carName);
        holder.tvBookingDate.setText("Booked: " + booking.bookingDate);
        holder.tvRentalPeriod.setText(booking.fromDate + " - " + booking.untilDate);
        holder.tvDuration.setText(booking.duration + " day(s)");
        holder.tvTotalPrice.setText("Rp " + String.format("%,d", booking.totalPrice));
        holder.tvStatus.setText(booking.status.toUpperCase());
        holder.ivCarImage.setImageResource(booking.carImageResource);

        // Set status color
        int statusColor;
        switch (booking.status) {
            case "active":
                statusColor = 0xFF4CAF50; // Green
                break;
            case "completed":
                statusColor = 0xFF2196F3; // Blue
                break;
            case "cancelled":
                statusColor = 0xFFF44336; // Red
                break;
            default:
                statusColor = 0xFF666666; // Gray
        }
        holder.tvStatus.setTextColor(statusColor);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateBookings(List<BookingHistoryManager.Booking> newBookings) {
        this.bookingList = newBookings;
        notifyDataSetChanged();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvCarName, tvBookingDate, tvRentalPeriod, tvDuration, tvTotalPrice, tvStatus;
        ImageView ivCarImage;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvRentalPeriod = itemView.findViewById(R.id.tvRentalPeriod);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
        }
    }
}