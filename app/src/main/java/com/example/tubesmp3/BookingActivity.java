package com.example.tubesmp3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class BookingActivity extends AppCompatActivity {


    private TextView tvCarName, tvCarRating, tvCarPrice, tvCurrentMonth;
    private ImageView ivCarImage, btnBack, btnPrevMonth, btnNextMonth;
    private Button btnFromDate, btnUntilDate, btnSelectTime, btnConfirm;
    private GridLayout calendarGrid;

    private Calendar fromDate, untilDate, currentCalendar;
    private int selectedHour = 17, selectedMinute = 0;
    private CarAdapter.CarModel selectedCar;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initializeViews();
        getSelectedCarData();
        setupCalendar();
        setupClickListeners();
        updateCalendarDisplay();
    }

    private void initializeViews() {
        // Car info views
        tvCarName = findViewById(R.id.tvCarName);
        tvCarRating = findViewById(R.id.tvCarRating);
        tvCarPrice = findViewById(R.id.tvCarPrice);
        ivCarImage = findViewById(R.id.ivCarImage);

        // Navigation views
        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        // Date and time views
        btnFromDate = findViewById(R.id.btnFromDate);
        btnUntilDate = findViewById(R.id.btnUntilDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        calendarGrid = findViewById(R.id.calendarGrid);

        // Action button
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void getSelectedCarData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selected_car")) {
            selectedCar = (CarAdapter.CarModel) intent.getSerializableExtra("selected_car");
            if (selectedCar != null) {
                updateCarInfo();
            }
        }
    }

    private void updateCarInfo() {
        tvCarName.setText(selectedCar.name);
        tvCarRating.setText(selectedCar.rating);
        tvCarPrice.setText("Rp " + selectedCar.price + "/hari");
        ivCarImage.setImageResource(selectedCar.imageResource);
    }

    private void setupCalendar() {
        currentCalendar = Calendar.getInstance();
        fromDate = Calendar.getInstance();
        untilDate = Calendar.getInstance();
        untilDate.add(Calendar.DAY_OF_MONTH, 1); // Default: tomorrow

        // Update date buttons with current dates
        btnFromDate.setText(dateFormat.format(fromDate.getTime()));
        btnUntilDate.setText(dateFormat.format(untilDate.getTime()));
        btnSelectTime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFromDate.setOnClickListener(v -> showDatePicker(true));
        btnUntilDate.setOnClickListener(v -> showDatePicker(false));
        btnSelectTime.setOnClickListener(v -> showTimePicker());

        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarDisplay();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarDisplay();
        });

        btnConfirm.setOnClickListener(v -> confirmBooking());
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = isFromDate ? fromDate : untilDate;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);

                    if (isFromDate) {
                        btnFromDate.setText(dateFormat.format(calendar.getTime()));
                        // Ensure until date is not before from date
                        if (untilDate.before(fromDate)) {
                            untilDate.setTime(fromDate.getTime());
                            untilDate.add(Calendar.DAY_OF_MONTH, 1);
                            btnUntilDate.setText(dateFormat.format(untilDate.getTime()));
                        }
                    } else {
                        // Ensure until date is not before from date
                        if (calendar.before(fromDate)) {
                            calendar.setTime(fromDate.getTime());
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        btnUntilDate.setText(dateFormat.format(calendar.getTime()));
                    }

                    updateCalendarDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    btnSelectTime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
                },
                selectedHour,
                selectedMinute,
                true
        );
        timePickerDialog.show();
    }

    private void updateCalendarDisplay() {
        tvCurrentMonth.setText(monthFormat.format(currentCalendar.getTime()));
        generateCalendarDays();
    }

    private void generateCalendarDays() {
        // Clear existing calendar days (keep headers)
        int childCount = calendarGrid.getChildCount();
        if (childCount > 7) {
            calendarGrid.removeViews(7, childCount - 7);
        }

        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty cells for days before the first day of the month
        for (int i = 0; i < firstDayOfWeek; i++) {
            addEmptyDay();
        }

        // Add days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            addCalendarDay(day);
        }

        // Add empty cells to fill the grid
        int totalCells = firstDayOfWeek + daysInMonth;
        int remainingCells = 42 - totalCells; // 6 rows * 7 days
        for (int i = 0; i < remainingCells && totalCells < 35; i++) {
            addEmptyDay();
            totalCells++;
        }
    }

    private void addEmptyDay() {
        TextView dayView = new TextView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 120;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        dayView.setLayoutParams(params);
        calendarGrid.addView(dayView);
    }

    private void addCalendarDay(int day) {
        TextView dayView = new TextView(this);
        dayView.setText(String.valueOf(day));
        dayView.setTextSize(14);
        dayView.setTextColor(getResources().getColor(android.R.color.black));
        dayView.setGravity(android.view.Gravity.CENTER);
        dayView.setClickable(true);
        dayView.setFocusable(true);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 120;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(4, 4, 4, 4);
        dayView.setLayoutParams(params);

        // Set background based on date selection
        Calendar dayCalendar = (Calendar) currentCalendar.clone();
        dayCalendar.set(Calendar.DAY_OF_MONTH, day);

        if (isSameDay(dayCalendar, fromDate) || isSameDay(dayCalendar, untilDate)) {
            dayView.setBackgroundResource(R.drawable.calendar_day_selected);
            dayView.setTextColor(getResources().getColor(android.R.color.white));
        } else if (isDateInRange(dayCalendar)) {
            dayView.setBackgroundResource(R.drawable.calendar_day_range);
        } else {
            dayView.setBackgroundResource(R.drawable.calendar_day_background);
        }

        // Set click listener for date selection
        dayView.setOnClickListener(v -> {
            Calendar selectedDate = (Calendar) currentCalendar.clone();
            selectedDate.set(Calendar.DAY_OF_MONTH, day);

            // Simple logic: first click sets from date, second sets until date
            if (fromDate.after(selectedDate) || isSameDay(fromDate, selectedDate)) {
                fromDate.setTime(selectedDate.getTime());
                untilDate.setTime(selectedDate.getTime());
                untilDate.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                untilDate.setTime(selectedDate.getTime());
            }

            btnFromDate.setText(dateFormat.format(fromDate.getTime()));
            btnUntilDate.setText(dateFormat.format(untilDate.getTime()));
            updateCalendarDisplay();
        });

        calendarGrid.addView(dayView);
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private boolean isDateInRange(Calendar date) {
        return date.after(fromDate) && date.before(untilDate);
    }

    // Replace your confirmBooking() method with this:
    private void confirmBooking() {
        if (selectedCar == null) {
            Toast.makeText(this, "No car selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total days and price
        long diffInMillis = untilDate.getTimeInMillis() - fromDate.getTimeInMillis();
        int days = (int) (diffInMillis / (1000 * 60 * 60 * 24));

        if (days <= 0) {
            Toast.makeText(this, "Please select valid dates", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Remove dots and parse price
            String priceStr = selectedCar.price.replace(".", "");
            long pricePerDay = Long.parseLong(priceStr);
            long totalPrice = pricePerDay * days;

            // Create booking object
            BookingHistoryManager.Booking booking = new BookingHistoryManager.Booking(
                    selectedCar.name,
                    selectedCar.rating,
                    selectedCar.price,
                    selectedCar.imageResource,
                    dateFormat.format(fromDate.getTime()),
                    dateFormat.format(untilDate.getTime()),
                    String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute),
                    totalPrice,
                    days,
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()),
                    "active"
            );

            // Save booking to history
            BookingHistoryManager bookingManager = new BookingHistoryManager(this);
            bookingManager.saveBooking(booking);

            // Create booking summary
            String bookingSummary = String.format(
                    "Booking Confirmed!\n\n" +
                            "Car: %s\n" +
                            "From: %s\n" +
                            "Until: %s\n" +
                            "Duration: %d day(s)\n" +
                            "Time: %02d:%02d\n" +
                            "Total Price: Rp %,d\n\n" +
                            "Your booking has been saved to history!",
                    selectedCar.name,
                    dateFormat.format(fromDate.getTime()),
                    dateFormat.format(untilDate.getTime()),
                    days,
                    selectedHour,
                    selectedMinute,
                    totalPrice
            );

            // Show confirmation dialog
            showBookingConfirmation(bookingSummary);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error calculating price", Toast.LENGTH_SHORT).show();
        }
    }

// Update your showBookingConfirmation method in BookingActivity.java

    private void showBookingConfirmation(String bookingSummary) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Booking Confirmation")
                .setMessage(bookingSummary)
                .setPositiveButton("Proceed to Payment", (dialog, which) -> {
                    // Navigate to payment screen instead of dashboard
                    Intent intent = new Intent(this, PaymentActivity.class);

                    // Pass booking data to payment activity
                    intent.putExtra("booking_data", true);
                    intent.putExtra("car_name", selectedCar.name);
                    intent.putExtra("car_rating", selectedCar.rating);
                    intent.putExtra("car_price", selectedCar.price);
                    intent.putExtra("car_image", selectedCar.imageResource);
                    intent.putExtra("from_date", dateFormat.format(fromDate.getTime()));
                    intent.putExtra("until_date", dateFormat.format(untilDate.getTime()));
                    intent.putExtra("time", String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));

                    // Calculate total price
                    long diffInMillis = untilDate.getTimeInMillis() - fromDate.getTimeInMillis();
                    int days = (int) (diffInMillis / (1000 * 60 * 60 * 24));
                    String priceStr = selectedCar.price.replace(".", "");
                    long pricePerDay = Long.parseLong(priceStr);
                    long totalPrice = pricePerDay * days;

                    intent.putExtra("total_price", totalPrice);
                    intent.putExtra("duration", days);
                    intent.putExtra("booking_date", new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));

                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Edit Booking", (dialog, which) -> {
                    // Stay on booking page to edit
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }


}