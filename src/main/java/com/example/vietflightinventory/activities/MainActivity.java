package com.example.vietflightinventory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import com.example.vietflightinventory.R;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_create) {
                Toast.makeText(this, "Tạo bàn giao", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(...));
            } else if (id == R.id.nav_receive) {
                Toast.makeText(this, "Nhận bàn giao", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_change) {
                Toast.makeText(this, "Đổi chuyến", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_report) {
                Toast.makeText(this, "Báo cáo", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_account) {
                Toast.makeText(this, "Thông tin tài khoản", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_password) {
                Toast.makeText(this, "Đổi mật khẩu", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }
}