package com.example.vietflightinventory.models;

public class Product {

    private String _id; // ID từ MongoDB
    private String name; // Tên sản phẩm
    private String imageUrl; // URL hình ảnh sản phẩm (cho dữ liệu từ server)
    private int imageResId; // ID tài nguyên ảnh local (giữ lại để tương thích với mock data hiện tại của bạn)
    private String category; // Danh mục: "hotmeal", "fnb", "souvenir", "sboss_business"
    private double price; // Giá sản phẩm
    private String description; // Mô tả chi tiết sản phẩm (nếu có)
    // private int stockKeepingUnit; // SKU - Mã đơn vị lưu kho (nếu cần)

    // Trường này dùng để theo dõi số lượng người dùng chọn trên giao diện (UI state).
    // Nó không phải là thuộc tính cố định của Product trong cơ sở dữ liệu.
    // ProductAdapter của bạn đang sử dụng trường 'quantity' này.
    private int uiQuantitySelected;

    // Constructor rỗng
    public Product() {
        this.uiQuantitySelected = 0; // Khởi tạo số lượng chọn là 0
    }

    // Constructor đầy đủ cho dữ liệu từ DB (không bao gồm uiQuantitySelected)
    public Product(String _id, String name, String imageUrl, String category, double price, String description) {
        this._id = _id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.price = price;
        this.description = description;
        this.uiQuantitySelected = 0; // Mặc định số lượng chọn là 0 khi tải sản phẩm
    }

    // Constructor bạn đã dùng cho mock data trong CreateHandoverActivity
    // Chúng ta sẽ điều chỉnh nó một chút để phù hợp
    public Product(String name, int imageResId, String category) {
        this.name = name;
        this.imageResId = imageResId; // Sử dụng trường imageResId
        this.category = category;
        this.uiQuantitySelected = 0; // Mặc định số lượng chọn là 0
        // Gán giá trị mặc định cho các trường khác nếu cần cho mock data
        this.price = 0.0; // Ví dụ: giá mặc định
        this.imageUrl = null; // Mock data không có image URL
    }

    // --- Getters and Setters ---
    // Hãy tạo getters và setters cho tất cả các trường.

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUiQuantitySelected() {
        return uiQuantitySelected;
    }

    public void setUiQuantitySelected(int uiQuantitySelected) {
        this.uiQuantitySelected = uiQuantitySelected;
    }

    // toString() để debug (tùy chọn)
    @Override
    public String toString() {
        return "Product{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", uiQuantitySelected=" + uiQuantitySelected +
                '}';
    }
}