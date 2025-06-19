package com.example.tubesmp3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity {
    private BookingHistoryManager bookingManager;
    private RecyclerView recyclerBookingHistory;
    private BookingHistoryAdapter bookingHistoryAdapter;

    RecyclerView recyclerCar, recyclerAvailableCars, recyclerUnavailableCars;
    TextView tvHeaderName, tvProfileName, tvProfileEmail;
    BottomNavigationView bottomNavigation;
    Button btnLogout, btnContinue;
    CarAdapter dashboardAdapter;
    CarAdapter.CarModel selectedCar;


    // Update your onCreate method to include:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Initialize booking manager
        bookingManager = new BookingHistoryManager(this);

        // Initialize views
        tvHeaderName = findViewById(R.id.tvHeaderName);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        recyclerCar = findViewById(R.id.recyclerCar);
        recyclerAvailableCars = findViewById(R.id.recyclerAvailableCars);
        recyclerUnavailableCars = findViewById(R.id.recyclerUnavailableCars);
        recyclerBookingHistory = findViewById(R.id.recyclerBookingHistory); // Add this line
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnLogout = findViewById(R.id.btnLogout);
        btnContinue = findViewById(R.id.btnContinue);

        // Load user data from SharedPreferences
        loadUserData();

        // Setup RecyclerViews
        setupDashboardRecyclerView();
        setupCarsPageRecyclerViews();
        setupBookingHistoryRecyclerView(); // Add this line

        // Setup Bottom Navigation
        setupBottomNavigation();

        // Setup Logout Button
        setupLogout();

        // Setup Continue Button
        setupContinueButton();

        // Add this in your onCreate method in UserDashboardActivity.java
        Button btnClearHistory = findViewById(R.id.btnClearHistory);
        btnClearHistory.setOnClickListener(v -> {
            bookingManager.clearBookingHistory();
            setupDashboardRecyclerView();
            setupCarsPageRecyclerViews();
            if (bookingHistoryAdapter != null) {
                bookingHistoryAdapter.updateBookings(new ArrayList<>());
            }
            Toast.makeText(this, "Booking history cleared!", Toast.LENGTH_SHORT).show();
        });

        // Check if we should show profile section
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("show_profile", false)) {
            // Show profile section and select profile tab
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        } else {
            // Default to dashboard
            bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private void loadUserData() {
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPref.getString("username", "User");
        String email = sharedPref.getString("email", "user@example.com");

        // Update header with user name
        tvHeaderName.setText("Hi " + username + "!");

        // Update profile section
        tvProfileName.setText(username);
        tvProfileEmail.setText(email);
    }

    private void setupDashboardRecyclerView() {
        // Dashboard only shows available cars
        recyclerCar.setLayoutManager(new LinearLayoutManager(this));
        List<CarAdapter.CarModel> availableCarList = getAvailableCarList();
        dashboardAdapter = new CarAdapter(this, availableCarList, true);

        // Set click listener for car selection
        dashboardAdapter.setOnCarClickListener((car, position) -> {
            selectedCar = car;
            showContinueButton();
        });

        recyclerCar.setAdapter(dashboardAdapter);
    }

    private void setupCarsPageRecyclerViews() {
        // Setup Available Cars RecyclerView for Cars page
        recyclerAvailableCars.setLayoutManager(new LinearLayoutManager(this));
        List<CarAdapter.CarModel> availableCarList = getAvailableCarList();
        CarAdapter availableAdapter = new CarAdapter(this, availableCarList, true);
        recyclerAvailableCars.setAdapter(availableAdapter);

        // Setup Unavailable Cars RecyclerView for Cars page
        recyclerUnavailableCars.setLayoutManager(new LinearLayoutManager(this));
        List<CarAdapter.CarModel> unavailableCarList = getUnavailableCarList();
        CarAdapter unavailableAdapter = new CarAdapter(this, unavailableCarList, false);
        recyclerUnavailableCars.setAdapter(unavailableAdapter);
    }

    // Update the getAvailableCarList method:
    private List<CarAdapter.CarModel> getAvailableCarList() {
        List<CarAdapter.CarModel> allCars = getAllCars();
        List<CarAdapter.CarModel> availableCars = new ArrayList<>();
        List<String> unavailableCarNames = bookingManager.getUnavailableCarNames();

        for (CarAdapter.CarModel car : allCars) {
            if (!unavailableCarNames.contains(car.name)) {
                availableCars.add(car);
            }
        }

        return availableCars;
    }

    // Update the getUnavailableCarList method:
    private List<CarAdapter.CarModel> getUnavailableCarList() {
        List<CarAdapter.CarModel> allCars = getAllCars();
        List<CarAdapter.CarModel> unavailableCars = new ArrayList<>();
        List<String> unavailableCarNames = bookingManager.getUnavailableCarNames();

        for (CarAdapter.CarModel car : allCars) {
            if (unavailableCarNames.contains(car.name)) {
                car.isAvailable = false;
                unavailableCars.add(car);
            }
        }

        return unavailableCars;
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                // Show dashboard content (only available cars)
                findViewById(R.id.dashboardContent).setVisibility(android.view.View.VISIBLE);
                findViewById(R.id.carsContent).setVisibility(android.view.View.GONE);
                findViewById(R.id.profileContent).setVisibility(android.view.View.GONE);

                // Show continue button if car is selected
                if (selectedCar != null) {
                    showContinueButton();
                } else {
                    hideContinueButton();
                }
                return true;
            } else if (itemId == R.id.nav_cars) {
                // Show cars list page (available + unavailable cars with sections)
                findViewById(R.id.dashboardContent).setVisibility(android.view.View.GONE);
                findViewById(R.id.carsContent).setVisibility(android.view.View.VISIBLE);
                findViewById(R.id.profileContent).setVisibility(android.view.View.GONE);
                hideContinueButton(); // Hide continue button on cars page
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Show profile content
                findViewById(R.id.dashboardContent).setVisibility(android.view.View.GONE);
                findViewById(R.id.carsContent).setVisibility(android.view.View.GONE);
                findViewById(R.id.profileContent).setVisibility(android.view.View.VISIBLE);
                hideContinueButton(); // Hide continue button on profile page
                return true;
            }
            return false;
        });

        // Set default selected item
        bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            // Clear SharedPreferences
            SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            // Redirect to Login
            Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupContinueButton() {
        btnContinue.setOnClickListener(v -> {
            if (selectedCar != null) {
                // Navigate to BookingActivity with selected car data
                Intent intent = new Intent(UserDashboardActivity.this, BookingActivity.class);
                intent.putExtra("selected_car", selectedCar);
                startActivity(intent);
            } else {
                android.widget.Toast.makeText(this,
                        "Please select a car first",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showContinueButton() {
        btnContinue.setVisibility(android.view.View.VISIBLE);
        btnContinue.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(300)
                .start();
    }

    private void hideContinueButton() {
        btnContinue.animate()
                .translationY(btnContinue.getHeight())
                .alpha(0.0f)
                .setDuration(300)
                .withEndAction(() -> btnContinue.setVisibility(android.view.View.GONE))
                .start();
    }

    // Add method to get all cars:
    private List<CarAdapter.CarModel> getAllCars() {
        List<CarAdapter.CarModel> carList = new ArrayList<>();
        carList.add(new CarAdapter.CarModel("AVANZA", "4.5", "300.000", R.drawable.avanza, true));
        carList.add(new CarAdapter.CarModel("Ertiga", "4.2", "500.000", R.drawable.ertiga, true));
        carList.add(new CarAdapter.CarModel("Fortuner", "4.8", "800.000", R.drawable.fortuner, true));
        carList.add(new CarAdapter.CarModel("Innova", "4.6", "850.000", R.drawable.innova, true));
        carList.add(new CarAdapter.CarModel("Camry", "4.7", "950.000", R.drawable.mclaren, true));
        carList.add(new CarAdapter.CarModel("Tesla S", "4.9", "2.000.000", R.drawable.tesla, true));
        return carList;
    }

    // Add booking history setup:
    private void setupBookingHistoryRecyclerView() {
        if (recyclerBookingHistory != null) {
            recyclerBookingHistory.setLayoutManager(new LinearLayoutManager(this));
            List<BookingHistoryManager.Booking> bookings = bookingManager.getBookingHistory();
            bookingHistoryAdapter = new BookingHistoryAdapter(this, bookings);
            recyclerBookingHistory.setAdapter(bookingHistoryAdapter);
        }
    }

    // Add method to refresh data:
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh car lists and booking history
        setupDashboardRecyclerView();
        setupCarsPageRecyclerViews();
        if (bookingHistoryAdapter != null) {
            bookingHistoryAdapter.updateBookings(bookingManager.getBookingHistory());
        }
    }

    // Add method to complete booking (you can call this from booking history)
    public void completeBooking(String carName) {
        bookingManager.completeBooking(carName);
        // Refresh the UI
        setupDashboardRecyclerView();
        setupCarsPageRecyclerViews();
        if (bookingHistoryAdapter != null) {
            bookingHistoryAdapter.updateBookings(bookingManager.getBookingHistory());
        }
        Toast.makeText(this, "Booking completed! Car is now available.", Toast.LENGTH_SHORT).show();
    }
}