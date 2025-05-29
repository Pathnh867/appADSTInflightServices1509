package com.example.vietflightinventory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietflightinventory.R;
import com.example.vietflightinventory.models.Product; // Đảm bảo import đúng model Product

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productListDisplayed; // Danh sách sản phẩm đang được hiển thị (sau khi lọc)
    private List<Product> originalProductListForFilter; // Danh sách gốc cho việc lọc (chỉ chứa sp của category hiện tại)

    public ProductAdapter(Context context, List<Product> productListToDisplay) {
        this.context = context;
        // Quan trọng: Khởi tạo productListDisplayed bằng một *bản sao mới* của danh sách được truyền vào
        // để tránh việc thay đổi danh sách gốc trong Activity khi lọc.
        // CreateHandoverActivity sẽ quản lý việc cập nhật danh sách này thông qua một phương thức.
        this.productListDisplayed = productListToDisplay; // Danh sách này sẽ được cập nhật từ Activity
        this.originalProductListForFilter = new ArrayList<>(productListToDisplay); // Lưu trữ bản gốc để filter
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productListDisplayed.get(position);

        // Sử dụng getter để truy cập các trường
        holder.txtProductName.setText(product.getName());
        holder.txtQuantity.setText(String.valueOf(product.getUiQuantitySelected()));

        // Xử lý hiển thị hình ảnh (ưu tiên imageResId nếu có)
        if (product.getImageResId() != 0) {
            holder.imgProduct.setImageResource(product.getImageResId());
        } else if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // TODO: Tải ảnh từ URL bằng Glide hoặc Picasso
            // Ví dụ: Glide.with(context).load(product.getImageUrl()).placeholder(R.drawable.default_placeholder).into(holder.imgProduct);
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background); // Ảnh placeholder tạm
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background); // Ảnh placeholder mặc định
        }

        // Xử lý sự kiện cho nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = product.getUiQuantitySelected();
            currentQuantity++;
            product.setUiQuantitySelected(currentQuantity); // Cập nhật số lượng trong model
            holder.txtQuantity.setText(String.valueOf(currentQuantity)); // Cập nhật UI
            // Không cần notifyDataSetChanged() ở đây vì chỉ thay đổi 1 item và UI đã được cập nhật trực tiếp
        });

        // Xử lý sự kiện cho nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = product.getUiQuantitySelected();
            if (currentQuantity > 0) {
                currentQuantity--;
                product.setUiQuantitySelected(currentQuantity); // Cập nhật số lượng trong model
                holder.txtQuantity.setText(String.valueOf(currentQuantity)); // Cập nhật UI
            }
        });
    }

    @Override
    public int getItemCount() {
        return productListDisplayed != null ? productListDisplayed.size() : 0;
    }

    // ViewHolder class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtQuantity;
        Button btnIncrease, btnDecrease;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }

    // Phương thức để cập nhật danh sách sản phẩm từ Activity (ví dụ khi đổi category)
    public void updateProductList(List<Product> newProductList) {
        this.originalProductListForFilter.clear();
        this.originalProductListForFilter.addAll(newProductList);
        // Áp dụng lại filter hiện tại (nếu có) hoặc hiển thị toàn bộ danh sách mới
        filter(currentSearchTerm); // currentSearchTerm là một biến lưu trữ từ khóa tìm kiếm hiện tại
    }

    private String currentSearchTerm = ""; // Lưu trữ từ khóa tìm kiếm hiện tại

    // Phương thức lọc sản phẩm (được gọi từ Activity khi text trong edtSearchProduct thay đổi)
    public void filter(String searchTerm) {
        this.currentSearchTerm = searchTerm.toLowerCase(Locale.ROOT);
        productListDisplayed.clear(); // Xóa danh sách đang hiển thị

        if (this.currentSearchTerm.isEmpty()) {
            // Nếu không có từ khóa tìm kiếm, hiển thị toàn bộ danh sách gốc (của category hiện tại)
            productListDisplayed.addAll(originalProductListForFilter);
        } else {
            for (Product product : originalProductListForFilter) {
                if (product.getName() != null && product.getName().toLowerCase(Locale.ROOT).contains(this.currentSearchTerm)) {
                    productListDisplayed.add(product);
                }
            }
        }
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật lại giao diện
    }
}