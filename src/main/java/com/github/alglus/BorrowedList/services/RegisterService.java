package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.dto.ChangePasswordDTO;
import com.github.alglus.BorrowedList.dto.RegistrationDTO;

public interface RegisterService {

    void registerNewUser(RegistrationDTO registrationDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO, long userId);
}
