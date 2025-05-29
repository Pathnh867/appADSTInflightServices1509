// src/main/java/com/example/vietflightinventory/repositories/UserRepository.java
package com.example.vietflightinventory.repositories;

import android.util.Log;
import com.example.vietflightinventory.helpers.MongoDBHelper;
import com.example.vietflightinventory.models.User;
import com.example.vietflightinventory.models.ValidationResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class UserRepository implements BaseRepository<User>, DocumentConverter<User> {

    private static final String TAG = "UserRepository";
    private static UserRepository instance;

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private UserRepository() {}

    @Override
    public void insert(User user, OperationCallback<User> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                ValidationResult validation = validate(user);
                if (!validation.isValid()) {
                    callback.onError(validation.getFirstError());
                    return;
                }

                MongoCollection<Document> collection = MongoDBHelper.getUsersCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                // Check if username already exists
                Document existingUser = collection.find(eq("username", user.getUsername())).first();
                if (existingUser != null) {
                    callback.onError("Tên đăng nhập đã tồn tại");
                    return;
                }

                Document doc = toDocument(user);
                collection.insertOne(doc);

                user.set_id(doc.getObjectId("_id").toString());
                callback.onSuccess(user);

            } catch (Exception e) {
                Log.e(TAG, "Error inserting user", e);
                callback.onError("Lỗi khi tạo người dùng: " + e.getMessage());
            }
        });
    }

    @Override
    public void update(User user, BooleanCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                ValidationResult validation = validate(user);
                if (!validation.isValid()) {
                    callback.onError(validation.getFirstError());
                    return;
                }

                MongoCollection<Document> collection = MongoDBHelper.getUsersCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(user.get_id());
                Document doc = toDocument(user);
                doc.remove("_id"); // Don't update _id field

                long modifiedCount = collection.updateOne(
                        eq("_id", objectId),
                        new Document("$set", doc)
                ).getModifiedCount();

                callback.onSuccess(modifiedCount > 0);

            } catch (Exception e) {
                Log.e(TAG, "Error updating user", e);
                callback.onError("Lỗi khi cập nhật người dùng: " + e.getMessage());
            }
        });
    }

    @Override
    public void delete(String id, BooleanCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getUsersCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(id);
                long deletedCount = collection.deleteOne(eq("_id", objectId)).getDeletedCount();

                callback.onSuccess(deletedCount > 0);

            } catch (Exception e) {
                Log.e(TAG, "Error deleting user", e);
                callback.onError("Lỗi khi xóa người dùng: " + e.getMessage());
            }
        });
    }

    @Override
    public void findById(String id, OperationCallback<User> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getUsersCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(id);
                Document doc = collection.find(eq("_id", objectId)).first();

                if (doc != null) {
                    User user = fromDocument(doc);
                    callback.onSuccess(user);
                } else {
                    callback.onError("Không tìm thấy người dùng");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error finding user by id", e);
                callback.onError("Lỗi khi tìm người dùng: " + e.getMessage());
            }
        });
    }

    @Override
    public void findAll(ListCallback<User> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getUsersCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<User> users = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find().iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        User user = fromDocument(doc);
                        users.add(user);
                    }
                }

                callback.onSuccess(users);

            } catch (Exception e) {
                Log.e(TAG, "Error finding all users", e);
                callback.onError("Lỗi khi tải danh sách người dùng: " + e.getMessage());
            }
        });
    }

    // Custom methods for User
    public void findByUsername(String username, OperationCallback<User> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getUsersCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                Document doc = collection.find(eq("username", username)).first();

                if (doc != null) {
                    User user = fromDocument(doc);
                    callback.onSuccess(user);
                } else {
                    callback.onError("Không tìm thấy người dùng");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error finding user by username", e);
                callback.onError("Lỗi khi tìm người dùng: " + e.getMessage());
            }
        });
    }

    public void authenticate(String username, String password, OperationCallback<User> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getUsersCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                Document doc = collection.find(
                        and(eq("username", username), eq("password", password), eq("isActive", true))
                ).first();

                if (doc != null) {
                    User user = fromDocument(doc);
                    callback.onSuccess(user);
                } else {
                    callback.onError("Tên đăng nhập hoặc mật khẩu không đúng");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error authenticating user", e);
                callback.onError("Lỗi khi đăng nhập: " + e.getMessage());
            }
        });
    }

    public void findByRole(String role, ListCallback<User> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getUsersCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<User> users = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        and(eq("role", role), eq("isActive", true))
                ).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        User user = fromDocument(doc);
                        users.add(user);
                    }
                }

                callback.onSuccess(users);

            } catch (Exception e) {
                Log.e(TAG, "Error finding users by role", e);
                callback.onError("Lỗi khi tìm người dùng theo vai trò: " + e.getMessage());
            }
        });
    }

    @Override
    public ValidationResult validate(User user) {
        if (user == null) {
            ValidationResult result = new ValidationResult();
            result.addError("Thông tin người dùng không được để trống");
            return result;
        }
        return user.validateForRegistration();
    }

    @Override
    public Document toDocument(User user) {
        Document doc = new Document();

        if (user.get_id() != null && !user.get_id().isEmpty()) {
            doc.append("_id", new ObjectId(user.get_id()));
        }

        doc.append("username", user.getUsername())
                .append("password", user.getPassword()) // Note: Should be hashed in production
                .append("fullname", user.getFullname())
                .append("role", user.getRole())
                .append("email", user.getEmail())
                .append("phoneNumber", user.getPhoneNumber())
                .append("company", user.getCompany())
                .append("workplace", user.getWorkplace())
                .append("isActive", user.isActive())
                .append("createdAt", user.getCreatedAt() != null ? user.getCreatedAt() : new Date())
                .append("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt() : new Date());

        return doc;
    }

    @Override
    public User fromDocument(Document doc) {
        User user = new User();

        if (doc.getObjectId("_id") != null) {
            user.set_id(doc.getObjectId("_id").toString());
        }

        user.setUsername(doc.getString("username"));
        user.setPassword(doc.getString("password"));
        user.setFullname(doc.getString("fullname"));
        user.setRole(doc.getString("role"));
        user.setEmail(doc.getString("email"));
        user.setPhoneNumber(doc.getString("phoneNumber"));
        user.setCompany(doc.getString("company"));
        user.setWorkplace(doc.getString("workplace"));
        user.setActive(doc.getBoolean("isActive", true));
        user.setCreatedAt(doc.getDate("createdAt"));
        user.setUpdatedAt(doc.getDate("updatedAt"));

        return user;
    }
}