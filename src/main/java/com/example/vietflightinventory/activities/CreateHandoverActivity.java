// src/main/java/com/example/vietflightinventory/activities/CreateHandoverActivity.java
package com.example.vietflightinventory.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietflightinventory.R;
import com.example.vietflightinventory.adapters.ProductAdapter;
import com.example.vietflightinventory.constants.AppConstants;
import com.example.vietflightinventory.database.DatabaseManager;
import com.example.vietflightinventory.models.Flight;
import com.example.vietflightinventory.models.Handover;
import com.example.vietflightinventory.models.HandoverItem;
import com.example.vietflightinventory.models.Product;
import com.example.vietflightinventory.models.User;
import com.example.vietflightinventory.repositories.BaseRepository;
import com.example.vietflightinventory.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.example.vietflightinventory.constants.AppConstants;
import com.example.vietflightinventory.models.ValidationResult;
import java.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateHandoverActivity extends AppCompatActivity {

    // UI Components
    private EditText edtFlightDate, edtFlightNumber, edtAircraft, edtFlightType, edtSearchProduct;
    private Button btnSubmitHandover, btnLoadFlightData, btnClose;
    private TabLayout tabLayout;
    private RecyclerView rvProducts;
    private LinearLayout productSection;
    private ProgressBar progressBar;

    // Data
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;
    private User currentUser;
    private Flight selectedFlight;
    private List<Product> allProducts;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_handover);

        initializeViews();
        initializeManagers();
        setupRecyclerView();
        setupEventListeners();
        checkUserPermissions();
        loadAllProducts();
    }

    private void initializeViews() {
        edtFlightDate = findViewById(R.id.edtFlightDate);
        edtFlightNumber = findViewById(R.id.edtFlightNumber);
        edtAircraft = findViewById(R.id.edtAircraft);
        edtFlightType = findViewById(R.id.edtFlightType);
        edtSearchProduct = findViewById(R.id.edtSearchProduct);

        btnSubmitHandover = findViewById(R.id.btnSubmitHandover);
        btnLoadFlightData = findViewById(R.id.btnLoadFlightData);
        btnClose = findViewById(R.id.btnClose);

        tabLayout = findViewById(R.id.tabLayout);
        rvProducts = findViewById(R.id.rvProducts);
        productSection = findViewById(R.id.productSection);
        progressBar = findViewById(R.id.progressBar);

        // Initially hide product section and submit button
        productSection.setVisibility(View.GONE);
        btnSubmitHandover.setVisibility(View.GONE);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initializeManagers() {
        databaseManager = DatabaseManager.getInstance();
        sessionManager = SessionManager.getInstance(this);
        currentUser = sessionManager.getCurrentUser();
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setHasFixedSize(true);
        adapter = new ProductAdapter(this, new ArrayList<>());
        rvProducts.setAdapter(adapter);
    }

    private void setupEventListeners() {
        btnClose.setOnClickListener(v -> finish());

        btnLoadFlightData.setOnClickListener(v -> loadFlightData());

        btnSubmitHandover.setOnClickListener(v -> submitHandover());

        // Date picker
        edtFlightDate.setOnClickListener(v -> showDatePicker());

        // Tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterProductsByCategory(tab);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Search functionality
        edtSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkUserPermissions() {
        if (currentUser == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Only staff and admin can create handovers
        if (!AppConstants.ROLE_INFLIGHT_SERVICES_STAFF.equals(currentUser.getRole()) &&
                !AppConstants.ROLE_ADMINISTRATOR.equals(currentUser.getRole())) {
            Toast.makeText(this, "Bạn không có quyền tạo bàn giao", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadAllProducts() {
        showLoading(true);

        databaseManager.getProductRepository().findAll(new BaseRepository.ListCallback<Product>() {
            @Override
            public void onSuccess(List<Product> products) {
                runOnUiThread(() -> {
                    showLoading(false);
                    allProducts = products;
                    setupTabs();

                    if (products.isEmpty()) {
                        Toast.makeText(CreateHandoverActivity.this, "Chưa có sản phẩm nào trong hệ thống", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(CreateHandoverActivity.this, "Lỗi khi tải sản phẩm: " + error, Toast.LENGTH_LONG).show();
                    allProducts = new ArrayList<>();
                });
            }
        });
    }

    private void loadFlightData() {
        String flightDate = edtFlightDate.getText().toString().trim();
        String flightNumber = edtFlightNumber.getText().toString().trim();
        String aircraft = edtAircraft.getText().toString().trim();
        String flightType = edtFlightType.getText().toString().trim();

        if (!validateFlightInput(flightDate, flightNumber, aircraft, flightType)) {
            return;
        }

        showLoading(true);

        // Search for existing flight or create new one
        databaseManager.getFlightRepository().findByFlightNumber(flightNumber, new BaseRepository.OperationCallback<Flight>() {
            @Override
            public void onSuccess(Flight flight) {
                runOnUiThread(() -> {
                    showLoading(false);
                    selectedFlight = flight;
                    populateFlightData(flight);
                    showProductSection();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Flight not found, create new one
                    createNewFlight(flightDate, flightNumber, aircraft, flightType);
                });
            }
        });
    }

    private boolean validateFlightInput(String flightDate, String flightNumber, String aircraft, String flightType) {
        if (flightDate.isEmpty()) {
            edtFlightDate.setError("Vui lòng chọn ngày bay");
            return false;
        }

        if (flightNumber.isEmpty()) {
            edtFlightNumber.setError("Vui lòng nhập số hiệu chuyến bay");
            return false;
        }

        if (aircraft.isEmpty()) {
            edtAircraft.setError("Vui lòng nhập số hiệu tàu bay");
            return false;
        }

        if (flightType.isEmpty()) {
            edtFlightType.setError("Vui lòng nhập loại chuyến bay");
            return false;
        }

        return true;
    }

    private void createNewFlight(String flightDate, String flightNumber, String aircraft, String flightType) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(flightDate);

            Flight newFlight = new Flight();
            newFlight.setFlightNumber(flightNumber);
            newFlight.setAircraftNumber(aircraft);
            newFlight.setFlightDate(date);
            newFlight.setFlightType(flightType);
            newFlight.setStatus("SCHEDULED");
            newFlight.setDepartureAirport(currentUser.getAirport());
            newFlight.setArrivalAirport(""); // Will be set later

            databaseManager.getFlightRepository().insert(newFlight, new BaseRepository.OperationCallback<Flight>() {
                @Override
                public void onSuccess(Flight flight) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        selectedFlight = flight;
                        showProductSection();
                        Toast.makeText(CreateHandoverActivity.this, "Đã tạo thông tin chuyến bay mới", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(CreateHandoverActivity.this, "Lỗi khi tạo chuyến bay: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });

        } catch (Exception e) {
            showLoading(false);
            Toast.makeText(this, "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateFlightData(Flight flight) {
        if (flight.getFlightDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            edtFlightDate.setText(sdf.format(flight.getFlightDate()));
        }

        edtFlightNumber.setText(flight.getFlightNumber());
        edtAircraft.setText(flight.getAircraftNumber());
        edtFlightType.setText(flight.getFlightType());
    }

    private void showProductSection() {
        productSection.setVisibility(View.VISIBLE);
        btnSubmitHandover.setVisibility(View.VISIBLE);

        // Auto-select first tab
        if (tabLayout.getTabCount() > 0) {
            TabLayout.Tab firstTab = tabLayout.getTabAt(0);
            if (firstTab != null) {
                firstTab.select();
            }
        }
    }

    private void setupTabs() {
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Suất ăn nóng"));
        tabLayout.addTab(tabLayout.newTab().setText("F&B"));
        tabLayout.addTab(tabLayout.newTab().setText("Hàng lưu niệm"));
        tabLayout.addTab(tabLayout.newTab().setText("Sboss Business"));
    }

    private void filterProductsByCategory(TabLayout.Tab tab) {
        if (allProducts == null || allProducts.isEmpty() || tab.getText() == null) {
            return;
        }

        String category = getCategoryFromTabText(tab.getText().toString());

        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (category.equals(product.getCategory())) {
                filteredProducts.add(product);
            }
        }

        adapter.updateProductList(filteredProducts);
    }

    private String getCategoryFromTabText(String tabText) {
        String lowerText = tabText.toLowerCase(Locale.ROOT);

        if (lowerText.contains("suất ăn nóng")) {
            return AppConstants.CATEGORY_HOT_MEAL;
        } else if (lowerText.contains("f&b")) {
            return AppConstants.CATEGORY_FNB;
        } else if (lowerText.contains("lưu niệm")) {
            return AppConstants.CATEGORY_SOUVENIR;
        } else if (lowerText.contains("sboss business")) {
            return AppConstants.CATEGORY_SBOSS_BUSINESS;
        }

        return "";
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
                    edtFlightDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void submitHandover() {
        if (selectedFlight == null) {
            Toast.makeText(this, "Vui lòng tải thông tin chuyến bay trước", Toast.LENGTH_SHORT).show();
            return;
        }

        if (allProducts == null || allProducts.isEmpty()) {
            Toast.makeText(this, "Chưa có dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected products
        List<Product> selectedProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getUiQuantitySelected() > 0) {
                selectedProducts.add(product);
            }
        }

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create handover
        createHandover(selectedProducts);
    }

    private void createHandover(List<Product> selectedProducts) {
        showLoading(true);

        Handover handover = new Handover();
        handover.setFlightId(selectedFlight.get_id());
        handover.setFlightNumberDisplay(selectedFlight.getFlightNumber());
        handover.setAircraftNumberDisplay(selectedFlight.getAircraftNumber());
        handover.setFlightDateDisplay(selectedFlight.getFlightDate());
        handover.setCreatedByUserId(currentUser.get_id());
        handover.setCreatedByUserNameDisplay(currentUser.getFullName());
        handover.setCreationTimestamp(new Date());
        handover.setLastUpdatedTimestamp(new Date());
        handover.setStatus(AppConstants.HANDOVER_STATUS_PENDING_FA_APPROVAL);
        handover.setHandoverType(AppConstants.HANDOVER_TYPE_OUTBOUND);
        handover.setLocked(false);

        // Generate handover code
        String handoverCode = generateHandoverCode();
        handover.setHandoverCode(handoverCode);

        // Create handover items
        List<HandoverItem> items = new ArrayList<>();
        double totalValue = 0.0;

        for (Product product : selectedProducts) {
            HandoverItem item = new HandoverItem();
            item.setProductId(product.get_id());
            item.setProductName(product.getName());
            item.setProductImageUrl(product.getImageUrl());
            item.setUnitPrice(product.getPrice());
            item.setInitialQuantityFromStaff(product.getUiQuantitySelected());
            item.setCategory(product.getCategory());

            items.add(item);
            totalValue += product.getPrice() * product.getUiQuantitySelected();
        }

        handover.setItems(items);
        handover.setTotalValue(totalValue);

        // Save handover to database
        databaseManager.getHandoverRepository().insert(handover, new BaseRepository.OperationCallback<Handover>() {
            @Override
            public void onSuccess(Handover result) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(CreateHandoverActivity.this,
                            "Đã tạo bàn giao thành công!\nMã bàn giao: " + result.getHandoverCode(),
                            Toast.LENGTH_LONG).show();

                    // Log for debugging
                    Log.d("HANDOVER_CREATED", "Handover ID: " + result.get_id() +
                            ", Code: " + result.getHandoverCode() +
                            ", Items: " + result.getItems().size() +
                            ", Total Value: " + result.getTotalValue());

                    // Clear form and go back
                    clearForm();
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(CreateHandoverActivity.this,
                            "Lỗi khi tạo bàn giao: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String generateHandoverCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String dateStr = sdf.format(new Date());
        String randomSuffix = String.valueOf((int)(Math.random() * 9000) + 1000);
        return "HO" + dateStr + randomSuffix;
    }

    private void clearForm() {
        edtFlightDate.setText("");
        edtFlightNumber.setText("");
        edtAircraft.setText("");
        edtFlightType.setText("");
        edtSearchProduct.setText("");

        productSection.setVisibility(View.GONE);
        btnSubmitHandover.setVisibility(View.GONE);

        selectedFlight = null;

        // Reset product quantities
        if (allProducts != null) {
            for (Product product : allProducts) {
                product.setUiQuantitySelected(0);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        btnLoadFlightData.setEnabled(!show);
        btnSubmitHandover.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear any sensitive data if needed
    }
}