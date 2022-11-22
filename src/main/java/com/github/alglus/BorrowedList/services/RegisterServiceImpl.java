package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.dto.ChangePasswordDTO;
import com.github.alglus.BorrowedList.dto.RegistrationDTO;
import com.github.alglus.BorrowedList.models.Role;
import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterServiceImpl(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerNewUser(RegistrationDTO registrationDTO) {
        User user = new User();

        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setEmailVerified(true); // TODO: implement e-mail verification
        user.setRole(Role.USER);

        userService.save(user);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO, long userId) {

        User user = userService.findByIdOrThrowResponseStatusNotFoundException(userId);
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String newPasswordEncoded = passwordEncoder.encode(changePasswordDTO.getNewPassword());

        user.setPassword(newPasswordEncoded);
        userDetails.setPassword(newPasswordEncoded);

        userService.save(user);
    }
}
