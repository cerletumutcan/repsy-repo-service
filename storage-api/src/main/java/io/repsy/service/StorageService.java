package io.repsy.service;

import java.io.InputStream;

public interface StorageService {
    void save(String packageName, String version, String fileName, InputStream data);
    InputStream load(String packageName, String version, String fileName);
}
