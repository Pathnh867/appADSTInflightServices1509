// src/main/java/com/example/vietflightinventory/repositories/DocumentConverter.java
package com.example.vietflightinventory.repositories;

import org.bson.Document;

public interface DocumentConverter<T> {
    Document toDocument(T item);
    T fromDocument(Document document);
}