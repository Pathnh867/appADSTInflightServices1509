// Cập nhật HandoverItem.java
package com.example.vietflightinventory.models;

public class HandoverItem {

    private String productId;
    private String productName;
    private String productImageUrl;
    private double unitPrice;
    private int initialQuantityFromStaff;
    private int actualReceivedByFAQuantity;
    private int soldQuantityByFA;
    private int cancelledQuantityByFA;
    private int actualReturnedToStaffQuantity;
    private String notes; // Notes for this specific item
    private String category; // Product category for grouping

    public HandoverItem() {
    }

    public HandoverItem(String productId, String productName, String productImageUrl,
                        double unitPrice, int initialQuantityFromStaff) {
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.unitPrice = unitPrice;
        this.initialQuantityFromStaff = initialQuantityFromStaff;
    }

    // Validation Methods
    public boolean isValidQuantity(int quantity) {
        return quantity >= 0;
    }

    public boolean isValidInitialQuantity() {
        return isValidQuantity(initialQuantityFromStaff) && initialQuantityFromStaff > 0;
    }

    public boolean isValidPrice() {
        return unitPrice >= 0;
    }

    public boolean hasValidProductInfo() {
        return productId != null && !productId.trim().isEmpty() &&
                productName != null && !productName.trim().isEmpty();
    }

    // Utility Methods
    public double getTotalValue() {
        return unitPrice * initialQuantityFromStaff;
    }

    public String getFormattedTotalValue() {
        return String.format("%,.0f VND", getTotalValue());
    }

    public String getFormattedUnitPrice() {
        return String.format("%,.0f VND", unitPrice);
    }

    public int getExpectedReturnQuantity() {
        // Expected return = received - sold - cancelled
        return Math.max(0, actualReceivedByFAQuantity - soldQuantityByFA - cancelledQuantityByFA);
    }

    public int getDiscrepancyQuantity() {
        // Discrepancy = expected return - actual returned
        return getExpectedReturnQuantity() - actualReturnedToStaffQuantity;
    }

    public boolean hasDiscrepancy() {
        return getDiscrepancyQuantity() != 0;
    }

    public String getDiscrepancyStatus() {
        int discrepancy = getDiscrepancyQuantity();
        if (discrepancy > 0) {
            return "Thiếu " + discrepancy;
        } else if (discrepancy < 0) {
            return "Thừa " + Math.abs(discrepancy);
        } else {
            return "Đúng";
        }
    }

    public boolean isFullyProcessed() {
        // Item is fully processed when all quantities are accounted for
        return actualReceivedByFAQuantity > 0 &&
                (soldQuantityByFA + cancelledQuantityByFA + actualReturnedToStaffQuantity) == actualReceivedByFAQuantity;
    }

    public double getRevenue() {
        // Revenue = sold quantity * unit price
        return soldQuantityByFA * unitPrice;
    }

    public String getFormattedRevenue() {
        return String.format("%,.0f VND", getRevenue());
    }

    public double getLossValue() {
        // Loss = cancelled quantity * unit price
        return cancelledQuantityByFA * unitPrice;
    }

    public String getFormattedLossValue() {
        return String.format("%,.0f VND", getLossValue());
    }

    // Validation for complete item data
    public ValidationResult validateForHandover() {
        ValidationResult result = new ValidationResult();

        if (!hasValidProductInfo()) {
            result.addError("Thông tin sản phẩm không hợp lệ");
        }

        if (!isValidInitialQuantity()) {
            result.addError("Số lượng phải lớn hơn 0");
        }

        if (!isValidPrice()) {
            result.addError("Giá sản phẩm không hợp lệ");
        }

        return result;
    }

    public ValidationResult validateForReturn() {
        ValidationResult result = validateForHandover();

        if (actualReceivedByFAQuantity <= 0) {
            result.addError("Chưa có thông tin số lượng thực nhận");
        }

        if (soldQuantityByFA < 0) {
            result.addError("Số lượng đã bán không hợp lệ");
        }

        if (cancelledQuantityByFA < 0) {
            result.addError("Số lượng hủy không hợp lệ");
        }

        if ((soldQuantityByFA + cancelledQuantityByFA) > actualReceivedByFAQuantity) {
            result.addError("Tổng số lượng bán + hủy không thể lớn hơn số lượng thực nhận");
        }

        return result;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getInitialQuantityFromStaff() { return initialQuantityFromStaff; }
    public void setInitialQuantityFromStaff(int initialQuantityFromStaff) {
        this.initialQuantityFromStaff = Math.max(0, initialQuantityFromStaff);
    }

    public int getActualReceivedByFAQuantity() { return actualReceivedByFAQuantity; }
    public void setActualReceivedByFAQuantity(int actualReceivedByFAQuantity) {
        this.actualReceivedByFAQuantity = Math.max(0, actualReceivedByFAQuantity);
    }

    public int getSoldQuantityByFA() { return soldQuantityByFA; }
    public void setSoldQuantityByFA(int soldQuantityByFA) {
        this.soldQuantityByFA = Math.max(0, soldQuantityByFA);
    }

    public int getCancelledQuantityByFA() { return cancelledQuantityByFA; }
    public void setCancelledQuantityByFA(int cancelledQuantityByFA) {
        this.cancelledQuantityByFA = Math.max(0, cancelledQuantityByFA);
    }

    public int getActualReturnedToStaffQuantity() { return actualReturnedToStaffQuantity; }
    public void setActualReturnedToStaffQuantity(int actualReturnedToStaffQuantity) {
        this.actualReturnedToStaffQuantity = Math.max(0, actualReturnedToStaffQuantity);
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return "HandoverItem{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", initialQuantityFromStaff=" + initialQuantityFromStaff +
                ", actualReceivedByFAQuantity=" + actualReceivedByFAQuantity +
                ", soldQuantityByFA=" + soldQuantityByFA +
                ", cancelledQuantityByFA=" + cancelledQuantityByFA +
                ", actualReturnedToStaffQuantity=" + actualReturnedToStaffQuantity +
                ", totalValue=" + getFormattedTotalValue() +
                ", hasDiscrepancy=" + hasDiscrepancy() +
                '}';
    }
}