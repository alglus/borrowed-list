package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByIdOrThrow(long id, RuntimeException exception) {
        Optional<User> user = findById(id);

        if (user.isEmpty()) {
            throw exception;
        }

        return user.get();
    }

    @Override
    public User findByIdOrThrowResponseStatusNotFoundException(long id) {
        return findByIdOrThrow(id, new ResponseStatusException(NOT_FOUND, "Unable to find this user."));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }
}
