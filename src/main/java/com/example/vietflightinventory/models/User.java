// src/main/java/com/example/vietflightinventory/models/User.java
package com.example.vietflightinventory.models;

import com.example.vietflightinventory.constants.AppConstants;
import java.util.Date;

public class User {

    private String _id;
    private String username;
    private String password;
    private String fullname;
    private String role;
    private String email;
    private String phoneNumber;
    private String company;
    private String workplace;
    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;

    // Constructor rỗng
    public User() {
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Constructor đầy đủ
    public User(String username, String password, String fullname, String role,
                String email, String phoneNumber, String company, String workplace) {
        this();
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.workplace = workplace;
    }

    // Getters and Setters
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

    // ADD MISSING METHOD
    public String getFullName() {
        return this.fullname;
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

    // ADD MISSING METHOD
    public String getAirport() {
        return this.workplace;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Validation methods
    public ValidationResult validateForSave() {
        ValidationResult result = new ValidationResult();

        if (username == null || username.trim().isEmpty()) {
            result.addError("Tên đăng nhập không được để trống");
        } else if (username.length() < 3) {
            result.addError("Tên đăng nhập phải có ít nhất 3 ký tự");
        }

        if (password == null || password.trim().isEmpty()) {
            result.addError("Mật khẩu không được để trống");
        } else if (password.length() < 6) {
            result.addError("Mật khẩu phải có ít nhất 6 ký tự");
        }

        if (fullname == null || fullname.trim().isEmpty()) {
            result.addError("Họ tên không được để trống");
        }

        if (role == null || role.trim().isEmpty()) {
            result.addError("Vai trò không được để trống");
        } else if (!isValidRole(role)) {
            result.addError("Vai trò không hợp lệ");
        }

        if (email == null || email.trim().isEmpty()) {
            result.addError("Email không được để trống");
        } else if (!isValidEmail(email)) {
            result.addError("Email không hợp lệ");
        }

        return result;
    }

    private boolean isValidRole(String role) {
        return AppConstants.ROLE_ADMINISTRATOR.equals(role) ||
                AppConstants.ROLE_INFLIGHT_SERVICES_STAFF.equals(role) ||
                AppConstants.ROLE_FLIGHT_ATTENDANT.equals(role);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    public boolean hasRole(String requiredRole) {
        return requiredRole.equals(this.role);
    }

    public boolean isAdmin() {
        return AppConstants.ROLE_ADMINISTRATOR.equals(this.role);
    }

    public boolean isStaff() {
        return AppConstants.ROLE_INFLIGHT_SERVICES_STAFF.equals(this.role);
    }

    public boolean isFlightAttendant() {
        return AppConstants.ROLE_FLIGHT_ATTENDANT.equals(this.role);
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", workplace='" + workplace + '\'' +
                '}';
    }
}