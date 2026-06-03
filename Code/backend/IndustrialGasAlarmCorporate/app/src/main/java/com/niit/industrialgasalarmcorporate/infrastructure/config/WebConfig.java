package com.niit.industrialgasalarmcorporate.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    private String resolvedUploadPath;

    @PostConstruct
    void init() {
        resolvedUploadPath = Paths.get(uploadPath).toAbsolutePath().normalize().toString();
        log.info("静态资源映射: /uploads/** -> file:{}/", resolvedUploadPath);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + resolvedUploadPath.replace("\\", "/");
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
