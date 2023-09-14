package com.gruposuporte.projetosuporte.utils;

import com.gruposuporte.projetosuporte.dto.CallRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.thymeleaf.util.StringUtils;


@Component
public class CreateCallValidator implements Validator {

//    private final CallRepository callRepository;
//
//    @Autowired
//    public CreateCallValidator(CallRepository callRepository) {
//        this.callRepository = callRepository;
//    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CallRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CallRequest callRequest = (CallRequest) target;

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
}
