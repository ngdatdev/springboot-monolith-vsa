package com.vsa.ecommerce.common.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Storage service interface for file management.
 */
public interface StorageService {

    /**
     * Upload file to cloud storage.
     * 
     * @param file   File to upload
     * @param folder Folder path in cloud storage
     * @return Upload result with file URLs and metadata
     * @throws IOException if upload fails
     */
    UploadResult uploadFile(MultipartFile file, String folder) throws IOException;

    /**
     * Upload file with default folder.
     * 
     * @param file File to upload
     * @return Upload result
     * @throws IOException if upload fails
     */
    UploadResult uploadFile(MultipartFile file) throws IOException;

    /**
     * Delete file from cloud storage.
     * 
     * @param publicId Public ID of the file to delete
     * @throws IOException if deletion fails
     */
    void deleteFile(String publicId) throws IOException;

    /**
     * Get file URL by public ID.
     * 
     * @param publicId Public ID of the file
     * @return Secure URL of the file
     */
    String getFileUrl(String publicId);
}
