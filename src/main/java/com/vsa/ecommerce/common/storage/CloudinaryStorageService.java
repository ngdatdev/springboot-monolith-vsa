package com.vsa.ecommerce.common.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Cloudinary implementation of StorageService.
 * 
 * Features:
 * - Upload images, videos, and raw files
 * - Automatic format detection
 * - Unique file naming
 * - Folder organization
 * - Delete files
 * - Get file URLs
 * 
 * Usage:
 * 
 * <pre>
 * UploadResult result = storageService.uploadFile(file, "products");
 * String imageUrl = result.getSecureUrl();
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryStorageService implements StorageService {

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;

    @Override
    public UploadResult uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, cloudinaryProperties.getDefaultFolder());
    }

    @Override
    public UploadResult uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            // Generate unique public ID
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String publicId = folder + "/" + UUID.randomUUID() + extension;

            // Upload file to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder,
                    "resource_type", "auto", // Auto-detect resource type
                    "overwrite", false));

            log.info("File uploaded to Cloudinary: {}", publicId);

            // Extract and return upload result
            return new UploadResult(
                    (String) uploadResult.get("url"),
                    (String) uploadResult.get("secure_url"),
                    (String) uploadResult.get("public_id"),
                    (String) uploadResult.get("resource_type"),
                    (String) uploadResult.get("format"),
                    ((Number) uploadResult.get("bytes")).longValue(),
                    (Integer) uploadResult.get("width"),
                    (Integer) uploadResult.get("height"));

        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String publicId) throws IOException {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String status = (String) result.get("result");

            if (!"ok".equals(status)) {
                log.warn("Failed to delete file from Cloudinary: {}, status: {}", publicId, status);
                throw new IOException("Failed to delete file: " + status);
            }

            log.info("File deleted from Cloudinary: {}", publicId);

        } catch (IOException e) {
            log.error("Error deleting file from Cloudinary: {}", publicId, e);
            throw new IOException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFileUrl(String publicId) {
        return cloudinary.url().secure(true).generate(publicId);
    }

    /**
     * Upload image with transformation (resize, crop, etc.)
     */
    public UploadResult uploadImage(MultipartFile file, String folder, Integer width, Integer height)
            throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            String publicId = folder + "/" + UUID.randomUUID();

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder,
                    "resource_type", "image",
                    "transformation", ObjectUtils.asMap(
                            "width", width,
                            "height", height,
                            "crop", "limit" // Resize without cropping
                    )));

            log.info("Image uploaded with transformation: {}", publicId);

            return new UploadResult(
                    (String) uploadResult.get("url"),
                    (String) uploadResult.get("secure_url"),
                    (String) uploadResult.get("public_id"),
                    (String) uploadResult.get("resource_type"),
                    (String) uploadResult.get("format"),
                    ((Number) uploadResult.get("bytes")).longValue(),
                    (Integer) uploadResult.get("width"),
                    (Integer) uploadResult.get("height"));

        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new IOException("Failed to upload image: " + e.getMessage(), e);
        }
    }
}
