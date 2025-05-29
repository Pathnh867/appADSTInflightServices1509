// src/main/java/com/example/vietflightinventory/activities/ReceiveHandoverActivity.java
package com.example.vietflightinventory.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietflightinventory.R;
import com.example.vietflightinventory.adapters.HandoverItemAdapter;
import com.example.vietflightinventory.constants.AppConstants;
import com.example.vietflightinventory.database.DatabaseManager;
import com.example.vietflightinventory.models.Handover;
import com.example.vietflightinventory.models.HandoverItem;
import com.example.vietflightinventory.models.User;
import com.example.vietflightinventory.repositories.BaseRepository;
import com.example.vietflightinventory.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReceiveHandoverActivity extends AppCompatActivity {

    // UI Components
    private EditText edtHandoverCode;
    private Button btnSearchHandover, btnConfirmReceive, btnClose;
    private LinearLayout handoverDetailsSection;
    private TextView tvFlightInfo, tvCreatedBy, tvCreationDate, tvStatus, tvTotalValue;
    private RecyclerView rvHandoverItems;
    private ProgressBar progressBar;

    // Data
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;
    private User currentUser;
    private Handover currentHandover;
    private HandoverItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_handover);

        initializeViews();
        initializeManagers();
        setupRecyclerView();
        setupEventListeners();
        checkUserPermissions();
    }

    private void initializeViews() {
        edtHandoverCode = findViewById(R.id.edtHandoverCode);
        btnSearchHandover = findViewById(R.id.btnSearchHandover);
        btnConfirmReceive = findViewById(R.id.btnConfirmReceive);
        btnClose = findViewById(R.id.btnClose);

        handoverDetailsSection = findViewById(R.id.handoverDetailsSection);
        tvFlightInfo = findViewById(R.id.tvFlightInfo);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvCreationDate = findViewById(R.id.tvCreationDate);
        tvStatus = findViewById(R.id.tvStatus);
        tvTotalValue = findViewById(R.id.tvTotalValue);

        rvHandoverItems = findViewById(R.id.rvHandoverItems);
        progressBar = findViewById(R.id.progressBar);

        // Initially hide details section
        handoverDetailsSection.setVisibility(View.GONE);
        btnConfirmReceive.setVisibility(View.GONE);

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
        rvHandoverItems.setLayoutManager(new LinearLayoutManager(this));
        rvHandoverItems.setHasFixedSize(true);
    }

    private void setupEventListeners() {
        btnClose.setOnClickListener(v -> finish());

        btnSearchHandover.setOnClickListener(v -> searchHandover());

        btnConfirmReceive.setOnClickListener(v -> confirmReceiveHandover());
    }

    private void checkUserPermissions() {
        if (currentUser == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Only FA and admin can receive handovers
        if (!AppConstants.ROLE_FLIGHT_ATTENDANT.equals(currentUser.getRole()) &&
                !AppConstants.ROLE_ADMINISTRATOR.equals(currentUser.getRole())) {
            Toast.makeText(this, "Bạn không có quyền nhận bàn giao", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void searchHandover() {
        String handoverCode = edtHandoverCode.getText().toString().trim();

        if (handoverCode.isEmpty()) {
            edtHandoverCode.setError("Vui lòng nhập mã bàn giao");
            return;
        }

        showLoading(true);

        databaseManager.getHandoverRepository().findByHandoverCode(handoverCode, new BaseRepository.OperationCallback<Handover>() {
            @Override
            public void onSuccess(Handover handover) {
                runOnUiThread(() -> {
                    showLoading(false);
                    currentHandover = handover;
                    displayHandoverDetails(handover);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(ReceiveHandoverActivity.this,
                            "Không tìm thấy bàn giao: " + error, Toast.LENGTH_LONG).show();
                    hideHandoverDetails();
                });
            }
        });
    }

    private void displayHandoverDetails(Handover handover) {
        // Show details section
        handoverDetailsSection.setVisibility(View.VISIBLE);

        // Flight info
        String flightInfo = String.format("Chuyến bay: %s | Tàu bay: %s",
                handover.getFlightNumberDisplay(),
                handover.getAircraftNumberDisplay());
        tvFlightInfo.setText(flightInfo);

        // Created by
        tvCreatedBy.setText("Tạo bởi: " + handover.getCreatedByUserNameDisplay());

        // Creation date
        if (handover.getCreationTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvCreationDate.setText("Ngày tạo: " + sdf.format(handover.getCreationTimestamp()));
        }

        // Status
        String statusDisplay = getStatusDisplayName(handover.getStatus());
        tvStatus.setText("Trạng thái: " + statusDisplay);

        // Total value
        tvTotalValue.setText(String.format("Tổng giá trị: %,.0f VNĐ", handover.getTotalValue()));

        // Setup items adapter
        if (handover.getItems() != null && !handover.getItems().isEmpty()) {
            itemAdapter = new HandoverItemAdapter(this, handover.getItems(), true); // true for receive mode
            rvHandoverItems.setAdapter(itemAdapter);
        }

        // Show/hide confirm button based on status
        updateConfirmButtonVisibility(handover);
    }

    private void updateConfirmButtonVisibility(Handover handover) {
        boolean canReceive = AppConstants.HANDOVER_STATUS_PENDING_FA_APPROVAL.equals(handover.getStatus()) &&
                (AppConstants.ROLE_FLIGHT_ATTENDANT.equals(currentUser.getRole()) ||
                        AppConstants.ROLE_ADMINISTRATOR.equals(currentUser.getRole()));

        btnConfirmReceive.setVisibility(canReceive ? View.VISIBLE : View.GONE);

        if (!canReceive && AppConstants.HANDOVER_STATUS_PENDING_FA_APPROVAL.equals(handover.getStatus())) {
            Toast.makeText(this, "Chỉ tiếp viên hàng không mới có thể nhận bàn giao này", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmReceiveHandover() {
        if (currentHandover == null) {
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận nhận bàn giao")
                .setMessage("Bạn có chắc chắn muốn nhận bàn giao này?\n\n" +
                        "Mã bàn giao: " + currentHandover.getHandoverCode() + "\n" +
                        "Chuyến bay: " + currentHandover.getFlightNumberDisplay())
                .setPositiveButton("Xác nhận", (dialog, which) -> processReceiveHandover())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void processReceiveHandover() {
        showLoading(true);

        // Update handover status and receiver info
        currentHandover.setReceivedByUserId(currentUser.get_id());
        currentHandover.setReceivedByUserNameDisplay(currentUser.getFullName());
        currentHandover.setFaConfirmationTimestamp(new Date());
        currentHandover.setLastUpdatedTimestamp(new Date());
        currentHandover.setStatus(AppConstants.HANDOVER_STATUS_CONFIRMED_BY_FA);

        // Update received quantities from adapter if needed
        if (itemAdapter != null) {
            List<HandoverItem> updatedItems = itemAdapter.getUpdatedItems();
            currentHandover.setItems(updatedItems);
        }

        databaseManager.getHandoverRepository().update(currentHandover, new BaseRepository.BooleanCallback() {
            @Override
            public void onSuccess(boolean success) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (success) {
                        Toast.makeText(ReceiveHandoverActivity.this,
                                "Đã nhận bàn giao thành công!", Toast.LENGTH_LONG).show();

                        // Refresh display
                        displayHandoverDetails(currentHandover);

                        // Optionally finish activity
                        // finish();
                    } else {
                        Toast.makeText(ReceiveHandoverActivity.this,
                                "Không thể cập nhật bàn giao", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(ReceiveHandoverActivity.this,
                            "Lỗi khi nhận bàn giao: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void hideHandoverDetails() {
        handoverDetailsSection.setVisibility(View.GONE);
        btnConfirmReceive.setVisibility(View.GONE);
        currentHandover = null;
    }

    private String getStatusDisplayName(String status) {
        switch (status) {
            case AppConstants.HANDOVER_STATUS_PENDING_FA_APPROVAL:
                return "Chờ tiếp viên xác nhận";
            case AppConstants.HANDOVER_STATUS_CONFIRMED_BY_FA:
                return "Đã xác nhận bởi tiếp viên";
            case AppConstants.HANDOVER_STATUS_PENDING_STAFF_APPROVAL_RETURN:
                return "Chờ nhân viên xác nhận trả lại";
            case AppConstants.HANDOVER_STATUS_COMPLETED:
                return "Hoàn thành";
            case AppConstants.HANDOVER_STATUS_CANCELLED:
                return "Đã hủy";
            default:
                return status;
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        btnSearchHandover.setEnabled(!show);
        btnConfirmReceive.setEnabled(!show);
        edtHandoverCode.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentHandover = null;
    }
}