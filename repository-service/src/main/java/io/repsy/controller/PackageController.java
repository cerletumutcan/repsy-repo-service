package io.repsy.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.repsy.entity.PackageMetadata;
import io.repsy.repository.PackageMetadataRepository;
import io.repsy.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

@RestController
@RequestMapping
public class PackageController {

    private final StorageService storageService;
    private final PackageMetadataRepository packageMetadataRepository;

    @Value("${repsy.storage.type}")
    private String storageType;

    public PackageController(StorageService storageService, PackageMetadataRepository packageMetadataRepository){
        this.storageService = storageService;
        this.packageMetadataRepository = packageMetadataRepository;
    }

    @PostMapping("/deploy")
    public ResponseEntity<String> uploadPackage
            (@RequestParam("packageName") String packageName,
             @RequestParam("version") String version,
             @RequestParam("file") MultipartFile file){

        try (InputStream inputStream = file.getInputStream()){
            String fileName = file.getOriginalFilename();

            // Dosya ismi kontrolü
            if (fileName == null || !fileName.equals("meta.json") && !fileName.endsWith(".rep")){
                return ResponseEntity
                        .badRequest()
                        .body("Invalid file type. Only 'meta.json' or '.rep' files are allowed.");
            }

            // Eğer dosya meta.json ise JSON doğrulaması yapıyorum
            if ("meta.json".equals(fileName)){
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(file.getBytes());

                    // 'name' ve 'version' alanlarının varlığını kontrol ediyorum
                    if (!jsonNode.hasNonNull("name") || !jsonNode.hasNonNull("version")){
                        return ResponseEntity
                                .badRequest()
                                .body("Invalid meta.json: 'name' and 'version' fields are required.");
                    }
                } catch (Exception e) {
                    return ResponseEntity
                            .badRequest()
                            .body("Invalid meta.json: Malformed JSON structure.");
                }
            }

            // Aynı packageName ve version var mı diye kontrol ediyorum
            boolean exists = packageMetadataRepository.existsByPackageNameAndVersion(packageName, version);
            if (exists){
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Package with the same name and version already exists.");
            }

            System.out.println(">>> [CONTROLLER] saving file: " + fileName);
            storageService.save(packageName, version, fileName, inputStream);

            PackageMetadata metadata = new PackageMetadata(
                    null,
                    packageName,
                    version,
                    fileName,
                    storageType,
                    LocalDateTime.now()
            );
            packageMetadataRepository.save(metadata);
            System.out.println(">>> [CONTROLLER] storageService class: " + storageService.getClass().getName());

            // Upload işlemi başarılı olunca 201 Created dönüyorum.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Package uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/{packageName}/{version}/{fileName}")
    public ResponseEntity<InputStreamResource> downloadPackage
            (@PathVariable("packageName") String packageName,
             @PathVariable("version") String version,
             @PathVariable("fileName") String fileName){

        try {
            InputStream stream = storageService.load(packageName, version, fileName);

            if (stream == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
