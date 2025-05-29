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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietflightinventory.R;
import com.example.vietflightinventory.adapters.ProductAdapter; // Đảm bảo import đúng
import com.example.vietflightinventory.models.Product;    // Đảm bảo import đúng
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateHandoverActivity extends AppCompatActivity {

    EditText edtFlightDate, edtFlightNumber, edtAircraft, edtFlightType, edtSearchProduct;
    Button btnSubmitHandover, btnLoadFlightData, btnClose;
    TabLayout tabLayout;
    RecyclerView rvProducts;
    LinearLayout productSection;

    private List<Product> allProducts; // Danh sách TẤT CẢ sản phẩm từ mock data
    // currentlyDisplayedProducts không còn cần thiết ở cấp Activity nữa, adapter sẽ quản lý
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_handover);

        // Ánh xạ view
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

        // Thiết lập RecyclerView
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setHasFixedSize(true);
        // Khởi tạo adapter với danh sách rỗng ban đầu.
        // Danh sách này (productListDisplayed trong adapter) sẽ được cập nhật bởi adapter.
        adapter = new ProductAdapter(this, new ArrayList<>());
        rvProducts.setAdapter(adapter);

        // Ban đầu ẩn phần sản phẩm và nút GỬI BÀN GIAO
        productSection.setVisibility(View.GONE);
        btnSubmitHandover.setVisibility(View.GONE);

        // Sự kiện cho nút ĐÓNG
        btnClose.setOnClickListener(v -> finish());

        // Sự kiện cho nút TẢI DỮ LIỆU CHUYẾN BAY
        btnLoadFlightData.setOnClickListener(v -> {
            String flightDate = edtFlightDate.getText().toString().trim();
            String flightNumber = edtFlightNumber.getText().toString().trim();
            String aircraft = edtAircraft.getText().toString().trim();
            String flightTypeVal = edtFlightType.getText().toString().trim();

            if (flightDate.isEmpty() || flightNumber.isEmpty() || aircraft.isEmpty() || flightTypeVal.isEmpty()) {
                Toast.makeText(CreateHandoverActivity.this, "Vui lòng nhập đầy đủ thông tin chuyến bay!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CreateHandoverActivity.this, "Đang xử lý thông tin chuyến bay...", Toast.LENGTH_SHORT).show();
                loadProductSectionAndData();
            }
        });

        // Sự kiện chọn Tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (allProducts == null || allProducts.isEmpty()) return;

                String categoryKey = "";
                if (tab.getText() != null) {
                    String tabText = tab.getText().toString().toLowerCase(Locale.ROOT);
                    if (tabText.contains("suất ăn nóng")) categoryKey = "hotmeal";
                    else if (tabText.contains("f&b")) categoryKey = "fnb";
                    else if (tabText.contains("lưu niệm")) categoryKey = "souvenir";
                    else if (tabText.contains("sboss business")) categoryKey = "sboss_business";
                }

                if (!categoryKey.isEmpty()) {
                    List<Product> productsForCategory = new ArrayList<>();
                    for (Product p : allProducts) {
                        if (p.getCategory() != null && p.getCategory().equals(categoryKey)) {
                            productsForCategory.add(p);
                        }
                    }
                    // Cập nhật danh sách sản phẩm của category hiện tại cho adapter
                    // Adapter sẽ tự động áp dụng filter tìm kiếm hiện tại (nếu có)
                    adapter.updateProductList(productsForCategory);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Sự kiện chọn ngày bay (giữ nguyên)
        edtFlightDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String date = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
                edtFlightDate.setText(date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Sự kiện cho nút GỬI BÀN GIAO (logic lấy selectedProducts không đổi)
        btnSubmitHandover.setOnClickListener(v -> {
            // ... (code kiểm tra thông tin chuyến bay như cũ) ...

            if (allProducts == null || allProducts.isEmpty()) { // allProducts là nguồn chính chứa trạng thái số lượng
                Toast.makeText(this, "Chưa có dữ liệu sản phẩm.", Toast.LENGTH_LONG).show();
                return;
            }

            List<Product> selectedProducts = new ArrayList<>();
            for (Product p : allProducts) { // Lấy từ allProducts vì uiQuantitySelected được cập nhật trực tiếp trên đó
                if (p.getUiQuantitySelected() > 0) {
                    selectedProducts.add(p);
                }
            }

            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Chưa chọn sản phẩm nào để bàn giao!", Toast.LENGTH_SHORT).show();
            } else {
                // ... (code xử lý gửi bàn giao như cũ, log ra thông tin) ...
                String flightNum = edtFlightNumber.getText().toString().trim();
                String dateStr = edtFlightDate.getText().toString().trim();
                String aircraftNum = edtAircraft.getText().toString().trim();

                StringBuilder handoverDetails = new StringBuilder();
                handoverDetails.append("Bàn giao cho chuyến bay: ").append(flightNum).append("\n");
                handoverDetails.append("Ngày: ").append(dateStr).append(", Tàu: ").append(aircraftNum).append("\n");
                handoverDetails.append("Sản phẩm đã chọn:\n");
                for (Product p : selectedProducts) {
                    handoverDetails.append("- ").append(p.getName()).append(": SL ").append(p.getUiQuantitySelected()).append("\n");
                }
                Toast.makeText(this, "Đã chuẩn bị bàn giao (xem Logcat)", Toast.LENGTH_LONG).show();
                Log.d("HANDOVER_SUBMIT", handoverDetails.toString());
            }
        });

        // Sự kiện tìm kiếm sản phẩm
        edtSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi text thay đổi, gọi phương thức filter của adapter
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProductSectionAndData() {
        productSection.setVisibility(View.VISIBLE);
        btnSubmitHandover.setVisibility(View.VISIBLE);

        allProducts = getMockProducts(); // Tải tất cả sản phẩm từ mock data

        setupTabs(); // Thiết lập các tab

        // Tự động chọn tab đầu tiên để hiển thị sản phẩm ban đầu
        if (tabLayout.getTabCount() > 0 && tabLayout.getSelectedTabPosition() == -1) {
            tabLayout.getTabAt(0).select();
        } else if (tabLayout.getTabCount() > 0) {
            // Nếu đã có tab được chọn (ví dụ khi xoay màn hình và trạng thái được phục hồi)
            // thì kích hoạt lại onTabSelected để đảm bảo dữ liệu được tải đúng
            TabLayout.Tab currentTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
            if (currentTab != null) {
                // Gọi lại logic onTabSelected, hoặc trực tiếp lấy category và gọi update adapter
                String categoryKey = "";
                String tabText = currentTab.getText().toString().toLowerCase(Locale.ROOT);
                if (tabText.contains("suất ăn nóng")) categoryKey = "hotmeal";
                else if (tabText.contains("f&b")) categoryKey = "fnb";
                else if (tabText.contains("lưu niệm")) categoryKey = "souvenir";
                else if (tabText.contains("sboss business")) categoryKey = "sboss_business";

                if (!categoryKey.isEmpty()) {
                    List<Product> productsForCategory = new ArrayList<>();
                    for (Product p : allProducts) {
                        if (p.getCategory() != null && p.getCategory().equals(categoryKey)) {
                            productsForCategory.add(p);
                        }
                    }
                    adapter.updateProductList(productsForCategory);
                }
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

    // Dữ liệu mẫu (getMockProducts không thay đổi, vẫn trả về List<Product>)
    private List<Product> getMockProducts() {
        List<Product> products = new ArrayList<>();
        // Constructor: Product(String name, int imageResId, String category)
        products.add(new Product("Mì Ý", R.drawable.mi_y, "hotmeal"));
        products.add(new Product("Miến xào tôm cua", R.drawable.mien_xao, "hotmeal"));
        products.add(new Product("Bánh chưng", R.drawable.banh_chung, "hotmeal"));

        products.add(new Product("Coca Cola", R.drawable.coca_cola, "fnb"));
        products.add(new Product("Nước suối", R.drawable.water, "fnb"));
        products.add(new Product("Cà phê sữa", R.drawable.cf_sua, "fnb"));

        products.add(new Product("Gấu bông VietJet", R.drawable.gaubong, "souvenir"));
        products.add(new Product("Móc khóa máy bay", R.drawable.mockhoa, "souvenir"));

        products.add(new Product("Rượu Vang Chile", 0, "sboss_business"));
        products.add(new Product("Hạt Macca Úc", 0, "sboss_business"));
        return products;
    }
}