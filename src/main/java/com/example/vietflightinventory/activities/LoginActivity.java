// src/main/java/com/example/vietflightinventory/activities/LoginActivity.java
package com.example.vietflightinventory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietflightinventory.R;
import com.example.vietflightinventory.database.DatabaseManager;
import com.example.vietflightinventory.models.User;
import com.example.vietflightinventory.repositories.BaseRepository;
import com.example.vietflightinventory.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    private DatabaseManager databaseManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeManagers();
        setupClickListeners();

        // Check if user is already logged in
        checkExistingSession();
    }

    private void initializeViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Hide progress bar initially
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initializeManagers() {
        databaseManager = DatabaseManager.getInstance();
        sessionManager = SessionManager.getInstance(this);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void checkExistingSession() {
        if (sessionManager.isLoggedIn()) {
            // User already logged in, redirect to main activity
            redirectToMainActivity();
        }
    }

    private void attemptLogin() {
        String username = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Validate input
        if (!validateInput(username, password)) {
            return;
        }

        // Show loading
        showLoading(true);

        // Authenticate user
        databaseManager.getUserRepository().authenticate(username, password, new BaseRepository.OperationCallback<User>() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    showLoading(false);
                    handleLoginSuccess(user);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    handleLoginError(error);
                });
            }
        });
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            edtEmail.setError("Vui lòng nhập tên đăng nhập");
            edtEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void handleLoginSuccess(User user) {
        // Save user session
        sessionManager.saveUserSession(user);

        Toast.makeText(this, "Đăng nhập thành công! Chào mừng " + user.getFullName(), Toast.LENGTH_SHORT).show();

        // Redirect based on user role
        redirectBasedOnRole(user);
    }

    private void handleLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();

        // Clear password field for security
        edtPassword.setText("");
        edtPassword.requestFocus();
    }

    private void redirectBasedOnRole(User user) {
        Intent intent;

        switch (user.getRole()) {
            case "Administrator":
                // Admin goes to main activity with full access
                intent = new Intent(this, MainActivity.class);
                break;
            case "InflightServicesStaff":
                // Staff goes to main activity (can create handovers)
                intent = new Intent(this, MainActivity.class);
                break;
            case "FlightAttendant":
                // FA goes to main activity (can receive handovers)
                intent = new Intent(this, MainActivity.class);
                break;
            default:
                // Default to main activity
                intent = new Intent(this, MainActivity.class);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        btnLogin.setEnabled(!show);
        edtEmail.setEnabled(!show);
        edtPassword.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear sensitive data
        if (edtPassword != null) {
            edtPassword.setText("");
        }
    }
}