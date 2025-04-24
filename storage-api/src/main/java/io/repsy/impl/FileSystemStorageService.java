package io.repsy.impl;

import io.repsy.service.StorageService;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileSystemStorageService implements StorageService {

    // Sonradan yapılandırılabilir bir dosya yolu
    private static final String ROOT = "/opt/repsy-files";

    @Override
    public void save(String packageName, String version, String fileName, InputStream data) {
        System.out.println(">>> [FILESYSTEM] save() called");
        try {
            Path packagePath = Paths.get(ROOT, packageName, version);
            File dir = packagePath.toFile();
            if (!dir.exists()){
                dir.mkdirs();
            }
            File file = packagePath.resolve(fileName).toFile();
            System.out.println(">>> [SAVE] Writing file to: " + file.getAbsolutePath());

            try (OutputStream os = new FileOutputStream(file)){
                data.transferTo(os);
            }
        } catch (Exception e) {
            System.out.println(">>> [ERROR] Could not save file: " + e.getMessage());
            throw new RuntimeException("Dosya kaydedilemedi", e);
        }
    }

    @Override
    public InputStream load(String packageName, String version, String fileName) {
        File file = Paths.get(ROOT, packageName, version, fileName).toFile();
        if (!file.exists()){
            throw new RuntimeException("Dosya bulunamadı");
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Dosya yüklenemedi", e);
        }
    }

}
