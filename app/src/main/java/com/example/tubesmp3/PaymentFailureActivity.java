package com.example.tubesmp3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentFailureActivity extends AppCompatActivity {

    private TextView tvErrorMessage, tvPaymentMethod, tvAmount, tvCarName;
    private Button btnTryAgain, btnBackToDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failure);

        initializeViews();
        displayFailureInfo();
        setupClickListeners();
    }

    private void initializeViews() {
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvAmount = findViewById(R.id.tvAmount);
        tvCarName = findViewById(R.id.tvCarName);
        btnTryAgain = findViewById(R.id.btnTryAgain);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
    }

    private void displayFailureInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            String errorMessage = intent.getStringExtra("error_message");
            String paymentMethod = intent.getStringExtra("payment_method");
            long totalAmount = intent.getLongExtra("total_amount", 0);
            String carName = intent.getStringExtra("car_name");

            tvErrorMessage.setText(errorMessage);
            tvPaymentMethod.setText(paymentMethod);
            tvAmount.setText("Rp " + String.format("%,d", totalAmount));
            tvCarName.setText(carName);
        }
    }

    private void setupClickListeners() {
        btnTryAgain.setOnClickListener(v -> {
            // Go back to payment selection
            finish();
        });

        btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}