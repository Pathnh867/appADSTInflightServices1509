// src/main/java/com/example/vietflightinventory/repositories/ProductRepository.java
package com.example.vietflightinventory.repositories;

import android.util.Log;
import com.example.vietflightinventory.helpers.MongoDBHelper;
import com.example.vietflightinventory.models.Product;
import com.example.vietflightinventory.models.ValidationResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.*;

public class ProductRepository implements BaseRepository<Product>, DocumentConverter<Product> {

    private static final String TAG = "ProductRepository";
    private static ProductRepository instance;

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    private ProductRepository() {}

    @Override
    public void insert(Product product, OperationCallback<Product> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                ValidationResult validation = validate(product);
                if (!validation.isValid()) {
                    callback.onError(validation.getFirstError());
                    return;
                }

                MongoCollection<Document> collection = MongoDBHelper.getProductsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                // Check if product name already exists in same category
                Document existingProduct = collection.find(
                        and(eq("name", product.getName()), eq("category", product.getCategory()))
                ).first();

                if (existingProduct != null) {
                    callback.onError("Sản phẩm đã tồn tại trong danh mục này");
                    return;
                }

                Document doc = toDocument(product);
                collection.insertOne(doc);

                product.set_id(doc.getObjectId("_id").toString());
                callback.onSuccess(product);

            } catch (Exception e) {
                Log.e(TAG, "Error inserting product", e);
                callback.onError("Lỗi khi tạo sản phẩm: " + e.getMessage());
            }
        });
    }

    @Override
    public void update(Product product, BooleanCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                ValidationResult validation = validate(product);
                if (!validation.isValid()) {
                    callback.onError(validation.getFirstError());
                    return;
                }

                MongoCollection<Document> collection = MongoDBHelper.getProductsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(product.get_id());
                Document doc = toDocument(product);
                doc.remove("_id");

                long modifiedCount = collection.updateOne(
                        eq("_id", objectId),
                        new Document("$set", doc)
                ).getModifiedCount();

                callback.onSuccess(modifiedCount > 0);

            } catch (Exception e) {
                Log.e(TAG, "Error updating product", e);
                callback.onError("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            }
        });
    }

    @Override
    public void delete(String id, BooleanCallback callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getProductsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                // Soft delete - mark as inactive instead of actual deletion
                ObjectId objectId = new ObjectId(id);
                long modifiedCount = collection.updateOne(
                        eq("_id", objectId),
                        new Document("$set", new Document("isActive", false))
                ).getModifiedCount();

                callback.onSuccess(modifiedCount > 0);

            } catch (Exception e) {
                Log.e(TAG, "Error deleting product", e);
                callback.onError("Lỗi khi xóa sản phẩm: " + e.getMessage());
            }
        });
    }

    @Override
    public void findById(String id, OperationCallback<Product> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getProductsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                ObjectId objectId = new ObjectId(id);
                Document doc = collection.find(eq("_id", objectId)).first();

                if (doc != null) {
                    Product product = fromDocument(doc);
                    callback.onSuccess(product);
                } else {
                    callback.onError("Không tìm thấy sản phẩm");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error finding product by id", e);
                callback.onError("Lỗi khi tìm sản phẩm: " + e.getMessage());
            }
        });
    }

    @Override
    public void findAll(ListCallback<Product> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getProductsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Product> products = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(eq("isActive", true))
                        .sort(new Document("category", 1).append("name", 1))
                        .iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Product product = fromDocument(doc);
                        products.add(product);
                    }
                }

                callback.onSuccess(products);

            } catch (Exception e) {
                Log.e(TAG, "Error finding all products", e);
                callback.onError("Lỗi khi tải danh sách sản phẩm: " + e.getMessage());
            }
        });
    }

    // Custom methods for Product
    public void findByCategory(String category, ListCallback<Product> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getProductsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Product> products = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        and(eq("category", category), eq("isActive", true))
                ).sort(new Document("name", 1)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Product product = fromDocument(doc);
                        products.add(product);
                    }
                }

                callback.onSuccess(products);

            } catch (Exception e) {
                Log.e(TAG, "Error finding products by category", e);
                callback.onError("Lỗi khi tìm sản phẩm theo danh mục: " + e.getMessage());
            }
        });
    }

    public void searchByName(String searchTerm, ListCallback<Product> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getProductsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Product> products = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        and(
                                regex("name", ".*" + searchTerm + ".*", "i"),
                                eq("isActive", true)
                        )
                ).sort(new Document("name", 1)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Product product = fromDocument(doc);
                        products.add(product);
                    }
                }

                callback.onSuccess(products);

            } catch (Exception e) {
                Log.e(TAG, "Error searching products by name", e);
                callback.onError("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage());
            }
        });
    }

    public void findByPriceRange(double minPrice, double maxPrice, ListCallback<Product> callback) {
        MongoDBHelper.executeInBackground(() -> {
            try {
                MongoCollection<Document> collection = MongoDBHelper.getProductsCollection();
                if (collection == null) {
                    callback.onError("Không thể kết nối database");
                    return;
                }

                List<Product> products = new ArrayList<>();
                try (MongoCursor<Document> cursor = collection.find(
                        and(
                                gte("price", minPrice),
                                lte("price", maxPrice),
                                eq("isActive", true)
                        )
                ).sort(new Document("price", 1)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Product product = fromDocument(doc);
                        products.add(product);
                    }
                }

                callback.onSuccess(products);

            } catch (Exception e) {
                Log.e(TAG, "Error finding products by price range", e);
                callback.onError("Lỗi khi tìm sản phẩm theo giá: " + e.getMessage());
            }
        });
    }

    @Override
    public ValidationResult validate(Product product) {
        if (product == null) {
            ValidationResult result = new ValidationResult();
            result.addError("Thông tin sản phẩm không được để trống");
            return result;
        }
        return product.validateForSave();
    }

    @Override
    public Document toDocument(Product product) {
        Document doc = new Document();

        if (product.get_id() != null && !product.get_id().isEmpty()) {
            doc.append("_id", new ObjectId(product.get_id()));
        }

        doc.append("name", product.getName())
                .append("category", product.getCategory())
                .append("price", product.getPrice())
                .append("description", product.getDescription())
                .append("sku", product.getSku())
                .append("isActive", product.isActive());

        if (product.getImageUrl() != null) {
            doc.append("imageUrl", product.getImageUrl());
        }

        if (product.getImageResId() != 0) {
            doc.append("imageResId", product.getImageResId());
        }

        return doc;
    }

    @Override
    public Product fromDocument(Document doc) {
        Product product = new Product();

        if (doc.getObjectId("_id") != null) {
            product.set_id(doc.getObjectId("_id").toString());
        }

        product.setName(doc.getString("name"));
        product.setCategory(doc.getString("category"));
        product.setPrice(doc.getDouble("price") != null ? doc.getDouble("price") : 0.0);
        product.setDescription(doc.getString("description"));
        product.setSku(doc.getString("sku"));
        product.setActive(doc.getBoolean("isActive", true));
        product.setImageUrl(doc.getString("imageUrl"));
        product.setImageResId(doc.getInteger("imageResId", 0));

        return product;
    }
}