package net.rewerk.webstore.uploads.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.exception.CloudFileNotFound;
import net.rewerk.exception.InvalidUploadTypeException;
import net.rewerk.webstore.dto.entity.UploadDto;
import net.rewerk.webstore.dto.entity.UserDto;
import net.rewerk.webstore.dto.mq.uploads.UploadsDeleteObjectsDto;
import net.rewerk.webstore.dto.request.upload.UploadSignUrlDto;
import net.rewerk.webstore.dto.response.upload.UploadSignUrlResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Upload service implementation
 *
 * @author rewerk
 */

@Service
@Slf4j
public class UploadService {
    @Value("${uploads.product_images_bucket_name}")
    private String productImagesBucketName;
    @Value("${uploads.brand_images_bucket_name}")
    private String brandImagesBucketName;
    @Value("${uploads.review_images_bucket_name}")
    private String reviewImagesBucketName;
    @Value("${uploads.project_id}")
    private String projectId;
    private final Storage storage;

    /**
     * Constructor
     * Initialize Google Cloud Storage by credentials
     *
     * @throws RuntimeException Failed to read credentials file
     */

    public UploadService() throws RuntimeException {
        log.info("Initializing Upload Service...");
        try {
            log.info("Authenticating in GCS with credentials...");
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(
                    new ClassPathResource("gcs-credentials.json").getInputStream()
            );
            log.info("Authenticated in GCS successfully!");
            log.info("Building GCS storage...");
            storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build()
                    .getService();
            log.info("GCS storage built successfully!");
        } catch (IOException e) {
            log.error("Failed to authenticate or build GCS storage", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sign upload URL
     *
     * @param signUrlDto DTO with sign parameters
     * @param user       Authenticated user
     * @return Response DTO with signed URL
     */

    public UploadSignUrlResponseDto signUploadURL(@NotNull UploadSignUrlDto signUrlDto, @NotNull UserDto user) {
        log.info("UploadService.signUploadURL: sign url, dto = {}, user = {}", signUrlDto, user);
        this.checkPermissions(user, signUrlDto.getType());
        String filename = String.format("%s_%s", UUID.randomUUID(), signUrlDto.getFilename());
        String url = this.signV4UploadURL(
                filename,
                signUrlDto.getMime(),
                signUrlDto.getType()
        );
        return UploadSignUrlResponseDto.builder()
                .uploadURL(url)
                .publicURL(this.getUploadPublicURL(filename, signUrlDto.getType()))
                .build();
    }

    /**
     * Delete uploaded object from Google Cloud Storage bucket
     *
     * @param objectName Name of the object to delete
     * @param uploadType Type of the object to delete
     * @throws CloudFileNotFound Cloud file not found
     */

    public void deleteObject(@NonNull String objectName,
                             @NonNull UploadDto.Type uploadType
    ) throws CloudFileNotFound {
        log.info("UploadService.deleteObject: objectName = {}, uploadType = {}", objectName, uploadType);
        if (objectName.startsWith("https://")) {
            objectName = objectName.substring(objectName.lastIndexOf("/") + 1);
        }
        Blob blob = storage.get(getBucketName(uploadType), objectName);
        if (blob != null) {
            BlobId blobId = blob.getBlobId();
            storage.delete(blobId);
        } else {
            log.error("UploadService.deleteObject: Failed to delete object");
            throw new CloudFileNotFound("Could not find object " + objectName);
        }
    }

    /**
     * Multiple delete objects from Google Cloud Storage bucket by list of names
     *
     * @param objectNames List of object names to delete
     * @param uploadType  Type of object to delete
     * @return CompletableFuture
     * @throws InterruptedException Thread execution interrupted
     */

    @Async
    public CompletableFuture<Void> deleteObjects(@NonNull List<String> objectNames,
                                                 @NonNull UploadDto.Type uploadType) throws InterruptedException {
        log.info("UploadService.deleteObjects: objectNames = {}, uploadType = {}", objectNames, uploadType);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String objectName : objectNames) {
            futures.add(CompletableFuture.runAsync(() -> deleteObject(objectName, uploadType)));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * Multiple delete objects from Google Cloud Storage bucket by delete objects request DTO
     *
     * @param dto DTO with information about objects to delete
     * @throws InterruptedException Thread execution interrupted
     */

    public void deleteObjects(@NonNull UploadsDeleteObjectsDto dto) throws InterruptedException {
        log.info("UploadService.deleteObjects: dto = {}", dto);
        this.deleteObjects(dto.getObject_names(), dto.getType());
    }

    /**
     * Get public object URL by object name and type
     *
     * @param objectName Object name
     * @param uploadType Object type
     * @return Public URL
     */

    private String getUploadPublicURL(String objectName, UploadDto.Type uploadType) {
        log.info("UploadService.getUploadPublicURL: objectName = {}, uploadType = {}", objectName, uploadType);
        return "https://%s.storage.googleapis.com/%s".formatted(getBucketName(uploadType), objectName);
    }

    /**
     * Get bucket name by upload type
     *
     * @param uploadType Upload object type
     * @return Bucket name
     * @throws InvalidUploadTypeException Invalid upload type
     */

    private String getBucketName(@NonNull UploadDto.Type uploadType) throws InvalidUploadTypeException {
        log.info("UploadService.getBucketName: uploadType = {}", uploadType);
        if (UploadDto.Type.PRODUCT_IMAGE.equals(uploadType)) {
            return productImagesBucketName;
        } else if (UploadDto.Type.BRAND_IMAGE.equals(uploadType)) {
            return brandImagesBucketName;
        } else if (UploadDto.Type.REVIEW_IMAGE.equals(uploadType)) {
            return reviewImagesBucketName;
        } else throw new InvalidUploadTypeException("Invalid upload type");
    }

    /**
     * Sign upload URL by V4 method
     *
     * @param objectName Object name
     * @param mime       Object mime type
     * @param uploadType Object upload type
     * @return Signed URL
     */

    private String signV4UploadURL(@NonNull String objectName,
                                   @NonNull String mime,
                                   @NonNull UploadDto.Type uploadType
    ) {
        log.info("UploadService.signV4UploadURL: objectName = {}, uploadType = {}", objectName, uploadType);
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(getBucketName(uploadType), objectName)).build();
        Map<String, String> extHeaders = new HashMap<>();
        extHeaders.put("Content-Type", mime);
        URL url = storage.signUrl(
                blobInfo,
                15,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withExtHeaders(extHeaders),
                Storage.SignUrlOption.withV4Signature()
        );

        return url.toString();
    }

    /**
     * Check permission by user and upload type
     *
     * @param user       Authenticated user
     * @param uploadType Object upload type
     * @throws InvalidUploadTypeException Upload type not allowed
     */

    private void checkPermissions(@NonNull UserDto user, @NonNull UploadDto.Type uploadType)
            throws InvalidUploadTypeException {
        log.info("UploadService.checkPermissions: user = {}, uploadType = {}", user, uploadType);
        if (!UserDto.Role.ROLE_ADMIN.equals(user.getRole()) &&
                !UploadDto.Type.REVIEW_IMAGE.equals(uploadType)) {
            throw new InvalidUploadTypeException("Upload type not allowed");
        }
    }
}
