package com.gruposuporte.projetosuporte.services;

import org.apache.tomcat.util.http.fileupload.UploadContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class CallServiceImpl implements CallService {

    @Override
    public void saveImageCall(String userName, MultipartFile multipartFile) {
        try {
            Path uploadPath = Paths.get("uploads/" + userName);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(multipartFile.getOriginalFilename());

            Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resource load(String userName, String fileName) {
        try {
            Path uploadPath = Paths.get("uploads/" + userName);
            Path file = uploadPath.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Não foi possivel encontrar o arquivo");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
