package com.nhpdev.backendservicesecond.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AWSConfiguration {

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-key-id}")
    private String secretKeyId;

    @Value("${aws.region}")
    private String regionName;

    @Bean
    S3Client s3Client() {
        Region region = Region.of(this.regionName);
        AwsBasicCredentials credentials = AwsBasicCredentials.builder()
                .accessKeyId(accessKeyId)
                .secretAccessKey(secretKeyId)
                .build();

        return S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    S3Presigner s3Presigner() {
        Region region = Region.of(this.regionName);
        return S3Presigner.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.builder()
                                .accessKeyId(this.accessKeyId)
                                .secretAccessKey(this.secretKeyId)
                                .build()))
                .build();
    }
}
