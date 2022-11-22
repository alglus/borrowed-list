package com.github.alglus.BorrowedList.dto;

/**
 * Any DTO, which implements this interface, can use the @PasswordsMatch annotation.
 */
public interface NewPasswordDTO {

    String getPassword();

    String getRetypedPassword();
}
