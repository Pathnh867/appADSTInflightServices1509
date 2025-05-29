// src/main/java/com/example/vietflightinventory/repositories/FlightRepository.java
package com.example.vietflightinventory.repositories;

import android.util.Log;
import com.example.vietflightinventory.helpers.MongoDBHelper;
import com.example.vietflightinventory.models.Flight;
import com.example.vietflightinventory.models.ValidationResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class FlightRepository implements BaseRepository<Flight>, DocumentConverter<Flight> {

    private static final String TAG = "FlightRepository";
    private static FlightRepository instance;

    public static synchronized FlightRepository getInstance() {
        if (instance == null) {
            instance = new FlightRepository();
        }
        return instance;
    }

    private FlightRepository() {}

    @Override
    public void insert(Flight flight, OperationCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                ValidationResult validation = validate(flight);
                if (!validation.isValid()) {
                    callback.onError(validation.getFirstError());
                    return;
                }

                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                // Check if flight number already exists for the same date
                Document existingFlight = collection.find(
                        and(eq("flightNumber", flight.getFlightNumber()),
                                eq("flightDate", flight.getFlightDate()))
                ).first();

                if (existingFlight != null) {
                    callback.onError("Chuyến bay đã tồn tại cho ngày này");
                    return;
                }

                Document doc = toDocument(flight);
                collection.insertOne(doc);

                flight.set_id(doc.getObjectId("_id").toString());
                callback.onSuccess(flight);

            } catch (Exception e) {
                Log.e(TAG, "Error inserting flight", e);
                callback.onError("Lỗi khi tạo chuyến bay: " + e.getMessage());
            }
        });
    }

    @Override
    public void update(Flight flight, BooleanCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                ValidationResult validation = validate(flight);
                if (!validation.isValid()) {
                    callback.onError(validation.getFirstError());
                    return;
                }

                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(flight.get_id());
                Document doc = toDocument(flight);
                doc.remove("_id");

                long modifiedCount = collection.updateOne(
                        eq("_id", objectId),
                        new Document("$set", doc)
                ).getModifiedCount();

                callback.onSuccess(modifiedCount > 0);

            } catch (Exception e) {
                Log.e(TAG, "Error updating flight", e);
                callback.onError("Lỗi khi cập nhật chuyến bay: " + e.getMessage());
            }
        });
    }

    @Override
    public void delete(String id, BooleanCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(id);
                long deletedCount = collection.deleteOne(eq("_id", objectId)).getDeletedCount();

                callback.onSuccess(deletedCount > 0);

            } catch (Exception e) {
                Log.e(TAG, "Error deleting flight", e);
                callback.onError("Lỗi khi xóa chuyến bay: " + e.getMessage());
            }
        });
    }

    @Override
    public void findById(String id, OperationCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(id);
                Document doc = collection.find(eq("_id", objectId)).first();

                if (doc != null) {
                    Flight flight = fromDocument(doc);
                    callback.onSuccess(flight);
                } else {
                    callback.onError("Không tìm thấy chuyến bay");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error finding flight by id", e);
                callback.onError("Lỗi khi tìm chuyến bay: " + e.getMessage());
            }
        });
    }

    @Override
    public void findAll(ListCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Flight> flights = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find()
                        .sort(new Document("flightDate", -1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Flight flight = fromDocument(doc);
                        flights.add(flight);
                    }
                }

                callback.onSuccess(flights);

            } catch (Exception e) {
                Log.e(TAG, "Error finding all flights", e);
                callback.onError("Lỗi khi tải danh sách chuyến bay: " + e.getMessage());
            }
        });
    }

    // Custom methods for Flight
    public void findByFlightNumber(String flightNumber, ListCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Flight> flights = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(eq("flightNumber", flightNumber))
                        .sort(new Document("flightDate", -1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Flight flight = fromDocument(doc);
                        flights.add(flight);
                    }
                }

                callback.onSuccess(flights);

            } catch (Exception e) {
                Log.e(TAG, "Error finding flights by number", e);
                callback.onError("Lỗi khi tìm chuyến bay: " + e.getMessage());
            }
        });
    }

    public void findByDateRange(Date startDate, Date endDate, ListCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Flight> flights = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        and(gte("flightDate", startDate), lte("flightDate", endDate))
                ).sort(new Document("flightDate", 1)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Flight flight = fromDocument(doc);
                        flights.add(flight);
                    }
                }

                callback.onSuccess(flights);

            } catch (Exception e) {
                Log.e(TAG, "Error finding flights by date range", e);
                callback.onError("Lỗi khi tìm chuyến bay theo ngày: " + e.getMessage());
            }
        });
    }

    public void findByStatus(String status, ListCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Flight> flights = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(eq("status", status))
                        .sort(new Document("flightDate", 1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Flight flight = fromDocument(doc);
                        flights.add(flight);
                    }
                }

                callback.onSuccess(flights);

            } catch (Exception e) {
                Log.e(TAG, "Error finding flights by status", e);
                callback.onError("Lỗi khi tìm chuyến bay theo trạng thái: " + e.getMessage());
            }
        });
    }

    @Override
    public ValidationResult validate(Flight flight) {
        if (flight == null) {
            ValidationResult result = new ValidationResult();
            result.addError("Thông tin chuyến bay không được để trống");
            return result;
        }
        return flight.validateForSave();
    }

    @Override
    public Document toDocument(Flight flight) {
        Document doc = new Document();

        if (flight.get_id() != null && !flight.get_id().isEmpty()) {
            doc.append("_id", new ObjectId(flight.get_id()));
        }

        doc.append("flightNumber", flight.getFlightNumber())
                .append("aircraftNumber", flight.getAircraftNumber())
                .append("flightDate", flight.getFlightDate())
                .append("flightType", flight.getFlightType())
                .append("departureAirport", flight.getDepartureAirport())
                .append("arrivalAirport", flight.getArrivalAirport())
                .append("status", flight.getStatus())
                .append("route", flight.getRoute())
                .append("estimatedPassengers", flight.getEstimatedPassengers())
                .append("notes", flight.getNotes())
                .append("createdAt", flight.getCreatedAt() != null ? flight.getCreatedAt() : new Date())
                .append("updatedAt", flight.getUpdatedAt() != null ? flight.getUpdatedAt() : new Date());

        if (flight.getScheduledDepartureTime() != null) {
            doc.append("scheduledDepartureTime", flight.getScheduledDepartureTime());
        }
        if (flight.getScheduledArrivalTime() != null) {
            doc.append("scheduledArrivalTime", flight.getScheduledArrivalTime());
        }
        if (flight.getActualDepartureTime() != null) {
            doc.append("actualDepartureTime", flight.getActualDepartureTime());
        }
        if (flight.getActualArrivalTime() != null) {
            doc.append("actualArrivalTime", flight.getActualArrivalTime());
        }

        return doc;
    }

    @Override
    public Flight fromDocument(Document doc) {
        Flight flight = new Flight();

        if (doc.getObjectId("_id") != null) {
            flight.set_id(doc.getObjectId("_id").toString());
        }

        flight.setFlightNumber(doc.getString("flightNumber"));
        flight.setAircraftNumber(doc.getString("aircraftNumber"));
        flight.setFlightDate(doc.getDate("flightDate"));
        flight.setFlightType(doc.getString("flightType"));
        flight.setDepartureAirport(doc.getString("departureAirport"));
        flight.setArrivalAirport(doc.getString("arrivalAirport"));
        flight.setStatus(doc.getString("status"));
        flight.setRoute(doc.getString("route"));
        flight.setEstimatedPassengers(doc.getInteger("estimatedPassengers", 0));
        flight.setNotes(doc.getString("notes"));
        flight.setScheduledDepartureTime(doc.getDate("scheduledDepartureTime"));
        flight.setScheduledArrivalTime(doc.getDate("scheduledArrivalTime"));
        flight.setActualDepartureTime(doc.getDate("actualDepartureTime"));
        flight.setActualArrivalTime(doc.getDate("actualArrivalTime"));
        flight.setCreatedAt(doc.getDate("createdAt"));
        flight.setUpdatedAt(doc.getDate("updatedAt"));

        return flight;
    }
    public void findByFlightNumber(String flightNumber, OperationCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                Document doc = collection.find(eq("flightNumber", flightNumber)).first();

                if (doc != null) {
                    Flight flight = fromDocument(doc);
                    callback.onSuccess(flight);
                } else {
                    callback.onError("Không tìm thấy chuyến bay");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error finding flight by number", e);
                callback.onError("Lỗi khi tìm chuyến bay: " + e.getMessage());
            }
        });
    }

    public void findTodayFlights(ListCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                // Get today's date range
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date startOfDay = cal.getTime();

                cal.add(Calendar.DAY_OF_MONTH, 1);
                Date startOfNextDay = cal.getTime();

                List<Flight> flights = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        and(
                                gte("flightDate", startOfDay),
                                lt("flightDate", startOfNextDay)
                        )
                ).sort(new Document("flightDate", 1)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Flight flight = fromDocument(doc);
                        flights.add(flight);
                    }
                }

                callback.onSuccess(flights);

            } catch (Exception e) {
                Log.e(TAG, "Error finding today flights", e);
                callback.onError("Lỗi khi tìm chuyến bay hôm nay: " + e.getMessage());
            }
        });
    }

    public void findRecentFlights(int limit, ListCallback<Flight> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getFlightsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Flight> flights = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find()
                        .sort(new Document("flightDate", -1))
                        .limit(limit)
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Flight flight = fromDocument(doc);
                        flights.add(flight);
                    }
                }

                callback.onSuccess(flights);

            } catch (Exception e) {
                Log.e(TAG, "Error finding recent flights", e);
                callback.onError("Lỗi khi tìm chuyến bay gần đây: " + e.getMessage());
            }
        });
    }
}