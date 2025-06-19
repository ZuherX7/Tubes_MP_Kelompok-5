package com.example.tubesmp3;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingHistoryManager {
    private static final String PREF_NAME = "BookingHistory";
    private static final String KEY_BOOKING_COUNT = "booking_count";
    private static final String KEY_UNAVAILABLE_CARS = "unavailable_cars";

    private Context context;
    private SharedPreferences sharedPreferences;

    public BookingHistoryManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Booking model class
    public static class Booking {
        public String carName;
        public String carRating;
        public String carPrice;
        public int carImageResource;
        public String fromDate;
        public String untilDate;
        public String time;
        public long totalPrice;
        public int duration;
        public String bookingDate;
        public String status; // "active", "completed", "cancelled"

        public Booking(String carName, String carRating, String carPrice, int carImageResource,
                       String fromDate, String untilDate, String time, long totalPrice,
                       int duration, String bookingDate, String status) {
            this.carName = carName;
            this.carRating = carRating;
            this.carPrice = carPrice;
            this.carImageResource = carImageResource;
            this.fromDate = fromDate;
            this.untilDate = untilDate;
            this.time = time;
            this.totalPrice = totalPrice;
            this.duration = duration;
            this.bookingDate = bookingDate;
            this.status = status;
        }

        // Convert booking to string for storage
        public String toString() {
            return carName + "|" + carRating + "|" + carPrice + "|" + carImageResource + "|" +
                    fromDate + "|" + untilDate + "|" + time + "|" + totalPrice + "|" +
                    duration + "|" + bookingDate + "|" + status;
        }

        // Create booking from string
        public static Booking fromString(String bookingString) {
            String[] parts = bookingString.split("\\|");
            if (parts.length == 11) {
                return new Booking(
                        parts[0], // carName
                        parts[1], // carRating
                        parts[2], // carPrice
                        Integer.parseInt(parts[3]), // carImageResource
                        parts[4], // fromDate
                        parts[5], // untilDate
                        parts[6], // time
                        Long.parseLong(parts[7]), // totalPrice
                        Integer.parseInt(parts[8]), // duration
                        parts[9], // bookingDate
                        parts[10] // status
                );
            }
            return null;
        }
    }

    // Save a new booking
    public void saveBooking(Booking booking) {
        int bookingCount = sharedPreferences.getInt(KEY_BOOKING_COUNT, 0);

        // Save booking data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("booking_" + bookingCount, booking.toString());
        editor.putInt(KEY_BOOKING_COUNT, bookingCount + 1);
        editor.apply();

        // Mark car as unavailable
        markCarAsUnavailable(booking.carName);
    }

    // Get all booking history
    public List<Booking> getBookingHistory() {
        List<Booking> bookings = new ArrayList<>();
        int bookingCount = sharedPreferences.getInt(KEY_BOOKING_COUNT, 0);

        for (int i = 0; i < bookingCount; i++) {
            String bookingString = sharedPreferences.getString("booking_" + i, "");
            if (!bookingString.isEmpty()) {
                Booking booking = Booking.fromString(bookingString);
                if (booking != null) {
                    bookings.add(booking);
                }
            }
        }

        return bookings;
    }

    // Mark car as unavailable
    private void markCarAsUnavailable(String carName) {
        Set<String> unavailableCars = sharedPreferences.getStringSet(KEY_UNAVAILABLE_CARS, new HashSet<String>());
        Set<String> newUnavailableCars = new HashSet<>(unavailableCars);
        newUnavailableCars.add(carName);

        sharedPreferences.edit().putStringSet(KEY_UNAVAILABLE_CARS, newUnavailableCars).apply();
    }

    // Get list of unavailable car names
    public List<String> getUnavailableCarNames() {
        Set<String> unavailableSet = sharedPreferences.getStringSet(KEY_UNAVAILABLE_CARS, new HashSet<String>());
        return new ArrayList<>(unavailableSet);
    }

    // Check if car is available
    public boolean isCarAvailable(String carName) {
        Set<String> unavailableCars = sharedPreferences.getStringSet(KEY_UNAVAILABLE_CARS, new HashSet<String>());
        return !unavailableCars.contains(carName);
    }

    // Complete a booking (mark as completed)
    public void completeBooking(String carName) {
        int bookingCount = sharedPreferences.getInt(KEY_BOOKING_COUNT, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (int i = 0; i < bookingCount; i++) {
            String bookingString = sharedPreferences.getString("booking_" + i, "");
            if (!bookingString.isEmpty()) {
                Booking booking = Booking.fromString(bookingString);
                if (booking != null && booking.carName.equals(carName) && booking.status.equals("active")) {
                    booking.status = "completed";
                    editor.putString("booking_" + i, booking.toString());
                    break;
                }
            }
        }

        editor.apply();

        // Make car available again
        makeCarAvailable(carName);
    }

    // Make car available again
    private void makeCarAvailable(String carName) {
        Set<String> unavailableCars = sharedPreferences.getStringSet(KEY_UNAVAILABLE_CARS, new HashSet<String>());
        Set<String> newUnavailableCars = new HashSet<>(unavailableCars);
        newUnavailableCars.remove(carName);

        sharedPreferences.edit().putStringSet(KEY_UNAVAILABLE_CARS, newUnavailableCars).apply();
    }

    // Clear all booking history (for testing)
    public void clearBookingHistory() {
        sharedPreferences.edit().clear().apply();
    }

    // Get active bookings only
    public List<Booking> getActiveBookings() {
        List<Booking> allBookings = getBookingHistory();
        List<Booking> activeBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if ("active".equals(booking.status)) {
                activeBookings.add(booking);
            }
        }

        return activeBookings;
    }

    // Get completed bookings only
    public List<Booking> getCompletedBookings() {
        List<Booking> allBookings = getBookingHistory();
        List<Booking> completedBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if ("completed".equals(booking.status)) {
                completedBookings.add(booking);
            }
        }

        return completedBookings;
    }
}