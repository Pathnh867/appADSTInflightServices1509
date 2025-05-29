package com.example.vietflightinventory.models;

import java.util.Date; // Bạn có thể cần cho các trường ngày tháng sau này

public class User {

    private String _id; // ID từ MongoDB (thường là ObjectId dưới dạng String)
    private String username; // Tên đăng nhập, có thể là email
    private String password; // Mật khẩu (LƯU Ý: Sẽ cần được mã hóa trước khi lưu vào DB)
    private String fullname; // Họ và tên đầy đủ
    private String role;     // Vai trò: ví dụ "InflightServicesStaff", "FlightAttendant", "Administrator"
    private String email;    // Email
    private String phoneNumber; // Số điện thoại
    private String company;  // Công ty, ví dụ: "VIETJET"
    private String workplace; // Nơi làm việc, ví dụ: "SGN" (Mã sân bay Tân Sơn Nhất)
    // private Date createdAt; // Thời điểm tạo tài khoản (có thể thêm sau)
    // private Date updatedAt; // Thời điểm cập nhật thông tin lần cuối (có thể thêm sau)

    // Constructor rỗng - cần thiết cho một số thư viện/framework (ví dụ: khi lấy dữ liệu từ DB)
    public User() {
    }

    // Constructor đầy đủ (trừ _id vì nó thường được DB tự tạo)
    public User(String username, String password, String fullname, String role,
                String email, String phoneNumber, String company, String workplace) {
        this.username = username;
        this.password = password; // Nhắc lại: đây nên là mật khẩu đã mã hóa
        this.fullname = fullname;
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.workplace = workplace;
    }

    // --- Getters and Setters cho tất cả các trường ---
    // Bạn hãy tự tạo các getter và setter này nhé.
    // Trong Android Studio: Chuột phải trong code -> Generate -> Getters and Setters -> Chọn tất cả các trường.

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    // Ví dụ về toString() để debug (tùy chọn)
    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}