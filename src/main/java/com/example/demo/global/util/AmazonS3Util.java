/*
package com.example.demo.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.demo.apiPayload.code.exception.GeneralException;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Util {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.path.storeImage}")
    private String storeImagePath;

    @Value("${cloud.aws.s3.path.shortsVideo}")
    private String shortsVideoPath;

    // 최대 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    // 최대 500MB(영상) – 필요시 조정
    private static final long MAX_VIDEO_FILE_SIZE = 500L * 1024 * 1024;

    public String uploadImage(MultipartFile file) {
        validateImage(file);

        String uuid = UUID.randomUUID().toString();
        String original = file.getOriginalFilename();
        String key = storeImagePath + "/" + uuid + "_" + (original == null ? "image" : original);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            amazonS3.putObject(bucket, key, file.getInputStream(), metadata);
            return amazonS3.getUrl(bucket, key).toString();
        } catch (IOException e) {
            log.error("S3 upload failed", e);
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAIL);
        }
    }

    public String uploadShortsVideoFromPath(Path mp4Path, String fileName) {
        try (InputStream in = Files.newInputStream(mp4Path)) {
            long size = Files.size(mp4Path);
            validateVideo(size);
            String uuid = UUID.randomUUID().toString();
            String safeName = (fileName == null || fileName.isBlank()) ? (uuid + ".mp4") : fileName;
            String key = shortsVideoPath + "/" + safeName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(size);
            metadata.setContentType("video/mp4");

            amazonS3.putObject(bucket, key, in, metadata);
            return amazonS3.getUrl(bucket, key).toString();
        } catch (IOException e) {
            log.error("S3 upload failed (video from path)", e);
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAIL);
        }
    }

    public String uploadShortsVideo(InputStream in, long contentLength, String fileName) {
        try {
            validateVideo(contentLength);
            String uuid = UUID.randomUUID().toString();
            String safeName = (fileName == null || fileName.isBlank()) ? (uuid + ".mp4") : fileName;
            String key = shortsVideoPath + "/" + safeName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType("video/mp4");

            amazonS3.putObject(bucket, key, in, metadata);
            return amazonS3.getUrl(bucket, key).toString();
        } catch (Exception e) {
            log.error("S3 upload failed (video inputstream)", e);
            throw new GeneralException(ErrorStatus.FILE_UPLOAD_FAIL);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new GeneralException(ErrorStatus.FILE_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
        }
    }

    private void validateVideo(long size) {
        if (size <= 0) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
        }
        if (size > MAX_VIDEO_FILE_SIZE) {
            throw new GeneralException(ErrorStatus.FILE_TOO_LARGE);
        }
    }
}
*/
