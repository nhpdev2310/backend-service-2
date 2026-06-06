package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.exception.BackendServiceException;
import com.nhpdev.backendservicesecond.exception.ErrorCode;
import com.nhpdev.backendservicesecond.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "MEDIA_SERVICE")
public class MediaServiceImpl implements MediaService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Override
    public String uploadMedia(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename(); //abc.png
        String key = generateKey(originalFilename, folder);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(file.getContentType())
                .key(key)
                .build();
        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, requestBody);
        boolean successful = putObjectResponse.sdkHttpResponse().isSuccessful();
        if(!successful) {
            throw new BackendServiceException(ErrorCode.UPLOAD_FILE_FAILED);
        }
        return key;
    }

    @Override
    public String generatePresignedUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(req -> req
                        .bucket(bucketName)
                        .key(key)
                )
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    private String generateKey(String fileName, String folder) {
        if (!StringUtils.hasText(fileName)) {
            return UUID.randomUUID().toString();
        }
        int dotIndex = fileName.lastIndexOf(".");
        return folder + "/" +
                fileName.substring(0, dotIndex) + "-" +
                System.currentTimeMillis() +
                fileName.substring(dotIndex);
    }
}
