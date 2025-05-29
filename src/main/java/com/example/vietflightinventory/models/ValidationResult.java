// src/main/java/com/example/vietflightinventory/models/ValidationResult.java
package com.example.vietflightinventory.models;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private List<String> errors;

    public ValidationResult() {
        this.errors = new ArrayList<>();
    }

    public void addError(String error) {
        if (error != null && !error.trim().isEmpty()) {
            errors.add(error);
        }
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public String getFirstError() {
        return errors.isEmpty() ? "" : errors.get(0);
    }

    public String getAllErrors() {
        if (errors.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            sb.append(errors.get(i));
            if (i < errors.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public int getErrorCount() {
        return errors.size();
    }

    public void clear() {
        errors.clear();
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + isValid() +
                ", errorCount=" + getErrorCount() +
                ", errors=" + errors +
                '}';
    }
}