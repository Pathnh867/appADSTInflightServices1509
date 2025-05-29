// Cập nhật Handover.java
package com.example.vietflightinventory.models;

import com.example.vietflightinventory.constants.AppConstants;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Handover {

    private String _id;
    private String flightId;
    private String flightNumberDisplay;
    private String aircraftNumberDisplay;
    private Date flightDateDisplay;
    private String createdByUserId;
    private String createdByUserNameDisplay;
    private String receivedByUserId;
    private String receivedByUserNameDisplay;
    private Date creationTimestamp;
    private Date faConfirmationTimestamp;
    private Date faReturnTimestamp;
    private Date staffConfirmationReturnTimestamp;
    private Date lastUpdatedTimestamp;
    private String status;
    private String handoverType;
    private List<HandoverItem> items;
    private boolean isLocked;
    private String notes;
    private String handoverCode; // Unique code for tracking
    private double totalValue; // Total value of handover

    public Handover() {
        this.items = new ArrayList<>();
        this.isLocked = false;
        this.creationTimestamp = new Date();
        this.lastUpdatedTimestamp = new Date();
        this.status = AppConstants.STATUS_DRAFT;
        this.totalValue = 0.0;
        this.handoverCode = generateHandoverCode();
    }

    public Handover(String flightId, String flightNumberDisplay, String aircraftNumberDisplay,
                    Date flightDateDisplay, String createdByUserId, String createdByUserNameDisplay,
                    String handoverType) {
        this();
        this.flightId = flightId;
        this.flightNumberDisplay = flightNumberDisplay;
        this.aircraftNumberDisplay = aircraftNumberDisplay;
        this.flightDateDisplay = flightDateDisplay;
        this.createdByUserId = createdByUserId;
        this.createdByUserNameDisplay = createdByUserNameDisplay;
        this.handoverType = handoverType;
    }

    // Validation Methods
    public boolean isValidStatus() {
        return status != null && (
                status.equals(AppConstants.STATUS_DRAFT) ||
                        status.equals(AppConstants.STATUS_PENDING_FA_APPROVAL) ||
                        status.equals(AppConstants.STATUS_FA_CONFIRMED_RECEIVED) ||
                        status.equals(AppConstants.STATUS_FA_RETURN_DRAFT) ||
                        status.equals(AppConstants.STATUS_PENDING_STAFF_APPROVAL_RETURN) ||
                        status.equals(AppConstants.STATUS_STAFF_CONFIRMED_RETURN) ||
                        status.equals(AppConstants.STATUS_LOCKED) ||
                        status.equals(AppConstants.STATUS_CANCELLED)
        );
    }

    public boolean isValidHandoverType() {
        return handoverType != null && (
                handoverType.equals(AppConstants.HANDOVER_STAFF_TO_FA_NEW_CREW) ||
                        handoverType.equals(AppConstants.HANDOVER_STAFF_TO_FA_TOPUP) ||
                        handoverType.equals(AppConstants.HANDOVER_FA_TO_STAFF_RETURN)
        );
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public boolean hasValidItems() {
        if (!hasItems()) return false;

        for (HandoverItem item : items) {
            if (item.getInitialQuantityFromStaff() <= 0) {
                return false;
            }
        }
        return true;
    }

    // Utility Methods
    public String getStatusDisplayName() {
        switch (status) {
            case AppConstants.STATUS_DRAFT:
                return "Bản Nháp";
            case AppConstants.STATUS_PENDING_FA_APPROVAL:
                return "Chờ TV Xác Nhận";
            case AppConstants.STATUS_FA_CONFIRMED_RECEIVED:
                return "TV Đã Xác Nhận Nhận";
            case AppConstants.STATUS_FA_RETURN_DRAFT:
                return "TV Đang Soạn Trả Hàng";
            case AppConstants.STATUS_PENDING_STAFF_APPROVAL_RETURN:
                return "Chờ NVCS Xác Nhận Nhận Lại";
            case AppConstants.STATUS_STAFF_CONFIRMED_RETURN:
                return "NVCS Đã Xác Nhận Nhận Lại";
            case AppConstants.STATUS_LOCKED:
                return "Đã Khóa";
            case AppConstants.STATUS_CANCELLED:
                return "Đã Hủy";
            default:
                return "Không Xác Định";
        }
    }

    public String getHandoverTypeDisplayName() {
        switch (handoverType) {
            case AppConstants.HANDOVER_STAFF_TO_FA_NEW_CREW:
                return "Giao Ca Mới";
            case AppConstants.HANDOVER_STAFF_TO_FA_TOPUP:
                return "Bổ Sung Hàng";
            case AppConstants.HANDOVER_FA_TO_STAFF_RETURN:
                return "Trả Hàng";
            default:
                return "Khác";
        }
    }

    public String getFormattedCreationTime() {
        if (creationTimestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATETIME_FORMAT_FULL, Locale.getDefault());
        return sdf.format(creationTimestamp);
    }

    public String getFormattedFlightDate() {
        if (flightDateDisplay == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_FORMAT_DISPLAY, Locale.getDefault());
        return sdf.format(flightDateDisplay);
    }

    public int getTotalItemsCount() {
        if (!hasItems()) return 0;

        int total = 0;
        for (HandoverItem item : items) {
            total += item.getInitialQuantityFromStaff();
        }
        return total;
    }

    public int getTotalItemTypes() {
        return hasItems() ? items.size() : 0;
    }

    public double calculateTotalValue() {
        if (!hasItems()) return 0.0;

        double total = 0.0;
        for (HandoverItem item : items) {
            total += item.getUnitPrice() * item.getInitialQuantityFromStaff();
        }
        this.totalValue = total;
        return total;
    }

    public String getFormattedTotalValue() {
        return String.format("%,.0f VND", calculateTotalValue());
    }

    public boolean canEdit() {
        return !isLocked && (
                AppConstants.STATUS_DRAFT.equals(status) ||
                        AppConstants.STATUS_FA_RETURN_DRAFT.equals(status)
        );
    }

    public boolean canSubmit() {
        return canEdit() && hasValidItems();
    }

    public boolean canConfirm() {
        return !isLocked && (
                AppConstants.STATUS_PENDING_FA_APPROVAL.equals(status) ||
                        AppConstants.STATUS_PENDING_STAFF_APPROVAL_RETURN.equals(status)
        );
    }

    public boolean canCancel() {
        return !isLocked && !AppConstants.STATUS_LOCKED.equals(status) &&
                !AppConstants.STATUS_CANCELLED.equals(status);
    }

    public boolean isStaffToFAHandover() {
        return AppConstants.HANDOVER_STAFF_TO_FA_NEW_CREW.equals(handoverType) ||
                AppConstants.HANDOVER_STAFF_TO_FA_TOPUP.equals(handoverType);
    }

    public boolean isFAToStaffHandover() {
        return AppConstants.HANDOVER_FA_TO_STAFF_RETURN.equals(handoverType);
    }

    private String generateHandoverCode() {
        // Generate a unique handover code based on timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return "HO" + sdf.format(new Date());
    }

    public void addItem(HandoverItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        calculateTotalValue();
        this.lastUpdatedTimestamp = new Date();
    }

    public void removeItem(HandoverItem item) {
        if (items != null) {
            items.remove(item);
            calculateTotalValue();
            this.lastUpdatedTimestamp = new Date();
        }
    }

    public HandoverItem findItemByProductId(String productId) {
        if (!hasItems() || productId == null) return null;

        for (HandoverItem item : items) {
            if (productId.equals(item.getProductId())) {
                return item;
            }
        }
        return null;
    }

    // State transition methods
    public void submitForApproval() {
        if (canSubmit()) {
            if (isStaffToFAHandover()) {
                this.status = AppConstants.STATUS_PENDING_FA_APPROVAL;
            } else if (isFAToStaffHandover()) {
                this.status = AppConstants.STATUS_PENDING_STAFF_APPROVAL_RETURN;
                this.faReturnTimestamp = new Date();
            }
            this.lastUpdatedTimestamp = new Date();
        }
    }

    public void confirmReceived(String receivedByUserId, String receivedByUserName) {
        if (canConfirm()) {
            this.receivedByUserId = receivedByUserId;
            this.receivedByUserNameDisplay = receivedByUserName;

            if (AppConstants.STATUS_PENDING_FA_APPROVAL.equals(status)) {
                this.status = AppConstants.STATUS_FA_CONFIRMED_RECEIVED;
                this.faConfirmationTimestamp = new Date();
            } else if (AppConstants.STATUS_PENDING_STAFF_APPROVAL_RETURN.equals(status)) {
                this.status = AppConstants.STATUS_STAFF_CONFIRMED_RETURN;
                this.staffConfirmationReturnTimestamp = new Date();
            }
            this.lastUpdatedTimestamp = new Date();
        }
    }

    public void lockHandover() {
        this.status = AppConstants.STATUS_LOCKED;
        this.isLocked = true;
        this.lastUpdatedTimestamp = new Date();
    }

    public void cancelHandover() {
        if (canCancel()) {
            this.status = AppConstants.STATUS_CANCELLED;
            this.lastUpdatedTimestamp = new Date();
        }
    }

    // Validation for complete handover data
    public ValidationResult validateForSubmit() {
        ValidationResult result = new ValidationResult();

        if (flightId == null || flightId.trim().isEmpty()) {
            result.addError("Chuyến bay không được để trống");
        }

        if (createdByUserId == null || createdByUserId.trim().isEmpty()) {
            result.addError("Người tạo không được để trống");
        }

        if (!isValidHandoverType()) {
            result.addError("Loại bàn giao không hợp lệ");
        }

        if (!hasValidItems()) {
            result.addError("Phải có ít nhất một sản phẩm với số lượng > 0");
        }

        return result;
    }

    // Getters and Setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) {
        this.flightId = flightId;
        this.lastUpdatedTimestamp = new Date();
    }

    public String getFlightNumberDisplay() { return flightNumberDisplay; }
    public void setFlightNumberDisplay(String flightNumberDisplay) {
        this.flightNumberDisplay = flightNumberDisplay;
        this.lastUpdatedTimestamp = new Date();
    }

    public String getAircraftNumberDisplay() { return aircraftNumberDisplay; }
    public void setAircraftNumberDisplay(String aircraftNumberDisplay) {
        this.aircraftNumberDisplay = aircraftNumberDisplay;
        this.lastUpdatedTimestamp = new Date();
    }

    public Date getFlightDateDisplay() { return flightDateDisplay; }
    public void setFlightDateDisplay(Date flightDateDisplay) {
        this.flightDateDisplay = flightDateDisplay;
        this.lastUpdatedTimestamp = new Date();
    }

    public String getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(String createdByUserId) { this.createdByUserId = createdByUserId; }

    public String getCreatedByUserNameDisplay() { return createdByUserNameDisplay; }
    public void setCreatedByUserNameDisplay(String createdByUserNameDisplay) {
        this.createdByUserNameDisplay = createdByUserNameDisplay;
    }

    public String getReceivedByUserId() { return receivedByUserId; }
    public void setReceivedByUserId(String receivedByUserId) { this.receivedByUserId = receivedByUserId; }

    public String getReceivedByUserNameDisplay() { return receivedByUserNameDisplay; }
    public void setReceivedByUserNameDisplay(String receivedByUserNameDisplay) {
        this.receivedByUserNameDisplay = receivedByUserNameDisplay;
    }

    public Date getCreationTimestamp() { return creationTimestamp; }
    public void setCreationTimestamp(Date creationTimestamp) { this.creationTimestamp = creationTimestamp; }

    public Date getFaConfirmationTimestamp() { return faConfirmationTimestamp; }
    public void setFaConfirmationTimestamp(Date faConfirmationTimestamp) {
        this.faConfirmationTimestamp = faConfirmationTimestamp;
    }

    public Date getFaReturnTimestamp() { return faReturnTimestamp; }
    public void setFaReturnTimestamp(Date faReturnTimestamp) { this.faReturnTimestamp = faReturnTimestamp; }

    public Date getStaffConfirmationReturnTimestamp() { return staffConfirmationReturnTimestamp; }
    public void setStaffConfirmationReturnTimestamp(Date staffConfirmationReturnTimestamp) {
        this.staffConfirmationReturnTimestamp = staffConfirmationReturnTimestamp;
    }

    public Date getLastUpdatedTimestamp() { return lastUpdatedTimestamp; }
    public void setLastUpdatedTimestamp(Date lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.lastUpdatedTimestamp = new Date();
    }

    public String getHandoverType() { return handoverType; }
    public void setHandoverType(String handoverType) { this.handoverType = handoverType; }

    public List<HandoverItem> getItems() { return items; }
    public void setItems(List<HandoverItem> items) {
        this.items = items;
        calculateTotalValue();
        this.lastUpdatedTimestamp = new Date();
    }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) {
        isLocked = locked;
        this.lastUpdatedTimestamp = new Date();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        this.lastUpdatedTimestamp = new Date();
    }

    public String getHandoverCode() { return handoverCode; }
    public void setHandoverCode(String handoverCode) { this.handoverCode = handoverCode; }

    public double getTotalValue() { return totalValue; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }

    @Override
    public String toString() {
        return "Handover{" +
                "_id='" + _id + '\'' +
                ", handoverCode='" + handoverCode + '\'' +
                ", flightNumberDisplay='" + flightNumberDisplay + '\'' +
                ", status='" + status + '\'' +
                ", handoverType='" + handoverType + '\'' +
                ", itemsCount=" + getTotalItemTypes() +
                ", totalValue=" + getFormattedTotalValue() +
                ", isLocked=" + isLocked +
                '}';
    }
}