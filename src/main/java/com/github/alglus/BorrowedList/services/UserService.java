package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.models.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserService {

    @Transactional(readOnly = true)
    Optional<User> findById(long id);

    User findByIdOrThrow(long id, RuntimeException exception);

    User findByIdOrThrowResponseStatusNotFoundException(long id);

    @Transactional(readOnly = true)
    Optional<User> findByEmail(String email);

    @Transactional
    void save(User user);

    @Transactional
    void delete(long id);
}
