// src/main/java/com/example/vietflightinventory/repositories/HandoverRepository.java
package com.example.vietflightinventory.repositories;

import android.util.Log;
import com.example.vietflightinventory.helpers.MongoDBHelper;
import com.example.vietflightinventory.models.Handover;
import com.example.vietflightinventory.models.HandoverItem;
import com.example.vietflightinventory.models.ValidationResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class HandoverRepository implements BaseRepository<Handover>, DocumentConverter<Handover> {

    private static final String TAG = "HandoverRepository";
    private static HandoverRepository instance;

    public static synchronized HandoverRepository getInstance() {
        if (instance == null) {
            instance = new HandoverRepository();
        }
        return instance;
    }

    private HandoverRepository() {}

    @Override
    public void insert(Handover handover, OperationCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                ValidationResult validation = validate(handover);
                if (!validation.isValid()) {
                    callback.onError(validation.getFirstError());
                    return;
                }

                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                Document doc = toDocument(handover);
                collection.insertOne(doc);

                handover.set_id(doc.getObjectId("_id").toString());
                callback.onSuccess(handover);

            } catch (Exception e) {
                Log.e(TAG, "Error inserting handover", e);
                callback.onError("Lỗi khi tạo bàn giao: " + e.getMessage());
            }
        });
    }

    @Override
    public void update(Handover handover, BooleanCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                ValidationResult validation = validate(handover);
                if (!validation.isValid()) {
                    callback.onError(validation.getFirstError());
                    return;
                }

                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(handover.get_id());
                Document doc = toDocument(handover);
                doc.remove("_id");

                long modifiedCount = collection.updateOne(
                        eq("_id", objectId),
                        new Document("$set", doc)
                ).getModifiedCount();

                callback.onSuccess(modifiedCount > 0);

            } catch (Exception e) {
                Log.e(TAG, "Error updating handover", e);
                callback.onError("Lỗi khi cập nhật bàn giao: " + e.getMessage());
            }
        });
    }

    @Override
    public void delete(String id, BooleanCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(id);
                long deletedCount = collection.deleteOne(eq("_id", objectId)).getDeletedCount();

                callback.onSuccess(deletedCount > 0);

            } catch (Exception e) {
                Log.e(TAG, "Error deleting handover", e);
                callback.onError("Lỗi khi xóa bàn giao: " + e.getMessage());
            }
        });
    }

    @Override
    public void findById(String id, OperationCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(id);
                Document doc = collection.find(eq("_id", objectId)).first();

                if (doc != null) {
                    Handover handover = fromDocument(doc);
                    callback.onSuccess(handover);
                } else {
                    callback.onError("Không tìm thấy bàn giao");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error finding handover by id", e);
                callback.onError("Lỗi khi tìm bàn giao: " + e.getMessage());
            }
        });
    }

    @Override
    public void findAll(ListCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Handover> handovers = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find()
                        .sort(new Document("creationTimestamp", -1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Handover handover = fromDocument(doc);
                        handovers.add(handover);
                    }
                }

                callback.onSuccess(handovers);

            } catch (Exception e) {
                Log.e(TAG, "Error finding all handovers", e);
                callback.onError("Lỗi khi tải danh sách bàn giao: " + e.getMessage());
            }
        });
    }

    // Custom methods for Handover
    public void findByFlightId(String flightId, ListCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Handover> handovers = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(eq("flightId", flightId))
                        .sort(new Document("creationTimestamp", -1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Handover handover = fromDocument(doc);
                        handovers.add(handover);
                    }
                }

                callback.onSuccess(handovers);

            } catch (Exception e) {
                Log.e(TAG, "Error finding handovers by flight id", e);
                callback.onError("Lỗi khi tìm bàn giao theo chuyến bay: " + e.getMessage());
            }
        });
    }

    public void findByUserId(String userId, ListCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Handover> handovers = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        or(eq("createdByUserId", userId), eq("receivedByUserId", userId))
                ).sort(new Document("creationTimestamp", -1)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Handover handover = fromDocument(doc);
                        handovers.add(handover);
                    }
                }

                callback.onSuccess(handovers);

            } catch (Exception e) {
                Log.e(TAG, "Error finding handovers by user id", e);
                callback.onError("Lỗi khi tìm bàn giao theo người dùng: " + e.getMessage());
            }
        });
    }

    public void findByStatus(String status, ListCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Handover> handovers = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(eq("status", status))
                        .sort(new Document("creationTimestamp", -1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Handover handover = fromDocument(doc);
                        handovers.add(handover);
                    }
                }

                callback.onSuccess(handovers);

            } catch (Exception e) {
                Log.e(TAG, "Error finding handovers by status", e);
                callback.onError("Lỗi khi tìm bàn giao theo trạng thái: " + e.getMessage());
            }
        });
    }

    public void findByDateRange(Date startDate, Date endDate, ListCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Handover> handovers = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        and(
                                gte("creationTimestamp", startDate),
                                lte("creationTimestamp", endDate)
                        )
                ).sort(new Document("creationTimestamp", -1)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Handover handover = fromDocument(doc);
                        handovers.add(handover);
                    }
                }

                callback.onSuccess(handovers);

            } catch (Exception e) {
                Log.e(TAG, "Error finding handovers by date range", e);
                callback.onError("Lỗi khi tìm bàn giao theo ngày: " + e.getMessage());
            }
        });
    }

    public void findPendingApprovals(String userId, String userRole, ListCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Handover> handovers = new ArrayList<>();

                // Different pending statuses based on user role
                Document filter;
                if ("FlightAttendant".equals(userRole)) {
                    // FA can see handovers pending their approval
                    filter = new Document("status", "PENDING_FA_APPROVAL");
                } else if ("InflightServicesStaff".equals(userRole)) {
                    // Staff can see handovers pending their approval for returns
                    filter = new Document("status", "PENDING_STAFF_APPROVAL_RETURN");
                } else {
                    // Admin can see all pending
                    filter = new Document("status",
                            new Document("$in", List.of("PENDING_FA_APPROVAL", "PENDING_STAFF_APPROVAL_RETURN")));
                }

                try (MongoCursor<Document> cursor = collection.find(filter)
                        .sort(new Document("creationTimestamp", 1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Handover handover = fromDocument(doc);
                        handovers.add(handover);
                    }
                }

                callback.onSuccess(handovers);

            } catch (Exception e) {
                Log.e(TAG, "Error finding pending approvals", e);
                callback.onError("Lỗi khi tìm bàn giao chờ duyệt: " + e.getMessage());
            }
        });
    }

    @Override
    public ValidationResult validate(Handover handover) {
        if (handover == null) {
            ValidationResult result = new ValidationResult();
            result.addError("Thông tin bàn giao không được để trống");
            return result;
        }
        return handover.validateForSubmit();
    }

    @Override
    public Document toDocument(Handover handover) {
        Document doc = new Document();

        if (handover.get_id() != null && !handover.get_id().isEmpty()) {
            doc.append("_id", new ObjectId(handover.get_id()));
        }

        doc.append("flightId", handover.getFlightId())
                .append("flightNumberDisplay", handover.getFlightNumberDisplay())
                .append("aircraftNumberDisplay", handover.getAircraftNumberDisplay())
                .append("flightDateDisplay", handover.getFlightDateDisplay())
                .append("createdByUserId", handover.getCreatedByUserId())
                .append("createdByUserNameDisplay", handover.getCreatedByUserNameDisplay())
                .append("receivedByUserId", handover.getReceivedByUserId())
                .append("receivedByUserNameDisplay", handover.getReceivedByUserNameDisplay())
                .append("creationTimestamp", handover.getCreationTimestamp())
                .append("faConfirmationTimestamp", handover.getFaConfirmationTimestamp())
                .append("faReturnTimestamp", handover.getFaReturnTimestamp())
                .append("staffConfirmationReturnTimestamp", handover.getStaffConfirmationReturnTimestamp())
                .append("lastUpdatedTimestamp", handover.getLastUpdatedTimestamp())
                .append("status", handover.getStatus())
                .append("handoverType", handover.getHandoverType())
                .append("isLocked", handover.isLocked())
                .append("notes", handover.getNotes())
                .append("handoverCode", handover.getHandoverCode())
                .append("totalValue", handover.getTotalValue());

        // Convert HandoverItems to Documents
        if (handover.getItems() != null && !handover.getItems().isEmpty()) {
            List<Document> itemDocs = new ArrayList<>();
            for (HandoverItem item : handover.getItems()) {
                itemDocs.add(handoverItemToDocument(item));
            }
            doc.append("items", itemDocs);
        }

        return doc;
    }

    @Override
    public Handover fromDocument(Document doc) {
        Handover handover = new Handover();

        if (doc.getObjectId("_id") != null) {
            handover.set_id(doc.getObjectId("_id").toString());
        }

        handover.setFlightId(doc.getString("flightId"));
        handover.setFlightNumberDisplay(doc.getString("flightNumberDisplay"));
        handover.setAircraftNumberDisplay(doc.getString("aircraftNumberDisplay"));
        handover.setFlightDateDisplay(doc.getDate("flightDateDisplay"));
        handover.setCreatedByUserId(doc.getString("createdByUserId"));
        handover.setCreatedByUserNameDisplay(doc.getString("createdByUserNameDisplay"));
        handover.setReceivedByUserId(doc.getString("receivedByUserId"));
        handover.setReceivedByUserNameDisplay(doc.getString("receivedByUserNameDisplay"));
        handover.setCreationTimestamp(doc.getDate("creationTimestamp"));
        handover.setFaConfirmationTimestamp(doc.getDate("faConfirmationTimestamp"));
        handover.setFaReturnTimestamp(doc.getDate("faReturnTimestamp"));
        handover.setStaffConfirmationReturnTimestamp(doc.getDate("staffConfirmationReturnTimestamp"));
        handover.setLastUpdatedTimestamp(doc.getDate("lastUpdatedTimestamp"));
        handover.setStatus(doc.getString("status"));
        handover.setHandoverType(doc.getString("handoverType"));
        handover.setLocked(doc.getBoolean("isLocked", false));
        handover.setNotes(doc.getString("notes"));
        handover.setHandoverCode(doc.getString("handoverCode"));
        handover.setTotalValue(doc.getDouble("totalValue") != null ? doc.getDouble("totalValue") : 0.0);

        // Convert Documents to HandoverItems
        @SuppressWarnings("unchecked")
        List<Document> itemDocs = (List<Document>) doc.get("items");
        if (itemDocs != null) {
            List<HandoverItem> items = new ArrayList<>();
            for (Document itemDoc : itemDocs) {
                items.add(handoverItemFromDocument(itemDoc));
            }
            handover.setItems(items);
        }

        return handover;
    }

    private Document handoverItemToDocument(HandoverItem item) {
        Document doc = new Document();

        doc.append("productId", item.getProductId())
                .append("productName", item.getProductName())
                .append("productImageUrl", item.getProductImageUrl())
                .append("unitPrice", item.getUnitPrice())
                .append("initialQuantityFromStaff", item.getInitialQuantityFromStaff())
                .append("actualReceivedByFAQuantity", item.getActualReceivedByFAQuantity())
                .append("soldQuantityByFA", item.getSoldQuantityByFA())
                .append("cancelledQuantityByFA", item.getCancelledQuantityByFA())
                .append("actualReturnedToStaffQuantity", item.getActualReturnedToStaffQuantity())
                .append("notes", item.getNotes())
                .append("category", item.getCategory());

        return doc;
    }

    private HandoverItem handoverItemFromDocument(Document doc) {
        HandoverItem item = new HandoverItem();

        item.setProductId(doc.getString("productId"));
        item.setProductName(doc.getString("productName"));
        item.setProductImageUrl(doc.getString("productImageUrl"));
        item.setUnitPrice(doc.getDouble("unitPrice") != null ? doc.getDouble("unitPrice") : 0.0);
        item.setInitialQuantityFromStaff(doc.getInteger("initialQuantityFromStaff", 0));
        item.setActualReceivedByFAQuantity(doc.getInteger("actualReceivedByFAQuantity", 0));
        item.setSoldQuantityByFA(doc.getInteger("soldQuantityByFA", 0));
        item.setCancelledQuantityByFA(doc.getInteger("cancelledQuantityByFA", 0));
        item.setActualReturnedToStaffQuantity(doc.getInteger("actualReturnedToStaffQuantity", 0));
        item.setNotes(doc.getString("notes"));
        item.setCategory(doc.getString("category"));

        return item;
    }
    // Add this method to HandoverRepository class
    public void findByHandoverCode(String handoverCode, OperationCallback<Handover> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getHandoversCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                Document doc = collection.find(eq("handoverCode", handoverCode)).first();

                if (doc != null) {
                    Handover handover = fromDocument(doc);
                    callback.onSuccess(handover);
                } else {
                    callback.onError("Không tìm thấy bàn giao với mã: " + handoverCode);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error finding handover by code", e);
                callback.onError("Lỗi khi tìm bàn giao: " + e.getMessage());
            }
        });
    }
}