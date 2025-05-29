// src/main/java/com/example/vietflightinventory/repositories/BaseRepository.java
package com.example.vietflightinventory.repositories;

import com.example.vietflightinventory.models.ValidationResult;
import java.util.List;

public interface BaseRepository<T> {

    interface OperationCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    interface ListCallback<T> {
        void onSuccess(List<T> results);
        void onError(String error);
    }

    interface BooleanCallback {
        void onSuccess(boolean result);
        void onError(String error);
    }

    // CRUD Operations
    void insert(T item, OperationCallback<T> callback);
    void update(T item, BooleanCallback callback);
    void delete(String id, BooleanCallback callback);
    void findById(String id, OperationCallback<T> callback);
    void findAll(ListCallback<T> callback);

    // Validation
    ValidationResult validate(T item);
}