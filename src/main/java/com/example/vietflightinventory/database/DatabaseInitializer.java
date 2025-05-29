// src/main/java/com/example/vietflightinventory/database/DatabaseInitializer.java
package com.example.vietflightinventory.database;

import android.content.Context;
import android.util.Log;
import com.example.vietflightinventory.constants.AppConstants;
import com.example.vietflightinventory.models.Product;
import com.example.vietflightinventory.models.User;
import com.example.vietflightinventory.repositories.BaseRepository;
import com.example.vietflightinventory.R;

public class DatabaseInitializer {

    private static final String TAG = "DatabaseInitializer";

    public static void initializeDefaultData(Context context, InitializationCallback callback) {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        if (!dbManager.isInitialized()) {
            callback.onError("Database not initialized");
            return;
        }

        // Initialize in sequence: Users -> Products -> Sample Data
        initializeDefaultUsers(dbManager, new InitializationCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d(TAG, "Users initialized: " + message);
                initializeDefaultProducts(context, dbManager, new InitializationCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Log.d(TAG, "Products initialized: " + message);
                        callback.onSuccess("Database initialization completed successfully");
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Product initialization failed: " + error);
                        callback.onError("Product initialization failed: " + error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "User initialization failed: " + error);
                callback.onError("User initialization failed: " + error);
            }
        });
    }

    private static void initializeDefaultUsers(DatabaseManager dbManager, InitializationCallback callback) {
        // Create default admin user
        User admin = new User(
                "admin",
                "admin123", // Should be hashed in production
                "Administrator",
                AppConstants.ROLE_ADMINISTRATOR,
                "admin@vietjet.com",
                "0901234567",
                "VIETJET",
                "SGN"
        );

        dbManager.getUserRepository().insert(admin, new BaseRepository.OperationCallback<User>() {
            @Override
            public void onSuccess(User result) {
                Log.d(TAG, "Default admin user created");

                // Create default staff user
                User staff = new User(
                        "staff01",
                        "staff123",
                        "Nguyễn Văn Staff",
                        AppConstants.ROLE_INFLIGHT_SERVICES_STAFF,
                        "staff01@vietjet.com",
                        "0901234568",
                        "VIETJET",
                        "SGN"
                );

                dbManager.getUserRepository().insert(staff, new BaseRepository.OperationCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        Log.d(TAG, "Default staff user created");

                        // Create default FA user
                        User fa = new User(
                                "fa01",
                                "fa123",
                                "Trần Thị Flight Attendant",
                                AppConstants.ROLE_FLIGHT_ATTENDANT,
                                "fa01@vietjet.com",
                                "0901234569",
                                "VIETJET",
                                "SGN"
                        );

                        dbManager.getUserRepository().insert(fa, new BaseRepository.OperationCallback<User>() {
                            @Override
                            public void onSuccess(User result) {
                                Log.d(TAG, "Default FA user created");
                                callback.onSuccess("Default users created successfully");
                            }

                            @Override
                            public void onError(String error) {
                                if (error.contains("đã tồn tại")) {
                                    callback.onSuccess("Users already exist");
                                } else {
                                    callback.onError(error);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if (error.contains("đã tồn tại")) {
                            callback.onSuccess("Users already exist");
                        } else {
                            callback.onError(error);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (error.contains("đã tồn tại")) {
                    callback.onSuccess("Users already exist");
                } else {
                    callback.onError(error);
                }
            }
        });
    }

    private static void initializeDefaultProducts(Context context, DatabaseManager dbManager, InitializationCallback callback) {
        // Sample products for each category
        Product[] defaultProducts = {
                // Hot Meals
                new Product("Cơm Gà Teriyaki", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_HOT_MEAL),
                new Product("Cơm Bò Lúc Lắc", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_HOT_MEAL),
                new Product("Mì Ý Sốt Cà Chua", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_HOT_MEAL),

                // F&B
                new Product("Nước Suối", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_FNB),
                new Product("Coca Cola", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_FNB),
                new Product("Cà Phê Đen", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_FNB),
                new Product("Bánh Mì Sandwich", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_FNB),

                // Souvenirs
                new Product("Móc Khóa VietJet", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_SOUVENIR),
                new Product("Áo Thun VietJet", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_SOUVENIR),
                new Product("Mũ Lưỡi Trai", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_SOUVENIR),

                // Sboss Business
                new Product("Combo Business Meal", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_SBOSS_BUSINESS),
                new Product("Premium Drink Set", R.drawable.ic_launcher_foreground, AppConstants.CATEGORY_SBOSS_BUSINESS)
        };

        // Set prices for products
        defaultProducts[0].setPrice(85000); // Cơm Gà Teriyaki
        defaultProducts[1].setPrice(95000); // Cơm Bò Lúc Lắc
        defaultProducts[2].setPrice(75000); // Mì Ý
        defaultProducts[3].setPrice(15000); // Nước Suối
        defaultProducts[4].setPrice(25000); // Coca Cola
        defaultProducts[5].setPrice(35000); // Cà Phê
        defaultProducts[6].setPrice(45000); // Bánh Mì
        defaultProducts[7].setPrice(50000); // Móc Khóa
        defaultProducts[8].setPrice(250000); // Áo Thun
        defaultProducts[9].setPrice(150000); // Mũ
        defaultProducts[10].setPrice(150000); // Combo Business
        defaultProducts[11].setPrice(80000); // Premium Drink

        insertProductsSequentially(dbManager, defaultProducts, 0, callback);
    }

    private static void insertProductsSequentially(DatabaseManager dbManager, Product[] products, int index, InitializationCallback callback) {
        if (index >= products.length) {
            callback.onSuccess("All products initialized successfully");
            return;
        }

        dbManager.getProductRepository().insert(products[index], new BaseRepository.OperationCallback<Product>() {
            @Override
            public void onSuccess(Product result) {
                Log.d(TAG, "Product created: " + result.getName());
                insertProductsSequentially(dbManager, products, index + 1, callback);
            }

            @Override
            public void onError(String error) {
                if (error.contains("đã tồn tại")) {
                    // Product already exists, continue with next
                    insertProductsSequentially(dbManager, products, index + 1, callback);
                } else {
                    callback.onError("Failed to create product " + products[index].getName() + ": " + error);
                }
            }
        });
    }

    public interface InitializationCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}