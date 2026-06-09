package com.nhpdev.backendservicesecond.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AWSConfiguration {

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.region}")
    private String regionName;

    @Bean
    S3Client s3Client() {
        Region region = Region.of(this.regionName);
        return S3Client.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    @Bean
    S3Presigner s3Presigner() {
        Region region = Region.of(this.regionName);
        return S3Presigner.builder()
                .region(region)
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    private AwsCredentialsProvider awsCredentialsProvider() {
        if(StringUtils.hasText(this.accessKeyId) && StringUtils.hasText(this.secretKey)) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.builder()
                    .accessKeyId(this.accessKeyId)
                    .secretAccessKey(this.secretKey)
                    .build());
        }
        return DefaultCredentialsProvider.builder().build();
    }
}
