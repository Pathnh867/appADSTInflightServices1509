// Cập nhật Flight.java
package com.example.vietflightinventory.models;

import com.example.vietflightinventory.constants.AppConstants;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Flight {

    private String _id;
    private String flightNumber;
    private String aircraftNumber;
    private Date flightDate;
    private String flightType;
    private String departureAirport;
    private String arrivalAirport;
    private String status;
    private Date scheduledDepartureTime;
    private Date scheduledArrivalTime;
    private Date actualDepartureTime;
    private Date actualArrivalTime;
    private String route; // e.g., "SGN-HAN"
    private int estimatedPassengers;
    private String notes;
    private Date createdAt;
    private Date updatedAt;

    public Flight() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = AppConstants.FLIGHT_STATUS_SCHEDULED;
    }

    public Flight(String flightNumber, String aircraftNumber, Date flightDate,
                  String flightType, String departureAirport, String arrivalAirport, String status) {
        this();
        this.flightNumber = flightNumber;
        this.aircraftNumber = aircraftNumber;
        this.flightDate = flightDate;
        this.flightType = flightType;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.status = status;
        this.route = departureAirport + "-" + arrivalAirport;
    }

    // Validation Methods
    public boolean isValidFlightNumber() {
        return flightNumber != null &&
                flightNumber.matches("^[A-Z]{2}[0-9]{1,4}$"); // e.g., VJ123
    }

    public boolean isValidAircraftNumber() {
        return aircraftNumber != null &&
                aircraftNumber.matches("^[A-Z]{2}-[A-Z0-9]{4,6}$"); // e.g., VN-A550
    }

    public boolean isValidAirportCode(String code) {
        return code != null &&
                code.matches("^[A-Z]{3}$"); // IATA 3-letter code
    }

    public boolean isValidStatus() {
        return status != null && (
                status.equals(AppConstants.FLIGHT_STATUS_SCHEDULED) ||
                        status.equals(AppConstants.FLIGHT_STATUS_BOARDING) ||
                        status.equals(AppConstants.FLIGHT_STATUS_DEPARTED) ||
                        status.equals(AppConstants.FLIGHT_STATUS_ARRIVED) ||
                        status.equals(AppConstants.FLIGHT_STATUS_CANCELLED)
        );
    }

    public boolean isValidFlightDate() {
        if (flightDate == null) return false;

        // Flight date should not be more than 1 year in the past or future
        Date now = new Date();
        long diffInMillis = Math.abs(flightDate.getTime() - now.getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        return diffInDays <= 365;
    }

    // Utility Methods
    public String getFormattedFlightDate() {
        if (flightDate == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_FORMAT_DISPLAY, Locale.getDefault());
        return sdf.format(flightDate);
    }

    public String getFormattedDepartureTime() {
        if (scheduledDepartureTime == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(scheduledDepartureTime);
    }

    public String getFormattedArrivalTime() {
        if (scheduledArrivalTime == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(scheduledArrivalTime);
    }

    public String getRouteDisplay() {
        if (route != null && !route.isEmpty()) {
            return route;
        }
        return (departureAirport != null ? departureAirport : "???") +
                " - " +
                (arrivalAirport != null ? arrivalAirport : "???");
    }

    public String getStatusDisplayName() {
        switch (status) {
            case AppConstants.FLIGHT_STATUS_SCHEDULED:
                return "Đã Lên Lịch";
            case AppConstants.FLIGHT_STATUS_BOARDING:
                return "Đang Lên Máy Bay";
            case AppConstants.FLIGHT_STATUS_DEPARTED:
                return "Đã Khởi Hành";
            case AppConstants.FLIGHT_STATUS_ARRIVED:
                return "Đã Đến";
            case AppConstants.FLIGHT_STATUS_CANCELLED:
                return "Đã Hủy";
            default:
                return "Không Xác Định";
        }
    }

    public boolean isActive() {
        return !AppConstants.FLIGHT_STATUS_CANCELLED.equals(status);
    }

    public boolean canCreateHandover() {
        return isActive() && (
                AppConstants.FLIGHT_STATUS_SCHEDULED.equals(status) ||
                        AppConstants.FLIGHT_STATUS_BOARDING.equals(status)
        );
    }

    public boolean isCompleted() {
        return AppConstants.FLIGHT_STATUS_ARRIVED.equals(status);
    }

    public String getFlightDuration() {
        if (scheduledDepartureTime == null || scheduledArrivalTime == null) {
            return "";
        }

        long diffInMillis = scheduledArrivalTime.getTime() - scheduledDepartureTime.getTime();
        long hours = TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        long minutes = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS) % 60;

        return String.format("%dh %02dm", hours, minutes);
    }

    // Validation for complete flight data
    public ValidationResult validateForSave() {
        ValidationResult result = new ValidationResult();

        if (!isValidFlightNumber()) {
            result.addError("Số hiệu chuyến bay không hợp lệ (VD: VJ123)");
        }

        if (!isValidAircraftNumber()) {
            result.addError("Số hiệu tàu bay không hợp lệ (VD: VN-A550)");
        }

        if (!isValidAirportCode(departureAirport)) {
            result.addError("Mã sân bay khởi hành không hợp lệ");
        }

        if (!isValidAirportCode(arrivalAirport)) {
            result.addError("Mã sân bay đến không hợp lệ");
        }

        if (departureAirport != null && departureAirport.equals(arrivalAirport)) {
            result.addError("Sân bay khởi hành và đến không thể giống nhau");
        }

        if (!isValidFlightDate()) {
            result.addError("Ngày bay không hợp lệ");
        }

        if (!isValidStatus()) {
            result.addError("Trạng thái chuyến bay không hợp lệ");
        }

        if (scheduledDepartureTime != null && scheduledArrivalTime != null) {
            if (scheduledArrivalTime.before(scheduledDepartureTime)) {
                result.addError("Thời gian đến không thể trước thời gian khởi hành");
            }
        }

        return result;
    }

    // Getters and Setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
        this.updatedAt = new Date();
    }

    public String getAircraftNumber() { return aircraftNumber; }
    public void setAircraftNumber(String aircraftNumber) {
        this.aircraftNumber = aircraftNumber;
        this.updatedAt = new Date();
    }

    public Date getFlightDate() { return flightDate; }
    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
        this.updatedAt = new Date();
    }

    public String getFlightType() { return flightType; }
    public void setFlightType(String flightType) {
        this.flightType = flightType;
        this.updatedAt = new Date();
    }

    public String getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
        this.route = departureAirport + "-" + (arrivalAirport != null ? arrivalAirport : "");
        this.updatedAt = new Date();
    }

    public String getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
        this.route = (departureAirport != null ? departureAirport : "") + "-" + arrivalAirport;
        this.updatedAt = new Date();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = new Date();
    }

    public Date getScheduledDepartureTime() { return scheduledDepartureTime; }
    public void setScheduledDepartureTime(Date scheduledDepartureTime) {
        this.scheduledDepartureTime = scheduledDepartureTime;
        this.updatedAt = new Date();
    }

    public Date getScheduledArrivalTime() { return scheduledArrivalTime; }
    public void setScheduledArrivalTime(Date scheduledArrivalTime) {
        this.scheduledArrivalTime = scheduledArrivalTime;
        this.updatedAt = new Date();
    }

    public Date getActualDepartureTime() { return actualDepartureTime; }
    public void setActualDepartureTime(Date actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
        this.updatedAt = new Date();
    }

    public Date getActualArrivalTime() { return actualArrivalTime; }
    public void setActualArrivalTime(Date actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
        this.updatedAt = new Date();
    }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public int getEstimatedPassengers() { return estimatedPassengers; }
    public void setEstimatedPassengers(int estimatedPassengers) {
        this.estimatedPassengers = estimatedPassengers;
        this.updatedAt = new Date();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Flight{" +
                "_id='" + _id + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", aircraftNumber='" + aircraftNumber + '\'' +
                ", route='" + getRouteDisplay() + '\'' +
                ", flightDate=" + getFormattedFlightDate() +
                ", status='" + status + '\'' +
                '}';
    }
}