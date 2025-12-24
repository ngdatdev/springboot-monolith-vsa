package com.vsa.ecommerce.common.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Upload result DTO containing file information.
 */
@Getter
@Setter
@AllArgsConstructor
public class UploadResult {

    /**
     * Public URL of uploaded file.
     */
    private String url;

    /**
     * Secure HTTPS URL of uploaded file.
     */
    private String secureUrl;

    /**
     * Public ID used to reference the file in Cloudinary.
     */
    private String publicId;

    /**
     * Resource type (image, video, raw, etc.)
     */
    private String resourceType;

    /**
     * File format/extension (jpg, png, pdf, etc.)
     */
    private String format;

    /**
     * File size in bytes.
     */
    private Long bytes;

    /**
     * Width in pixels (for images).
     */
    private Integer width;

    /**
     * Height in pixels (for images).
     */
    private Integer height;
}
