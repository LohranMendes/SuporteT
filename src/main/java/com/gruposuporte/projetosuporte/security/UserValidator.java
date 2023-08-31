package com.gruposuporte.projetosuporte.security;

import com.gruposuporte.projetosuporte.dto.SignupRequest;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {


    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SignupRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        SignupRequest user = (SignupRequest) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.username().length() < 6 || user.username().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        if (userRepository.findByUsername(user.username()).isPresent()) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.password().length() < 8 || user.password().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }

        if (!user.passwordConfirm().equals(user.password())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}
