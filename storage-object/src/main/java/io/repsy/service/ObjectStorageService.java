package io.repsy.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.FileInputStream;
import java.io.InputStream;

public class ObjectStorageService implements StorageService{

    private final MinioClient minioClient;

    public ObjectStorageService() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    @Override
    public void save(String packageName, String version, String fileName, InputStream data) {
        try {
            String objectName = packageName + "/" + version + "/" + fileName;
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("repsy")
                            .object(objectName)
                            .stream(data, -1 , 10485760)
                            .contentType("application/octet-stream")
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("MinIO dosya kaydetme hatası",e);
        }
    }

    @Override
    public InputStream load(String packageName, String version, String fileName) {
        try {
            String objectName = packageName + "/" + version + "/" + fileName;
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("repsy")
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("MinIO dosya yükleme hatası", e);
        }
    }
}
