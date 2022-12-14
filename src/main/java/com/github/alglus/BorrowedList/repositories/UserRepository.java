package com.github.alglus.BorrowedList.repositories;

import com.github.alglus.BorrowedList.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("DELETE from User u WHERE u.id = ?1")
    void deleteById(long id);
}
