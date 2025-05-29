package com.example.vietflightinventory.models;

import java.util.Date;
import java.util.List;
import java.util.ArrayList; // Import ArrayList

public class Handover {

    private String _id; // ID từ MongoDB

    // Thông tin chuyến bay liên quan
    private String flightId; // ID liên kết đến Flight._id
    private String flightNumberDisplay; // Mã chuyến bay để hiển thị (ví dụ: "VJ123")
    private String aircraftNumberDisplay; // Mã tàu bay để hiển thị (ví dụ: "VN-A629")
    private Date flightDateDisplay; // Ngày bay để hiển thị

    // Thông tin người dùng liên quan
    private String createdByUserId; // ID của User tạo bàn giao (NVCS hoặc TV tùy chiều)
    private String createdByUserNameDisplay; // Tên người tạo để hiển thị
    private String receivedByUserId; // ID của User nhận bàn giao (TV hoặc NVCS tùy chiều)
    private String receivedByUserNameDisplay; // Tên người nhận để hiển thị

    // Thời gian thực hiện các bước
    private Date creationTimestamp; // Thời điểm bắt đầu tạo bản ghi bàn giao
    private Date faConfirmationTimestamp; // Thời điểm Tiếp viên (FA) xác nhận nhận hàng từ NVCS
    private Date faReturnTimestamp; // Thời điểm Tiếp viên (FA) gửi trả hàng cho NVCS
    private Date staffConfirmationReturnTimestamp; // Thời điểm NVCS xác nhận nhận lại hàng từ FA
    private Date lastUpdatedTimestamp; // Thời điểm cập nhật cuối cùng

    // Trạng thái và loại bàn giao
    private String status;
    // Ví dụ các trạng thái: "DRAFT" (NVCS đang soạn thảo),
    // "PENDING_FA_APPROVAL" (Chờ TV xác nhận nhận),
    // "FA_CONFIRMED_RECEIVED" (TV đã xác nhận nhận),
    // "FA_RETURN_DRAFT" (TV đang soạn thảo trả hàng),
    // "PENDING_STAFF_APPROVAL_RETURN" (Chờ NVCS xác nhận nhận lại),
    // "STAFF_CONFIRMED_RETURN" (NVCS đã xác nhận nhận lại),
    // "LOCKED" (Đã khóa, không chỉnh sửa),
    // "CANCELLED" (Đã hủy)
    private String handoverType;
    // Ví dụ các loại: "STAFF_TO_FA_NEW_CREW" (NVCS giao cho tổ TV mới),
    // "STAFF_TO_FA_TOPUP" (NVCS giao bổ sung cho TV),
    // "FA_TO_STAFF_RETURN" (TV trả hàng cho NVCS)

    private List<HandoverItem> items; // Danh sách chi tiết các sản phẩm trong bàn giao

    private boolean isLocked; // Trạng thái khóa/mở khóa
    private String notes; // Ghi chú chung cho lần bàn giao (nếu có)

    // Constructor rỗng
    public Handover() {
        this.items = new ArrayList<>(); // Khởi tạo danh sách rỗng
        this.isLocked = false; // Mặc định là chưa khóa
    }

    // Constructor cơ bản (có thể thêm các trường khác nếu cần)
    public Handover(String flightId, String flightNumberDisplay, String aircraftNumberDisplay, Date flightDateDisplay,
                    String createdByUserId, String createdByUserNameDisplay, String handoverType) {
        this(); // Gọi constructor rỗng để khởi tạo items và isLocked
        this.flightId = flightId;
        this.flightNumberDisplay = flightNumberDisplay;
        this.aircraftNumberDisplay = aircraftNumberDisplay;
        this.flightDateDisplay = flightDateDisplay;
        this.createdByUserId = createdByUserId;
        this.createdByUserNameDisplay = createdByUserNameDisplay;
        this.handoverType = handoverType;
        this.creationTimestamp = new Date(); // Thời điểm tạo là hiện tại
        this.status = "DRAFT"; // Trạng thái ban đầu khi mới tạo
    }

    // --- Getters and Setters ---
    // Hãy tạo getters và setters cho tất cả các trường.
    // Trong Android Studio: Chuột phải trong code -> Generate -> Getters and Setters -> Chọn tất cả các trường.


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumberDisplay() {
        return flightNumberDisplay;
    }

    public void setFlightNumberDisplay(String flightNumberDisplay) {
        this.flightNumberDisplay = flightNumberDisplay;
    }

    public String getAircraftNumberDisplay() {
        return aircraftNumberDisplay;
    }

    public void setAircraftNumberDisplay(String aircraftNumberDisplay) {
        this.aircraftNumberDisplay = aircraftNumberDisplay;
    }

    public Date getFlightDateDisplay() {
        return flightDateDisplay;
    }

    public void setFlightDateDisplay(Date flightDateDisplay) {
        this.flightDateDisplay = flightDateDisplay;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedByUserNameDisplay() {
        return createdByUserNameDisplay;
    }

    public void setCreatedByUserNameDisplay(String createdByUserNameDisplay) {
        this.createdByUserNameDisplay = createdByUserNameDisplay;
    }

    public String getReceivedByUserId() {
        return receivedByUserId;
    }

    public void setReceivedByUserId(String receivedByUserId) {
        this.receivedByUserId = receivedByUserId;
    }

    public String getReceivedByUserNameDisplay() {
        return receivedByUserNameDisplay;
    }

    public void setReceivedByUserNameDisplay(String receivedByUserNameDisplay) {
        this.receivedByUserNameDisplay = receivedByUserNameDisplay;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Date getFaConfirmationTimestamp() {
        return faConfirmationTimestamp;
    }

    public void setFaConfirmationTimestamp(Date faConfirmationTimestamp) {
        this.faConfirmationTimestamp = faConfirmationTimestamp;
    }

    public Date getFaReturnTimestamp() {
        return faReturnTimestamp;
    }

    public void setFaReturnTimestamp(Date faReturnTimestamp) {
        this.faReturnTimestamp = faReturnTimestamp;
    }

    public Date getStaffConfirmationReturnTimestamp() {
        return staffConfirmationReturnTimestamp;
    }

    public void setStaffConfirmationReturnTimestamp(Date staffConfirmationReturnTimestamp) {
        this.staffConfirmationReturnTimestamp = staffConfirmationReturnTimestamp;
    }

    public Date getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(Date lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHandoverType() {
        return handoverType;
    }

    public void setHandoverType(String handoverType) {
        this.handoverType = handoverType;
    }

    public List<HandoverItem> getItems() {
        return items;
    }

    public void setItems(List<HandoverItem> items) {
        this.items = items;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // toString() để debug (tùy chọn)
    @Override
    public String toString() {
        return "Handover{" +
                "_id='" + _id + '\'' +
                ", flightNumberDisplay='" + flightNumberDisplay + '\'' +
                ", status='" + status + '\'' +
                ", handoverType='" + handoverType + '\'' +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                ", isLocked=" + isLocked +
                '}';
    }
}