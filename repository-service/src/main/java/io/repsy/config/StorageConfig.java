package io.repsy.config;

import io.repsy.impl.FileSystemStorageService;
import io.repsy.service.ObjectStorageService;
import io.repsy.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Value("${repsy.storage.type}")
    private String storageType;

    @Bean
    public StorageService storageService(){
        System.out.println(">>> Selected Storage Strategy: " + storageType);
        if ("object".equalsIgnoreCase(storageType)){
            System.out.println(">>> [CONFIG] USING ObjectStorageService");
            return new ObjectStorageService();
        } else {
            System.out.println(">>> [CONFIG] USING FileSystemStorageService");
            return new FileSystemStorageService();
        }
    }
}
