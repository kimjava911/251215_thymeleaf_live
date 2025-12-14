package kr.java.thymeleaf.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import static org.springframework.http.MediaType.parseMediaType;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
@Slf4j
public class S3FileController {

    private final S3Client s3Client;

    // org.springframework.beans.factory.annotation.Value
    @Value("${aws.s3.bucket}")
    private String bucket;

    @GetMapping("/{filename}")
    // org.springframework.core.io.Resource
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();

        try {
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            GetObjectResponse s3ObjectResponse = s3Object.response();

            InputStreamResource resource = new InputStreamResource(s3Object);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(parseMediaType(s3ObjectResponse.contentType()));
            headers.setContentLength(s3ObjectResponse.contentLength());
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}