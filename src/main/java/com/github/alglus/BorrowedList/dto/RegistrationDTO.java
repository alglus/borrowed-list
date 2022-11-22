package com.github.alglus.BorrowedList.dto;

import com.github.alglus.BorrowedList.util.validators.PasswordsMatch;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@PasswordsMatch
public class RegistrationDTO implements NewPasswordDTO {

    @NotEmpty(message = "{register.validate.email.notempty}")
    @Email(regexp = ".+@.+\\..+", message = "{register.validate.email.format}")
    @Length(max = 256, message = "{register.validate.email.maxlength}")
    private String email;

    @Length(min = 6, message = "{validate.password.minlength}")
    @Length(max = 256, message = "{validate.password.maxlength}")
    private String password;

    @Length(max = 256, message = "{validate.password.maxlength}")
    private String retypedPassword;

    public RegistrationDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }
}
