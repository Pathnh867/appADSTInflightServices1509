// src/main/java/com/example/vietflightinventory/VietFlightInventoryApplication.java
package com.example.vietflightinventory;

import android.app.Application;
import android.util.Log;
import com.example.vietflightinventory.database.DatabaseManager;
import com.example.vietflightinventory.database.DatabaseInitializer;

public class VietFlightInventoryApplication extends Application {

    private static final String TAG = "VietFlightApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Application starting...");

        // Initialize database
        initializeDatabase();
    }

    private void initializeDatabase() {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        boolean initialized = dbManager.initialize(this);
        if (initialized) {
            Log.d(TAG, "Database initialized successfully");

            // Initialize default data
            DatabaseInitializer.initializeDefaultData(this, new DatabaseInitializer.InitializationCallback() {
                @Override
                public void onSuccess(String message) {
                    Log.d(TAG, "Default data initialization: " + message);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Default data initialization failed: " + error);
                }
            });
        } else {
            Log.e(TAG, "Database initialization failed");
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // Cleanup database connections
        DatabaseManager.getInstance().cleanup();
        Log.d(TAG, "Application terminated");
    }
}