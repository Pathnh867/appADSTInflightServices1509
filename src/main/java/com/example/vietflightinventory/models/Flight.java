package com.example.vietflightinventory.models;

import java.util.Date;

public class Flight {

    private String _id; // ID từ MongoDB
    private String flightNumber; // Mã chuyến bay, ví dụ: "VJ123"
    private String aircraftNumber; // Mã tàu bay, ví dụ: "VN-A550"
    private Date flightDate; // Ngày bay
    private String flightType; // Loại chuyến bay, ví dụ: "COM 2025" hoặc "Passenger"
    private String departureAirport; // Sân bay khởi hành (ví dụ: "SGN")
    private String arrivalAirport; // Sân bay đến (ví dụ: "HAN")
    private String status; // Trạng thái chuyến bay (ví dụ: "Scheduled", "Boarding", "Departed", "Arrived", "Cancelled")
    // private Date scheduledDepartureTime; // Giờ khởi hành dự kiến
    // private Date scheduledArrivalTime; // Giờ đến dự kiến

    // Constructor rỗng
    public Flight() {
    }

    // Constructor đầy đủ (trừ _id)
    public Flight(String flightNumber, String aircraftNumber, Date flightDate, String flightType,
                  String departureAirport, String arrivalAirport, String status) {
        this.flightNumber = flightNumber;
        this.aircraftNumber = aircraftNumber;
        this.flightDate = flightDate;
        this.flightType = flightType;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.status = status;
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

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAircraftNumber() {
        return aircraftNumber;
    }

    public void setAircraftNumber(String aircraftNumber) {
        this.aircraftNumber = aircraftNumber;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString() để debug (tùy chọn)
    @Override
    public String toString() {
        return "Flight{" +
                "_id='" + _id + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", aircraftNumber='" + aircraftNumber + '\'' +
                ", flightDate=" + flightDate +
                ", flightType='" + flightType + '\'' +
                '}';
    }
}