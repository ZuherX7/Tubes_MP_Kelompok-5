package com.example.tubesmp3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PaymentProcessingActivity extends AppCompatActivity {

    private TextView tvPaymentMethod, tvAmount, tvCarName, tvStatus;
    private ImageView ivPaymentIcon;
    private ProgressBar progressBar;

    private String paymentMethod;
    private String paymentType;
    private long totalAmount;
    private String carName;
    private String bookingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_processing);

        initializeViews();
        getPaymentData();
        updatePaymentInfo();
        simulatePaymentProcess();
    }

    private void initializeViews() {
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvAmount = findViewById(R.id.tvAmount);
        tvCarName = findViewById(R.id.tvCarName);
        tvStatus = findViewById(R.id.tvStatus);
        ivPaymentIcon = findViewById(R.id.ivPaymentIcon);
        progressBar = findViewById(R.id.progressBar);
    }

    private void getPaymentData() {
        Intent intent = getIntent();
        if (intent != null) {
            paymentMethod = intent.getStringExtra("payment_method");
            paymentType = intent.getStringExtra("payment_type");
            totalAmount = intent.getLongExtra("total_amount", 0);
            carName = intent.getStringExtra("car_name");
            bookingData = intent.getStringExtra("booking_data");
        }
    }

    private void updatePaymentInfo() {
        tvPaymentMethod.setText(paymentMethod);
        tvAmount.setText("Rp " + String.format("%,d", totalAmount));
        tvCarName.setText(carName);
        tvStatus.setText("Processing payment...");

        // Set payment icon based on method
        int iconResource = getPaymentIcon(paymentMethod);
        ivPaymentIcon.setImageResource(iconResource);
    }

    private int getPaymentIcon(String method) {
        switch (method.toLowerCase()) {
            case "gopay": return R.drawable.ic_money;
            case "ovo": return R.drawable.ic_money;
            case "dana": return R.drawable.ic_money;
            case "shopeepay": return R.drawable.ic_money;
            case "bca": return R.drawable.ic_money;
            case "mandiri": return R.drawable.ic_money;
            case "bni": return R.drawable.ic_money;
            case "bri": return R.drawable.ic_money;
            case "credit card": return R.drawable.ic_money;
            case "indomaret": return R.drawable.ic_money;
            case "alfamart": return R.drawable.ic_money;
            default: return R.drawable.ic_money;
        }
    }

    private void simulatePaymentProcess() {
        // Simulate different processing times based on payment type
        int processingTime = getProcessingTime(paymentType);

        new Handler().postDelayed(() -> {
            // Simulate payment success/failure (90% success rate)
            Random random = new Random();
            boolean isSuccess = random.nextInt(10) < 9; // 90% success rate

            if (isSuccess) {
                handlePaymentSuccess();
            } else {
                handlePaymentFailure();
            }
        }, processingTime);
    }

    private int getProcessingTime(String paymentType) {
        switch (paymentType.toLowerCase()) {
            case "e-wallet": return 3000; // 3 seconds
            case "bank transfer": return 5000; // 5 seconds
            case "card payment": return 4000; // 4 seconds
            case "convenience store": return 2000; // 2 seconds
            default: return 3000;
        }
    }

    private void handlePaymentSuccess() {
        // Save booking to history
        if (bookingData != null) {
            BookingHistoryManager.Booking booking = BookingHistoryManager.Booking.fromString(bookingData);
            if (booking != null) {
                booking.status = "active"; // Change from pending_payment to active
                BookingHistoryManager bookingManager = new BookingHistoryManager(this);
                bookingManager.saveBooking(booking);
            }
        }

        // Navigate to payment success screen
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra("payment_method", paymentMethod);
        intent.putExtra("total_amount", totalAmount);
        intent.putExtra("car_name", carName);
        intent.putExtra("transaction_id", generateTransactionId());
        startActivity(intent);
        finish();
    }

    private void handlePaymentFailure() {
        // Navigate to payment failure screen
        Intent intent = new Intent(this, PaymentFailureActivity.class);
        intent.putExtra("payment_method", paymentMethod);
        intent.putExtra("total_amount", totalAmount);
        intent.putExtra("car_name", carName);
        intent.putExtra("error_message", "Payment processing failed. Please try again.");
        startActivity(intent);
        finish();
    }

    private String generateTransactionId() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        Random random = new Random();
        int randomNum = random.nextInt(9999);
        return "TXN" + timestamp + String.format("%04d", randomNum);
    }
}