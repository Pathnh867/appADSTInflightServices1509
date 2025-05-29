package com.example.vietflightinventory.models;

public class HandoverItem {

    private String productId; // ID của sản phẩm (liên kết đến Product._id)
    private String productName; // Tên sản phẩm (lưu lại để hiển thị tiện lợi, tránh query lại)
    private String productImageUrl; // URL hình ảnh sản phẩm (tương tự, để hiển thị)
    private double unitPrice; // Đơn giá của sản phẩm tại thời điểm bàn giao

    // Số lượng khi Nhân viên Cung ứng (NVCS) bàn giao cho Tiếp viên (TV)
    private int initialQuantityFromStaff; // Số lượng NVCS dự định giao ban đầu (cho giao ca hoặc top-up)
    private int actualReceivedByFAQuantity; // Số lượng TV xác nhận thực nhận từ NVCS

    // Số lượng liên quan đến việc TV trả hàng lại cho NVCS (sau chuyến bay)
    private int soldQuantityByFA; // Số lượng TV báo đã bán
    private int cancelledQuantityByFA; // Số lượng TV báo hủy (hỏng, hết hạn, v.v.)
    // Số lượng tồn TV trả lại có thể được tính: actualReceivedByFAQuantity - soldQuantityByFA - cancelledQuantityByFA
    private int actualReturnedToStaffQuantity; // Số lượng NVCS xác nhận thực nhận lại từ TV

    // Constructor rỗng
    public HandoverItem() {
    }

    // Constructor cơ bản khi tạo item (ví dụ: NVCS chuẩn bị hàng giao)
    public HandoverItem(String productId, String productName, String productImageUrl, double unitPrice, int initialQuantityFromStaff) {
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.unitPrice = unitPrice;
        this.initialQuantityFromStaff = initialQuantityFromStaff;
    }

    // --- Getters and Setters ---
    // Hãy tạo getters và setters cho tất cả các trường.
    // Trong Android Studio: Chuột phải trong code -> Generate -> Getters and Setters -> Chọn tất cả các trường.

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getInitialQuantityFromStaff() {
        return initialQuantityFromStaff;
    }

    public void setInitialQuantityFromStaff(int initialQuantityFromStaff) {
        this.initialQuantityFromStaff = initialQuantityFromStaff;
    }

    public int getActualReceivedByFAQuantity() {
        return actualReceivedByFAQuantity;
    }

    public void setActualReceivedByFAQuantity(int actualReceivedByFAQuantity) {
        this.actualReceivedByFAQuantity = actualReceivedByFAQuantity;
    }

    public int getSoldQuantityByFA() {
        return soldQuantityByFA;
    }

    public void setSoldQuantityByFA(int soldQuantityByFA) {
        this.soldQuantityByFA = soldQuantityByFA;
    }

    public int getCancelledQuantityByFA() {
        return cancelledQuantityByFA;
    }

    public void setCancelledQuantityByFA(int cancelledQuantityByFA) {
        this.cancelledQuantityByFA = cancelledQuantityByFA;
    }

    public int getActualReturnedToStaffQuantity() {
        return actualReturnedToStaffQuantity;
    }

    public void setActualReturnedToStaffQuantity(int actualReturnedToStaffQuantity) {
        this.actualReturnedToStaffQuantity = actualReturnedToStaffQuantity;
    }

    // toString() để debug (tùy chọn)
    @Override
    public String toString() {
        return "HandoverItem{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", initialQuantityFromStaff=" + initialQuantityFromStaff +
                ", soldQuantityByFA=" + soldQuantityByFA +
                '}';
    }
}