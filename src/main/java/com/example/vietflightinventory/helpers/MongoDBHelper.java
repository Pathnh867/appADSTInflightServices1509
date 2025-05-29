package com.example.vietflightinventory.helpers; // Hoặc package bạn đã đặt lớp này

import com.example.vietflightinventory.BuildConfig; // QUAN TRỌNG: Import lớp BuildConfig của ứng dụng bạn
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document; // Import Document từ org.bson

public class MongoDBHelper {

    private static final String TAG = "MongoDBHelper";

    // Sử dụng các hằng số từ BuildConfig
    private static final String CONNECTION_STRING = BuildConfig.MONGO_CONNECTION_STRING;
    private static final String DATABASE_NAME = BuildConfig.MONGO_DB_NAME;

    private static MongoClient mongoClientInstance;
    private static MongoDatabase databaseInstance;

    public static synchronized MongoClient getMongoClient() {
        if (mongoClientInstance == null) {
            try {
                // Kiểm tra xem chuỗi kết nối có rỗng không trước khi tạo client
                if (CONNECTION_STRING == null || CONNECTION_STRING.isEmpty() ||
                        (BuildConfig.MONGO_USER != null && BuildConfig.MONGO_USER.isEmpty()) || // Kiểm tra user/pass rỗng nếu chuỗi chính cũng rỗng
                        (BuildConfig.MONGO_PASSWORD != null && BuildConfig.MONGO_PASSWORD.isEmpty())) {
                    android.util.Log.e(TAG, "MongoDB connection string or credentials are not configured properly in BuildConfig.");
                    return null;
                }
                // KHÔNG thực hiện việc này trên UI thread trong ứng dụng thực tế!
                // Việc tạo client có thể mất thời gian.
                mongoClientInstance = MongoClients.create(CONNECTION_STRING);
                android.util.Log.d(TAG, "MongoClient initialized successfully!");
            } catch (Exception e) {
                android.util.Log.e(TAG, "Error initializing MongoClient: ", e);
                // Xử lý lỗi phù hợp, ví dụ: throw new RuntimeException("Could not initialize MongoClient", e);
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
                    android.util.Log.d(TAG, "Database instance '" + DATABASE_NAME + "' acquired!");
                } catch (Exception e) {
                    android.util.Log.e(TAG, "Error acquiring database: ", e);
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
                android.util.Log.e(TAG, "Error acquiring collection '" + collectionName + "': ", e);
            }
        }
        return null;
    }

    public static synchronized void close() {
        if (mongoClientInstance != null) {
            try {
                mongoClientInstance.close();
                mongoClientInstance = null;
                databaseInstance = null;
                android.util.Log.d(TAG, "MongoClient closed.");
            } catch (Exception e) {
                android.util.Log.e(TAG, "Error closing MongoClient: ", e);
            }
        }
    }

    public static MongoCollection<Document> getUsersCollection() {
        return getCollection("users");
    }

    public static MongoCollection<Document> getProductsCollection() {
        return getCollection("products");
    }

    // Thêm các phương thức tương tự cho flights, handovers, v.v. nếu cần
}