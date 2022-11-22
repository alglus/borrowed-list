package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userService.findByEmail(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Account with this email not found.");
        }

        User user = userOptional.get();
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), user.isEmailVerified(), user.getRole());
    }
}
