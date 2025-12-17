package com.vsa.monolith.common.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface StorageService {
    String upload(MultipartFile file, String folder);
    String upload(InputStream inputStream, String fileName, String folder);
    void delete(String fileUrl);
    // Future: byte[] download(String fileUrl);
}
