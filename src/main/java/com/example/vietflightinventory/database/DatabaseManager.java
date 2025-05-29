// src/main/java/com/example/vietflightinventory/database/DatabaseManager.java
package com.example.vietflightinventory.database;

import android.content.Context;
import android.util.Log;
import com.example.vietflightinventory.helpers.MongoDBHelper;
import com.example.vietflightinventory.repositories.*;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";
    private static DatabaseManager instance;

    private UserRepository userRepository;
    private FlightRepository flightRepository;
    private ProductRepository productRepository;
    private HandoverRepository handoverRepository;

    private boolean isInitialized = false;

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private DatabaseManager() {
        initializeRepositories();
    }

    private void initializeRepositories() {
        try {
            userRepository = UserRepository.getInstance();
            flightRepository = FlightRepository.getInstance();
            productRepository = ProductRepository.getInstance();
            handoverRepository = HandoverRepository.getInstance();

            isInitialized = true;
            Log.d(TAG, "Database repositories initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database repositories", e);
            isInitialized = false;
        }
    }

    public boolean initialize(Context context) {
        try {
            // Test database connection
            boolean connectionTest = MongoDBHelper.testConnection();
            if (connectionTest) {
                Log.d(TAG, "Database connection successful");
                return true;
            } else {
                Log.e(TAG, "Database connection failed");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error testing database connection", e);
            return false;
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    // Repository getters
    public UserRepository getUserRepository() {
        return userRepository;
    }

    public FlightRepository getFlightRepository() {
        return flightRepository;
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public HandoverRepository getHandoverRepository() {
        return handoverRepository;
    }

    // Cleanup
    public void cleanup() {
        try {
            MongoDBHelper.close();
            Log.d(TAG, "Database cleanup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error during database cleanup", e);
        }
    }

    // Health check
    public void performHealthCheck(HealthCheckCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                boolean isHealthy = MongoDBHelper.testConnection();
                callback.onResult(isHealthy, isHealthy ? "Database is healthy" : "Database connection failed");
            } catch (Exception e) {
                callback.onResult(false, "Health check failed: " + e.getMessage());
            }
        });
    }

    public interface HealthCheckCallback {
        void onResult(boolean isHealthy, String message);
    }
}