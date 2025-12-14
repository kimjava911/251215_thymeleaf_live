package kr.java.thymeleaf.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String store(MultipartFile file);

    void delete(String key);

    String getUrl(String key);
}
