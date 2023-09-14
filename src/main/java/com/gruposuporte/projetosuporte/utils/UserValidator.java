package com.gruposuporte.projetosuporte.utils;

import com.gruposuporte.projetosuporte.dto.SignupRequest;
import com.gruposuporte.projetosuporte.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {


    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SignupRequest.class.equals(aClass);
    }


    @Override
    public void validate(Object o, Errors errors) {
        SignupRequest user = (SignupRequest) o;

//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.username().length() < 6 || user.username().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username");
        }

        if (user.username().length() < 3 || user.username().length() > 32) {
            errors.rejectValue("name", "Size.userForm.name");
        }


        if (userRepository.findByUsername(user.username()).isPresent()) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

        if (!isValidEmail(user.email())) {
            errors.rejectValue("email", "Invalid.userForm.email");
        }

//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.password().length() < 6 || user.password().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }

        if (!user.passwordConfirm().equals(user.password())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
