package com.example.tubesmp3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView tvTransactionId, tvPaymentMethod, tvAmount, tvCarName, tvDateTime;
    private Button btnBackToDashboard, btnViewBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        initializeViews();
        displayPaymentInfo();
        setupClickListeners();
    }

    private void initializeViews() {
        tvTransactionId = findViewById(R.id.tvTransactionId);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvAmount = findViewById(R.id.tvAmount);
        tvCarName = findViewById(R.id.tvCarName);
        tvDateTime = findViewById(R.id.tvDateTime);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
        btnViewBooking = findViewById(R.id.btnViewBooking);
    }

    private void displayPaymentInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            String transactionId = intent.getStringExtra("transaction_id");
            String paymentMethod = intent.getStringExtra("payment_method");
            long totalAmount = intent.getLongExtra("total_amount", 0);
            String carName = intent.getStringExtra("car_name");

            tvTransactionId.setText(transactionId);
            tvPaymentMethod.setText(paymentMethod);
            tvAmount.setText("Rp " + String.format("%,d", totalAmount));
            tvCarName.setText(carName);

            // Set current date and time
            String currentDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
            tvDateTime.setText(currentDateTime);
        }
    }

    private void setupClickListeners() {
        btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnViewBooking.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserDashboardActivity.class);
            intent.putExtra("show_profile", true); // Flag to show profile with booking history
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}