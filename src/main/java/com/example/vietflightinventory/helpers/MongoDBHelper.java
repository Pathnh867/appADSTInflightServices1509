// Cập nhật MongoDBHelper.java
package com.example.vietflightinventory.helpers;

import android.util.Log;
import com.example.vietflightinventory.BuildConfig;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MongoDBHelper {

    private static final String TAG = "MongoDBHelper";

    // Collection names
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_FLIGHTS = "flights";
    public static final String COLLECTION_PRODUCTS = "products";
    public static final String COLLECTION_HANDOVERS = "handovers";
    public static final String COLLECTION_HANDOVER_ITEMS = "handover_items";

    private static final String CONNECTION_STRING = BuildConfig.MONGO_CONNECTION_STRING;
    private static final String DATABASE_NAME = BuildConfig.MONGO_DB_NAME;

    private static MongoClient mongoClientInstance;
    private static MongoDatabase databaseInstance;
    private static ExecutorService executorService;

    // Initialize executor service for background operations
    static {
        executorService = Executors.newFixedThreadPool(3);
    }

    public static synchronized MongoClient getMongoClient() {
        if (mongoClientInstance == null) {
            try {
                if (CONNECTION_STRING == null || CONNECTION_STRING.isEmpty()) {
                    Log.e(TAG, "MongoDB connection string is not configured properly in BuildConfig.");
                    return null;
                }

                mongoClientInstance = MongoClients.create(CONNECTION_STRING);
                Log.d(TAG, "MongoClient initialized successfully!");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing MongoClient: ", e);
                return null;
            }
        }
        return mongoClientInstance;
    }

    public static synchronized MongoDatabase getDatabase() {
        if (databaseInstance == null) {
            MongoClient client = getMongoClient();
            if (client != null) {
                try {
                    databaseInstance = client.getDatabase(DATABASE_NAME);
                    Log.d(TAG, "Database instance '" + DATABASE_NAME + "' acquired!");
                } catch (Exception e) {
                    Log.e(TAG, "Error acquiring database: ", e);
                }
            }
        }
        return databaseInstance;
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        MongoDatabase db = getDatabase();
        if (db != null) {
            try {
                return db.getCollection(collectionName);
            } catch (Exception e) {
                Log.e(TAG, "Error acquiring collection '" + collectionName + "': ", e);
            }
        }
        return null;
    }

    // Collection getters
    public static MongoCollection<Document> getUsersCollection() {
        return getCollection(COLLECTION_USERS);
    }

    public static MongoCollection<Document> getFlightsCollection() {
        return getCollection(COLLECTION_FLIGHTS);
    }

    public static MongoCollection<Document> getProductsCollection() {
        return getCollection(COLLECTION_PRODUCTS);
    }

    public static MongoCollection<Document> getHandoversCollection() {
        return getCollection(COLLECTION_HANDOVERS);
    }

    public static MongoCollection<Document> getHandoverItemsCollection() {
        return getCollection(COLLECTION_HANDOVER_ITEMS);
    }

    // Execute database operations in background
    public static void executeInBackground(Runnable task) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.execute(task);
        }
    }

    // Test connection
    public static boolean testConnection() {
        try {
            MongoDatabase db = getDatabase();
            if (db != null) {
                // Try to list collections to test connection
                db.listCollectionNames().first();
                Log.d(TAG, "Database connection test successful!");
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Database connection test failed: ", e);
        }
        return false;
    }

    public static synchronized void close() {
        if (mongoClientInstance != null) {
            try {
                mongoClientInstance.close();
                mongoClientInstance = null;
                databaseInstance = null;
                Log.d(TAG, "MongoClient closed.");
            } catch (Exception e) {
                Log.e(TAG, "Error closing MongoClient: ", e);
            }
        }

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}