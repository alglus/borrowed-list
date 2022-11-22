package com.github.alglus.BorrowedList.util.validators;

import com.github.alglus.BorrowedList.dto.NewPasswordDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, NewPasswordDTO> {

    @Override
    public boolean isValid(NewPasswordDTO form, ConstraintValidatorContext constraintValidatorContext) {

        return form.getPassword().equals(form.getRetypedPassword());
    }
}
