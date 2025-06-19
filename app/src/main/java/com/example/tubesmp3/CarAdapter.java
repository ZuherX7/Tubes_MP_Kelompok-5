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

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private Context context;
    private List<CarModel> carList;
    private boolean isAvailable;
    private OnCarClickListener onCarClickListener;
    private int selectedPosition = -1;

    public interface OnCarClickListener {
        void onCarClick(CarModel car, int position);
    }

    public CarAdapter(Context context, List<CarModel> carList, boolean isAvailable) {
        this.context = context;
        this.carList = carList;
        this.isAvailable = isAvailable;
    }

    public void setOnCarClickListener(OnCarClickListener listener) {
        this.onCarClickListener = listener;
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;

        // Notify changes for visual feedback
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isAvailable ? R.layout.item_car : R.layout.item_car_unavailable;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarModel car = carList.get(position);
        holder.tvCarName.setText(car.name);
        holder.tvRating.setText("⭐ " + car.rating);

        // Format price to match the image (show as "Rp. X.XXX.XXX/Hari")
        holder.tvPrice.setText("Rp. " + car.price + "/Hari");
        holder.ivCarImage.setImageResource(car.imageResource);

        // Highlight selected item
        if (position == selectedPosition && isAvailable) {
            holder.itemView.setBackgroundColor(0xFF0F4137); // Green theme color
            holder.itemView.setAlpha(1.0f);
            // Change text colors for better contrast
            holder.tvCarName.setTextColor(0xFFFFFFFF);
            holder.tvRating.setTextColor(0xFFFFFFFF);
            holder.tvPrice.setTextColor(0xFFFFFFFF);
        } else {
            holder.itemView.setBackgroundColor(0xFFEDEDED); // Original background
            if (!isAvailable) {
                holder.itemView.setAlpha(0.5f);
            } else {
                holder.itemView.setAlpha(1.0f);
            }
            // Original text colors
            holder.tvCarName.setTextColor(0xFF545454);
            holder.tvRating.setTextColor(0xFF545454);
            holder.tvPrice.setTextColor(0xFF545454);
        }

        // Set click listener only for available cars
        if (isAvailable) {
            holder.itemView.setOnClickListener(v -> {
                if (onCarClickListener != null) {
                    setSelectedPosition(position);
                    onCarClickListener.onCarClick(car, position);
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public class CarViewHolder extends RecyclerView.ViewHolder {
        TextView tvCarName, tvRating, tvPrice;
        ImageView ivCarImage;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
        }
    }

    // Enhanced model class with availability status
// Update CarAdapter.java - tambahkan implements Serializable pada CarModel class

    public static class CarModel implements java.io.Serializable {
        public String name;
        public String rating;
        public String price;
        public int imageResource;
        public boolean isAvailable;

        public CarModel(String name, String rating, String price, int imageResource, boolean isAvailable) {
            this.name = name;
            this.rating = rating;
            this.price = price;
            this.imageResource = imageResource;
            this.isAvailable = isAvailable;
        }
    }
}