package com.github.alglus.BorrowedList;

import com.github.alglus.BorrowedList.models.Role;
import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@Sql("classpath:sql/user-repository-test-data.sql")
public class UserRepositoryTests {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void findByEmail_with_wrong_email_returns_empty_optional() {
        Optional<User> userOptional = userRepository.findByEmail("wrongEmail");
        assertThat(userOptional).isNotPresent();
    }

    @Test
    void findByEmail_with_correct_email_returns_correct_user() {
        String correctEmail = "name1@example.com";

        Optional<User> userOptional = userRepository.findByEmail(correctEmail);
        assertThat(userOptional).isPresent();

        User user = userOptional.get();
        assertThat(user.getEmail()).isEqualTo(correctEmail);
    }

    @Test
    void deleteById_successfully_deletes_existing_user() {
        long userToDeleteID = 1;

        long usersInDBBeforeDelete = userRepository.count();

        userRepository.deleteById(userToDeleteID);

        Optional<User> userOptionalAfterDelete = userRepository.findById(userToDeleteID);
        assertThat(userOptionalAfterDelete).isNotPresent();

        long usersInDBAfterDelete = userRepository.count();
        assertThat(usersInDBAfterDelete).isEqualTo(usersInDBBeforeDelete - 1);
    }

    @Test
    void save_successfully_adds_user_to_DB() {
        String newUserEmail = "newUserName@example.com";

        User newUser = new User(
                newUserEmail,
                "password",
                true,
                Role.USER
        );

        userRepository.save(newUser);

        Optional<User> newUserOptional = userRepository.findByEmail(newUserEmail);
        assertThat(newUserOptional).isPresent();
    }
}
