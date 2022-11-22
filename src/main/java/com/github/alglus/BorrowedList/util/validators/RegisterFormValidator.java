package com.github.alglus.BorrowedList.util.validators;

import com.github.alglus.BorrowedList.dto.RegistrationDTO;
import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class RegisterFormValidator implements Validator {

    private final UserService userService;
    private final MessageSource messageSource;

    @Autowired
    public RegisterFormValidator(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegistrationDTO newRegistration = (RegistrationDTO) target;

        checkEmailIsUnique(newRegistration, errors);
    }

    private void checkEmailIsUnique(RegistrationDTO newRegistration, Errors errors) {

        Optional<User> userWithSameEmailInDB = userService.findByEmail(newRegistration.getEmail());

        if (userWithSameEmailInDB.isPresent()) {
            errors.rejectValue("email", "", messageSource.getMessage(
                    "register.validate.email.duplicate", null, LocaleContextHolder.getLocale()));
        }
    }
}
