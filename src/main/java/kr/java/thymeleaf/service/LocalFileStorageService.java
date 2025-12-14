package kr.java.thymeleaf.service;

import kr.java.thymeleaf.exception.FileStorageException;
import kr.java.thymeleaf.exception.InvalidFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    // java.nio.file.Path;
    private final Path uploadPath;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    // org.springframework.beans.factory.annotation.Value
    public LocalFileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new FileStorageException("업로드 디렉토리 생성 실패");
        }
    }

    @Override
    public String store(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path targetPath = this.uploadPath.resolve(storedFilename);
            file.transferTo(targetPath);
            return storedFilename;  // 키 = 파일명
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 실패: " + e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) return;
        try {
            Path filePath = this.uploadPath.resolve(key);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getUrl(String key) {
        return "/files/" + key;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("빈 파일입니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException("허용되지 않는 파일 형식입니다.");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
