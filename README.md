# Repsy Repository Service 
- This project provides a minimal package management system for the Repsy Programming Language.
- The goal is to upload .rep files and related meta.json files to a server and make them available for download.

## Technologies Used
- Java 24
- Spring Boot 3.4
- PostgreSQL 16
- MinIO (Object Storage)
- Docker & Docker Compose
- Maven (Multi-module structure)
- Hibernate (JPA)
- HikariCP (Connection Pooling)

## Installation and Operation

### 1- Requirements
- Docker (20+)
- Docker Compose (v2+)
- Java 21 or later (for local build, not required for running on docker)
- Maven 3.8 or later (for local build)

### 2- Installation Steps
1 - Clone the project
- `git clone` [https://github.com/cerletumutcan/repsy-repo-service.git](https://github.com/cerletumutcan/repsy-repo-service.git)
- Access the relevant directory with `cd repsy-repo-service`. 

2 - Start with Docker Compose
- `docker-compose up -d`

This command:
- PostgreSQL database,
- MinIO object storage service,
- Repository Service starts the application in the background.

### 3 - Services
- PostgreSQL - [localhost:5433](http://localhost:9000)
- MinIO      - [http://localhost:9000](http://localhost:9000)(Admin Panel:[http://localhost:9001](http://localhost:9000))
- Repository Service - [http://localhost:8080](http://localhost:9000)

## API Endpoints

### Deploy Package
- Endpoint: `POST` `/deploy`
- Parameters (multipart/form-data):
     1. [x] packageName - Package Name
     2. [x] version     - Package Version
     3. [x] file        - .rep file or meta.json file
- Prefix cURL:
    `curl -X POST "http://localhost:8080/deploy" \ 
    -F "packageName=mypackage" \
    -F "version=1.0.0" \
    -F "file=@path/to/package.rep`
- And
  `curl -X POST "http://localhost:8080/deploy" \ 
  -F "packageName=mypackage" \
  -F "version=1.0.0" \
  -F "file=@path/to/meta.json`

Only `.rep` and `meta.json` files are accepted. Other files are rejected.

### Download Package
- Endpoint: `GET` `/download/{packageName}/{version}/{fileName}`
- Example cURL:
    `curl -X GET "http://localhost:8080/download/mypackage/1.0.0/package.rep" --output package.rep`
- Or
    `curl -X GET "http://localhost:8080/download/mypackage/1.0.0/meta.json" --output meta.json`

## Storage Type Selection
- The application supports two different storage strategies:
    - File System (file-system)
    - Object Storage (MinIO) (object-storage)

**- To change the storage type:**
- Set the following environment variable via Docker Compose:
    - `repsy.storage.type: file-system`
- Or
    - `repsy.storage.type: object-storage`
- For example:
    - `STORAGE_TYPE=file-system docker-compose up -d`
- Or
    - `STORAGE_TYPE=object-storage docker-compose up -d`

## DockerHub
- The application is published as a ready Docker image.
- To pull the image:
    - `docker pull umutcancerlet/repository-service:latest`
- If you want to run it manually:
    - `docker run -d --name repsy-repo-service \
         -p 8080:8080 \
         --env SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/repsydb \
         --env SPRING_DATASOURCE_USERNAME=repsyuser \
         --env SPRING_DATASOURCE_PASSWORD=123456789 \
         --env repsy.storage.type=file-system \
         umutcancerlet/repository-service:latest`
- However, the recommended use is via Docker Compose.

## Project Structure
1. [x] repsy-repo-service/
   1. [x] `storage-api/`        Common interface for storage strategies
   2. [x] `storage-filesystem/` Storage implementation for file system
   3. [x] `storage-object/`     MinIO object storage implementation
   4. [x] `repository-service/` Spring Boot implementation
   5. [x] `docker-compose.yml`  Compose file that starts the whole system
   6. [x] `README.md`           Project documentation

## Error Management
- `400 Bad Request` - if invalid file is uploaded
- `409 Conflict`    - if same package/version is uploaded again
- `404 Not Found`   - if file to download is not found
- `201 Created`     - Successful upload
- `200 OK`          - Successful download

## Troubleshooting
- If PostgreSQL is not running, check if port 5433 is not being used by another application
- If you get a MinIO access error, check the administration panel via `http://localhost:9001`
- If the repository-service container does not start, verify the database or storage access information in `.env` or `docker-compose.yml`

## Contact
- If you have questions about the project, you can contact us at `cerletumutcan@gmail.com`

## In Summary
- This project is the basic of a complete package management system for the Repsy programming language. It is designed in accordance with modern software development principles such as file upload, file download, strategy selection, management with environment variables, fast installation with Docker.

## Extra Note 
- This project was developed under the Junior FullStack Developer Assignment. It has been optimized according to the time constraints and the scope specified in the document.





























