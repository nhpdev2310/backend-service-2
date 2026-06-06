package com.nhpdev.backendservicesecond.controller;

import com.nhpdev.backendservicesecond.constraint.AppConstants;
import com.nhpdev.backendservicesecond.dto.response.ApiResponse;
import com.nhpdev.backendservicesecond.service.MediaService;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(AppConstants.URL_PREFIX + "/medias")
@Slf4j(topic = "MEDIA_CONTROLLER")
public class MediaController {
    public final MediaService mediaService;

    @PostMapping
    public ApiResponse<String> uploadMedia(@RequestParam("file")MultipartFile file) throws IOException {
        var data = mediaService.uploadMedia(file, "avatar");
        return ApiResponse.success(data);
    }
}
