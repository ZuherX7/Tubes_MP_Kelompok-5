package com.example.tubesmp3;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentViewHolder> {

    private Context context;
    private List<PaymentMethod> paymentMethods;
    private OnPaymentMethodClickListener onPaymentMethodClickListener;
    private int selectedPosition = -1;

    public interface OnPaymentMethodClickListener {
        void onPaymentMethodClick(PaymentMethod paymentMethod, int position);
    }

    public PaymentMethodAdapter(Context context, List<PaymentMethod> paymentMethods) {
        this.context = context;
        this.paymentMethods = paymentMethods;
    }

    public void setOnPaymentMethodClickListener(OnPaymentMethodClickListener listener) {
        this.onPaymentMethodClickListener = listener;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentMethod paymentMethod = paymentMethods.get(position);

        holder.tvPaymentName.setText(paymentMethod.name);
        holder.tvPaymentType.setText(paymentMethod.type);
        holder.tvPaymentDescription.setText(paymentMethod.description);
        holder.ivPaymentIcon.setImageResource(paymentMethod.iconResource);

        // Show selection state using colors instead of drawable resources
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#E8F5E8")); // Light green
            holder.ivSelected.setVisibility(View.VISIBLE);
            // Make border effect by adding padding and background
            holder.itemView.setPadding(16, 16, 16, 16);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5")); // Light gray
            holder.ivSelected.setVisibility(View.GONE);
            holder.itemView.setPadding(16, 16, 16, 16);
        }

        // Set enabled/disabled state
        holder.itemView.setAlpha(paymentMethod.isEnabled ? 1.0f : 0.5f);
        holder.itemView.setEnabled(paymentMethod.isEnabled);

        // Set click listener
        if (paymentMethod.isEnabled) {
            holder.itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = position;

                // Notify changes for visual feedback
                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition);
                }
                notifyItemChanged(selectedPosition);

                if (onPaymentMethodClickListener != null) {
                    onPaymentMethodClickListener.onPaymentMethodClick(paymentMethod, position);
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaymentName, tvPaymentType, tvPaymentDescription;
        ImageView ivPaymentIcon, ivSelected;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaymentName = itemView.findViewById(R.id.tvPaymentName);
            tvPaymentType = itemView.findViewById(R.id.tvPaymentType);
            tvPaymentDescription = itemView.findViewById(R.id.tvPaymentDescription);
            ivPaymentIcon = itemView.findViewById(R.id.ivPaymentIcon);
            ivSelected = itemView.findViewById(R.id.ivSelected);
        }
    }

    public static class PaymentMethod {
        public String name;
        public String type;
        public int iconResource;
        public boolean isEnabled;
        public String description;

        public PaymentMethod(String name, String type, int iconResource, boolean isEnabled, String description) {
            this.name = name;
            this.type = type;
            this.iconResource = iconResource;
            this.isEnabled = isEnabled;
            this.description = description;
        }
    }
}