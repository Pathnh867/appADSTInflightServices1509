// Cập nhật Product.java
package com.example.vietflightinventory.models;

import com.example.vietflightinventory.constants.AppConstants;

public class Product {

    private String _id;
    private String name;
    private String imageUrl;
    private int imageResId;
    private String category;
    private double price;
    private String description;
    private String sku; // Stock Keeping Unit
    private boolean isActive;
    private int uiQuantitySelected;

    public Product() {
        this.uiQuantitySelected = 0;
        this.isActive = true;
    }

    public Product(String _id, String name, String imageUrl, String category,
                   double price, String description) {
        this();
        this._id = _id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.price = price;
        this.description = description;
    }

    public Product(String name, int imageResId, String category) {
        this();
        this.name = name;
        this.imageResId = imageResId;
        this.category = category;
        this.price = 0.0;
        this.imageUrl = null;
    }

    // Validation Methods
    public boolean isValidCategory() {
        return category != null && (
                category.equals(AppConstants.CATEGORY_HOT_MEAL) ||
                        category.equals(AppConstants.CATEGORY_FNB) ||
                        category.equals(AppConstants.CATEGORY_SOUVENIR) ||
                        category.equals(AppConstants.CATEGORY_SBOSS_BUSINESS)
        );
    }

    public boolean isValidPrice() {
        return price >= 0;
    }

    public boolean isValidName() {
        return name != null && !name.trim().isEmpty();
    }

    // Utility Methods
    public String getCategoryDisplayName() {
        switch (category) {
            case AppConstants.CATEGORY_HOT_MEAL:
                return "Suất Ăn Nóng";
            case AppConstants.CATEGORY_FNB:
                return "F & B";
            case AppConstants.CATEGORY_SOUVENIR:
                return "Hàng Lưu Niệm";
            case AppConstants.CATEGORY_SBOSS_BUSINESS:
                return "Sboss Business";
            default:
                return "Khác";
        }
    }

    public String getFormattedPrice() {
        return String.format("%,.0f VND", price);
    }

    public boolean hasImage() {
        return (imageUrl != null && !imageUrl.trim().isEmpty()) || imageResId != 0;
    }

    // Validation for complete product data
    public ValidationResult validateForSave() {
        ValidationResult result = new ValidationResult();

        if (!isValidName()) {
            result.addError("Tên sản phẩm không được để trống");
        }

        if (!isValidCategory()) {
            result.addError("Danh mục sản phẩm không hợp lệ");
        }

        if (!isValidPrice()) {
            result.addError("Giá sản phẩm không hợp lệ");
        }

        return result;
    }

    // Getters and Setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getUiQuantitySelected() { return uiQuantitySelected; }
    public void setUiQuantitySelected(int uiQuantitySelected) {
        this.uiQuantitySelected = Math.max(0, uiQuantitySelected);
    }

    @Override
    public String toString() {
        return "Product{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", uiQuantitySelected=" + uiQuantitySelected +
                ", isActive=" + isActive +
                '}';
    }
}