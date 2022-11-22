package com.github.alglus.BorrowedList.dto;

import com.github.alglus.BorrowedList.util.validators.PasswordsMatch;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@PasswordsMatch
public class ChangePasswordDTO implements NewPasswordDTO {

    @NotEmpty(message = "{account.password.current.notempty}")
    private String currentPassword;

    @Length(min = 6, message = "{validate.password.minlength}")
    @Length(max = 256, message = "{validate.password.maxlength}")
    private String newPassword;

    @Length(max = 256, message = "{validate.password.maxlength}")
    private String retypedPassword;

    public ChangePasswordDTO() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    @Override
    public String getPassword() {
        return getNewPassword();
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }
}
