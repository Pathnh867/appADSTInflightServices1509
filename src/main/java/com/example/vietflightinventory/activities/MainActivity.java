// src/main/java/com/example/vietflightinventory/activities/MainActivity.java
package com.example.vietflightinventory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;

import com.example.vietflightinventory.R;
import com.example.vietflightinventory.adapters.FlightAdapter;
import com.example.vietflightinventory.database.DatabaseManager;
import com.example.vietflightinventory.models.Flight;
import com.example.vietflightinventory.models.Handover;
import com.example.vietflightinventory.models.User;
import com.example.vietflightinventory.repositories.BaseRepository;
import com.example.vietflightinventory.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // Dashboard views
    private CardView cardCreateHandover, cardReceiveHandover, cardReports, cardFlightChange;
    private TextView tvWelcome, tvPendingHandovers, tvTodayFlights, tvUserRole;
    private RecyclerView rvRecentFlights;

    // Data
    private DatabaseManager databaseManager;
    private SessionManager sessionManager;
    private User currentUser;
    private FlightAdapter flightAdapter;
    private List<Flight> recentFlights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeManagers();
        setupToolbar();
        setupNavigationDrawer();
        setupDashboardCards();
        loadUserData();
        loadDashboardData();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Dashboard cards
        cardCreateHandover = findViewById(R.id.cardCreateHandover);
        cardReceiveHandover = findViewById(R.id.cardReceiveHandover);
        cardReports = findViewById(R.id.cardReports);
        cardFlightChange = findViewById(R.id.cardFlightChange);

        // Dashboard info
        tvWelcome = findViewById(R.id.tvWelcome);
        tvPendingHandovers = findViewById(R.id.tvPendingHandovers);
        tvTodayFlights = findViewById(R.id.tvTodayFlights);
        tvUserRole = findViewById(R.id.tvUserRole);

        // Recent flights
        rvRecentFlights = findViewById(R.id.rvRecentFlights);
        recentFlights = new ArrayList<>();
        flightAdapter = new FlightAdapter(this, recentFlights);
        rvRecentFlights.setLayoutManager(new LinearLayoutManager(this));
        rvRecentFlights.setAdapter(flightAdapter);
    }

    private void initializeManagers() {
        databaseManager = DatabaseManager.getInstance();
        sessionManager = SessionManager.getInstance(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("VietJet Inflight Services");
        }
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupDashboardCards() {
        cardCreateHandover.setOnClickListener(v -> {
            if (canCreateHandover()) {
                startActivity(new Intent(this, CreateHandoverActivity.class));
            } else {
                Toast.makeText(this, "Bạn không có quyền tạo bàn giao", Toast.LENGTH_SHORT).show();
            }
        });

        cardReceiveHandover.setOnClickListener(v -> {
            if (canReceiveHandover()) {
                startActivity(new Intent(this, ReceiveHandoverActivity.class));
            } else {
                Toast.makeText(this, "Bạn không có quyền nhận bàn giao", Toast.LENGTH_SHORT).show();
            }
        });

        cardReports.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
        });

        cardFlightChange.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng đổi chuyến đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to login
            redirectToLogin();
            return;
        }

        updateUIWithUserData();
        updateNavigationHeader();
        updateCardVisibility();
    }

    private void updateUIWithUserData() {
        if (tvWelcome != null) {
            tvWelcome.setText("Chào mừng, " + currentUser.getFullName());
        }

        if (tvUserRole != null) {
            String roleDisplay = getRoleDisplayName(currentUser.getRole());
            tvUserRole.setText(roleDisplay + " - " + currentUser.getAirport());
        }
    }

    private void updateNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView navUserName = headerView.findViewById(R.id.nav_user_name);
            TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
            TextView navUserRole = headerView.findViewById(R.id.nav_user_role);

            if (navUserName != null) navUserName.setText(currentUser.getFullName());
            if (navUserEmail != null) navUserEmail.setText(currentUser.getEmail());
            if (navUserRole != null) navUserRole.setText(getRoleDisplayName(currentUser.getRole()));
        }
    }

    private void updateCardVisibility() {
        // Show/hide cards based on user role
        if (cardCreateHandover != null) {
            cardCreateHandover.setVisibility(canCreateHandover() ? View.VISIBLE : View.GONE);
        }

        if (cardReceiveHandover != null) {
            cardReceiveHandover.setVisibility(canReceiveHandover() ? View.VISIBLE : View.GONE);
        }
    }

    private void loadDashboardData() {
        loadPendingHandoversCount();
        loadTodayFlightsCount();
        loadRecentFlights();
    }

    private void loadPendingHandoversCount() {
        databaseManager.getHandoverRepository().findPendingApprovals(
                currentUser.get_id(),
                currentUser.getRole(),
                new BaseRepository.ListCallback<Handover>() {
                    @Override
                    public void onSuccess(List<Handover> handovers) {
                        runOnUiThread(() -> {
                            if (tvPendingHandovers != null) {
                                tvPendingHandovers.setText(String.valueOf(handovers.size()));
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            if (tvPendingHandovers != null) {
                                tvPendingHandovers.setText("0");
                            }
                        });
                    }
                }
        );
    }

    private void loadTodayFlightsCount() {
        databaseManager.getFlightRepository().findTodayFlights(new BaseRepository.ListCallback<Flight>() {
            @Override
            public void onSuccess(List<Flight> flights) {
                runOnUiThread(() -> {
                    if (tvTodayFlights != null) {
                        tvTodayFlights.setText(String.valueOf(flights.size()));
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (tvTodayFlights != null) {
                        tvTodayFlights.setText("0");
                    }
                });
            }
        });
    }

    private void loadRecentFlights() {
        databaseManager.getFlightRepository().findRecentFlights(5, new BaseRepository.ListCallback<Flight>() {
            @Override
            public void onSuccess(List<Flight> flights) {
                runOnUiThread(() -> {
                    recentFlights.clear();
                    recentFlights.addAll(flights);
                    flightAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Lỗi khi tải danh sách chuyến bay: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean canCreateHandover() {
        return currentUser != null &&
                ("Administrator".equals(currentUser.getRole()) ||
                        "InflightServicesStaff".equals(currentUser.getRole()));
    }

    private boolean canReceiveHandover() {
        return currentUser != null &&
                ("Administrator".equals(currentUser.getRole()) ||
                        "FlightAttendant".equals(currentUser.getRole()));
    }

    private String getRoleDisplayName(String role) {
        switch (role) {
            case "Administrator":
                return "Quản trị viên";
            case "InflightServicesStaff":
                return "Nhân viên dịch vụ";
            case "FlightAttendant":
                return "Tiếp viên hàng không";
            default:
                return role;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_create) {
            if (canCreateHandover()) {
                startActivity(new Intent(this, CreateHandoverActivity.class));
            } else {
                Toast.makeText(this, "Bạn không có quyền tạo bàn giao", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_receive) {
            if (canReceiveHandover()) {
                startActivity(new Intent(this, ReceiveHandoverActivity.class));
            } else {
                Toast.makeText(this, "Bạn không có quyền nhận bàn giao", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_change) {
            Toast.makeText(this, "Tính năng đổi chuyến đang phát triển", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_report) {
            startActivity(new Intent(this, ReportActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountInfoActivity.class));
        } else if (id == R.id.nav_password) {
            Toast.makeText(this, "Tính năng đổi mật khẩu đang phát triển", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            performLogout();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void performLogout() {
        sessionManager.logout();
        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh dashboard data when returning to main activity
        if (currentUser != null) {
            loadDashboardData();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }
}