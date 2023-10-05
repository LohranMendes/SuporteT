package com.gruposuporte.projetosuporte.utils;

import com.gruposuporte.projetosuporte.dto.CallRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;


@Component
public class CreateCallValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return CallRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CallRequest callRequest = (CallRequest) target;
        if(callRequest.multipartFile()!=null && !isFileSizeValid(callRequest.multipartFile())){
            errors.rejectValue("file","Size.callForm.image");
        }
        if (StringUtils.isEmptyOrWhitespace(callRequest.title())) {
            errors.rejectValue("title", "NotEmpty.callForm.title");
        }

        if (StringUtils.isEmptyOrWhitespace(callRequest.description())) {
            errors.rejectValue("description", "NotEmpty.callForm.description");
        }

        if (callRequest.title().length() > 300){
            errors.rejectValue("title", "Size.callForm.title");
        }
         if (callRequest.description().length() > 780){
            errors.rejectValue("title", "Size.callForm.title");
        }

    }

    private boolean isFileSizeValid(MultipartFile multipartFile) {
        long maxSize = 1024*1024;
        long fileSize = multipartFile.getSize();

        return fileSize <= maxSize;
    }
}
