package com.github.alglus.BorrowedList.util.validators;

import com.github.alglus.BorrowedList.dto.ChangePasswordDTO;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChangePasswordFormValidator implements Validator {

    private final MessageSource messageSource;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ChangePasswordFormValidator(MessageSource messageSource, PasswordEncoder passwordEncoder) {
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ChangePasswordDTO changePasswordDTO = (ChangePasswordDTO) target;

        authenticateUser(changePasswordDTO, errors);
    }

    private void authenticateUser(ChangePasswordDTO changePasswordDTO, Errors errors) {

        if (passwordInputIsNotEmpty(changePasswordDTO)) {

            if (wrongPassword(changePasswordDTO)) {
                errors.rejectValue("currentPassword", "", messageSource.getMessage(
                        "account.password.current.wrong", null, LocaleContextHolder.getLocale()));
            }
        }
    }

    private boolean passwordInputIsNotEmpty(ChangePasswordDTO changePasswordDTO) {
        return !changePasswordDTO.getCurrentPassword().trim().isEmpty();
    }

    private boolean wrongPassword(ChangePasswordDTO changePasswordDTO) {
        String correctPassword = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPassword();
        String inputPassword = changePasswordDTO.getCurrentPassword();

        return !passwordEncoder.matches(inputPassword, correctPassword);
    }
}
