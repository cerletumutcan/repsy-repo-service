package io.repsy.repository;

import io.repsy.entity.PackageMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageMetadataRepository extends JpaRepository<PackageMetadata, Long> {
    boolean existsByPackageNameAndVersion(String packageName, String version);
}
