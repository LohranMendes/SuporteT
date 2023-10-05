package com.gruposuporte.projetosuporte.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDiretory("uploads",registry);
    }

    private void exposeDiretory(String uploads, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(uploads);
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        if(uploads.startsWith("../")){
            uploads = uploads.replace("../","");
        }
        registry.addResourceHandler("/"+uploads+"/**").addResourceLocations("file:/"+uploadPath+"/");
    }
}
