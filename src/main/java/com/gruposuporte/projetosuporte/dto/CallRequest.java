package com.gruposuporte.projetosuporte.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public record CallRequest(
        String title,
        String description,
        MultipartFile multipartFile

) {
}
