package com.gruposuporte.projetosuporte.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface CallService {
    void saveImageCall(String userName, MultipartFile multipartFile);

    Resource load(String userName, String fileName);
}
