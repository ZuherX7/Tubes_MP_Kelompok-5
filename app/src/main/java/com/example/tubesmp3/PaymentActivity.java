package com.example.tubesmp3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvCarName, tvCarPrice, tvRentalPeriod, tvDuration, tvTotalPrice;
    private ImageView ivCarImage, btnBack;
    private RecyclerView recyclerPaymentMethods;
    private Button btnPayNow;

    private PaymentMethodAdapter paymentAdapter;
    private BookingHistoryManager.Booking currentBooking;
    private PaymentMethodAdapter.PaymentMethod selectedPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initializeViews();
        getBookingData();
        setupPaymentMethods();
        setupClickListeners();
        updateBookingInfo();
    }

    private void initializeViews() {
        // Booking info views
        tvCarName = findViewById(R.id.tvCarName);
        tvCarPrice = findViewById(R.id.tvCarPrice);
        tvRentalPeriod = findViewById(R.id.tvRentalPeriod);
        tvDuration = findViewById(R.id.tvDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        ivCarImage = findViewById(R.id.ivCarImage);

        // Navigation
        btnBack = findViewById(R.id.btnBack);

        // Payment methods
        recyclerPaymentMethods = findViewById(R.id.recyclerPaymentMethods);

        // Action button
        btnPayNow = findViewById(R.id.btnPayNow);
    }

    private void getBookingData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("booking_data")) {
            // Get booking data from intent
            String carName = intent.getStringExtra("car_name");
            String carRating = intent.getStringExtra("car_rating");
            String carPrice = intent.getStringExtra("car_price");
            int carImageResource = intent.getIntExtra("car_image", R.drawable.avanza);
            String fromDate = intent.getStringExtra("from_date");
            String untilDate = intent.getStringExtra("until_date");
            String time = intent.getStringExtra("time");
            long totalPrice = intent.getLongExtra("total_price", 0);
            int duration = intent.getIntExtra("duration", 1);
            String bookingDate = intent.getStringExtra("booking_date");

            currentBooking = new BookingHistoryManager.Booking(
                    carName, carRating, carPrice, carImageResource,
                    fromDate, untilDate, time, totalPrice, duration, bookingDate, "pending_payment"
            );
        }
    }

    private void updateBookingInfo() {
        if (currentBooking != null) {
            tvCarName.setText(currentBooking.carName);
            tvCarPrice.setText("Rp " + currentBooking.carPrice + "/day");
            tvRentalPeriod.setText(currentBooking.fromDate + " - " + currentBooking.untilDate);
            tvDuration.setText(currentBooking.duration + " day(s)");
            tvTotalPrice.setText("Rp " + String.format("%,d", currentBooking.totalPrice));
            ivCarImage.setImageResource(currentBooking.carImageResource);
        }
    }

    private void setupPaymentMethods() {
        recyclerPaymentMethods.setLayoutManager(new LinearLayoutManager(this));

        List<PaymentMethodAdapter.PaymentMethod> paymentMethods = getPaymentMethods();
        paymentAdapter = new PaymentMethodAdapter(this, paymentMethods);

        paymentAdapter.setOnPaymentMethodClickListener((paymentMethod, position) -> {
            selectedPaymentMethod = paymentMethod;
            updatePayButton();
        });

        recyclerPaymentMethods.setAdapter(paymentAdapter);
    }

    private List<PaymentMethodAdapter.PaymentMethod> getPaymentMethods() {
        List<PaymentMethodAdapter.PaymentMethod> methods = new ArrayList<>();

        // Use Android built-in icons or simple placeholder
        int defaultIcon = android.R.drawable.ic_menu_gallery; // Built-in Android icon

        // E-Wallet Methods
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "GoPay", "E-Wallet", defaultIcon, true, "Instant payment"
        ));
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "OVO", "E-Wallet", defaultIcon, true, "Instant payment"
        ));
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "DANA", "E-Wallet", defaultIcon, true, "Instant payment"
        ));
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "ShopeePay", "E-Wallet", defaultIcon, true, "Instant payment"
        ));

        // Bank Transfer Methods
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "BCA", "Bank Transfer", defaultIcon, true, "1-2 hours processing"
        ));
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "Mandiri", "Bank Transfer", defaultIcon, true, "1-2 hours processing"
        ));
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "BNI", "Bank Transfer", defaultIcon, true, "1-2 hours processing"
        ));
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "BRI", "Bank Transfer", defaultIcon, true, "1-2 hours processing"
        ));

        // Credit Card
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "Credit Card", "Card Payment", defaultIcon, true, "Visa, Mastercard, JCB"
        ));

        // Other Methods
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "Indomaret", "Convenience Store", defaultIcon, true, "Pay at store"
        ));
        methods.add(new PaymentMethodAdapter.PaymentMethod(
                "Alfamart", "Convenience Store", defaultIcon, true, "Pay at store"
        ));

        return methods;
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPayNow.setOnClickListener(v -> {
            if (selectedPaymentMethod != null) {
                processPayment();
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePayButton() {
        if (selectedPaymentMethod != null) {
            btnPayNow.setText("Pay with " + selectedPaymentMethod.name);
            btnPayNow.setEnabled(true);
            btnPayNow.setAlpha(1.0f);
            btnPayNow.setBackgroundColor(0xFF0F4137); // Green color
        } else {
            btnPayNow.setText("Select Payment Method");
            btnPayNow.setEnabled(false);
            btnPayNow.setAlpha(0.5f);
            btnPayNow.setBackgroundColor(0xFF999999); // Gray color
        }
    }

    private void processPayment() {
        // Show payment processing
        Intent intent = new Intent(this, PaymentProcessingActivity.class);
        intent.putExtra("payment_method", selectedPaymentMethod.name);
        intent.putExtra("payment_type", selectedPaymentMethod.type);
        intent.putExtra("total_amount", currentBooking.totalPrice);
        intent.putExtra("car_name", currentBooking.carName);
        intent.putExtra("booking_data", currentBooking.toString());
        startActivity(intent);
        finish();
    }
}