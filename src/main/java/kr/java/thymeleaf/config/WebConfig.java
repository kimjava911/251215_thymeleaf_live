package kr.java.thymeleaf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // org.springframework.beans.factory.annotation.Value;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.storage.type}")
    private String storageType;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (storageType.equals("local")) {
            String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();

            registry.addResourceHandler("/files/**")  // URL 패턴
                    .addResourceLocations("file:" + absolutePath + "/")  // 실제 디렉토리
                    .setCachePeriod(3600);
        }
    }
}
