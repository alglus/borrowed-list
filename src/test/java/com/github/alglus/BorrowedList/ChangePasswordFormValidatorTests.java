package com.github.alglus.BorrowedList;

import com.github.alglus.BorrowedList.config.TestsConfig;
import com.github.alglus.BorrowedList.config.WithMockCustomUser;
import com.github.alglus.BorrowedList.dto.ChangePasswordDTO;
import com.github.alglus.BorrowedList.util.validators.ChangePasswordFormValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestsConfig.class)
@SpringBootTest(classes = ChangePasswordFormValidator.class)
public class ChangePasswordFormValidatorTests {

    private final PasswordEncoder passwordEncoder;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    public ChangePasswordFormValidatorTests(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Test
    @WithMockCustomUser
    void correct_password_is_validated_without_errors() {
        ChangePasswordFormValidator validator = new ChangePasswordFormValidator(messageSource, passwordEncoder);

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("password");
        changePasswordDTO.setNewPassword("newPassword");
        changePasswordDTO.setRetypedPassword("newPassword");

        Errors errors = new BeanPropertyBindingResult(changePasswordDTO, "changePasswordDTO");

        validator.validate(changePasswordDTO, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    @WithMockCustomUser
    void wrong_password_is_validated_with_errors() {
        ChangePasswordFormValidator validator = new ChangePasswordFormValidator(messageSource, passwordEncoder);

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("pwd");
        changePasswordDTO.setNewPassword("newPassword");
        changePasswordDTO.setRetypedPassword("newPassword");

        Errors errors = new BeanPropertyBindingResult(changePasswordDTO, "changePasswordDTO");

        validator.validate(changePasswordDTO, errors);

        assertTrue(errors.hasErrors());
    }
}
