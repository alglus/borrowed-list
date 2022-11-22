package com.github.alglus.BorrowedList.repositories;

import com.github.alglus.BorrowedList.models.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
}
