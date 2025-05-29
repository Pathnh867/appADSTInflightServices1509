// src/main/java/com/example/vietflightinventory/constants/AppConstants.java
package com.example.vietflightinventory.constants;

public class AppConstants {

    // User Roles
    public static final String ROLE_ADMINISTRATOR = "Administrator";
    public static final String ROLE_INFLIGHT_SERVICES_STAFF = "InflightServicesStaff";
    public static final String ROLE_FLIGHT_ATTENDANT = "FlightAttendant";

    // Product Categories
    public static final String CATEGORY_HOT_MEAL = "hotmeal";
    public static final String CATEGORY_FNB = "fnb";
    public static final String CATEGORY_SOUVENIR = "souvenir";
    public static final String CATEGORY_SBOSS_BUSINESS = "sboss_business";

    // Handover Status
    public static final String HANDOVER_STATUS_PENDING_FA_APPROVAL = "PENDING_FA_APPROVAL";
    public static final String HANDOVER_STATUS_CONFIRMED_BY_FA = "CONFIRMED_BY_FA";
    public static final String HANDOVER_STATUS_PENDING_STAFF_APPROVAL_RETURN = "PENDING_STAFF_APPROVAL_RETURN";
    public static final String HANDOVER_STATUS_COMPLETED = "COMPLETED";
    public static final String HANDOVER_STATUS_CANCELLED = "CANCELLED";

    // Handover Types
    public static final String HANDOVER_TYPE_OUTBOUND = "OUTBOUND";
    public static final String HANDOVER_TYPE_INBOUND = "INBOUND";
    public static final String HANDOVER_TYPE_RETURN = "RETURN";

    // Flight Status
    public static final String FLIGHT_STATUS_SCHEDULED = "SCHEDULED";
    public static final String FLIGHT_STATUS_BOARDING = "BOARDING";
    public static final String FLIGHT_STATUS_DEPARTED = "DEPARTED";
    public static final String FLIGHT_STATUS_ARRIVED = "ARRIVED";
    public static final String FLIGHT_STATUS_CANCELLED = "CANCELLED";
    public static final String FLIGHT_STATUS_DELAYED = "DELAYED";

    // Database Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_FLIGHTS = "flights";
    public static final String COLLECTION_PRODUCTS = "products";
    public static final String COLLECTION_HANDOVERS = "handovers";

    // Validation Constants
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PRODUCT_NAME_LENGTH = 100;
    public static final int MAX_HANDOVER_NOTES_LENGTH = 500;

    // Date Formats
    public static final String DATE_FORMAT_DISPLAY = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT_DISPLAY = "dd/MM/yyyy HH:mm";
    public static final String DATE_FORMAT_API = "yyyy-MM-dd";

    // Airports
    public static final String AIRPORT_SGN = "SGN"; // Tân Sơn Nhất
    public static final String AIRPORT_HAN = "HAN"; // Nội Bài
    public static final String AIRPORT_DAD = "DAD"; // Đà Nẵng
    public static final String AIRPORT_CXR = "CXR"; // Cam Ranh

    // Companies
    public static final String COMPANY_VIETJET = "VIETJET";

    // Preferences Keys
    public static final String PREF_USER_SESSION = "user_session";
    public static final String PREF_LAST_LOGIN = "last_login";
    public static final String PREF_REMEMBER_LOGIN = "remember_login";

    // Request Codes
    public static final int REQUEST_CODE_LOGIN = 1001;
    public static final int REQUEST_CODE_CREATE_HANDOVER = 1002;
    public static final int REQUEST_CODE_RECEIVE_HANDOVER = 1003;

    // Error Messages
    public static final String ERROR_NETWORK = "Lỗi kết nối mạng";
    public static final String ERROR_DATABASE = "Lỗi cơ sở dữ liệu";
    public static final String ERROR_AUTHENTICATION = "Lỗi xác thực";
    public static final String ERROR_PERMISSION_DENIED = "Không có quyền truy cập";
    public static final String ERROR_INVALID_INPUT = "Dữ liệu đầu vào không hợp lệ";

    // Success Messages
    public static final String SUCCESS_LOGIN = "Đăng nhập thành công";
    public static final String SUCCESS_LOGOUT = "Đăng xuất thành công";
    public static final String SUCCESS_HANDOVER_CREATED = "Tạo bàn giao thành công";
    public static final String SUCCESS_HANDOVER_RECEIVED = "Nhận bàn giao thành công";

    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}