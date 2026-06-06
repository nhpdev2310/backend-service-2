package com.nhpdev.backendservicesecond.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MediaService {
    String uploadMedia(MultipartFile file, String folder) throws IOException;
    String generatePresignedUrl(String key);
}
