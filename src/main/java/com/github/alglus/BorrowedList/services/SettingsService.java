package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.models.Settings;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SettingsService {

    @Transactional
    void save(Settings settings);

    @Transactional
    void update(Settings updatedSettings);

    @Transactional(readOnly = true)
    Optional<Settings> findByUserId(long userId);

    Settings findByUserIdOrThrow(long userId, RuntimeException exception);

    Settings findByUserIdOrThrowResponseStatusNotFoundException(long userId);

    Settings findByUserIdOrCreateNewEntry(long userId);

    @Transactional(readOnly = true)
    String getPreferredLanguageCode(long userId);

    @Transactional
    void savePreferredLanguageCode(long userId, String languageCode);

    @Transactional(readOnly = true)
    String getPreferredHomepageItemType(long userId);

    String getPreferredHomepageItemTypeOrDefault(long userId);

    String getPreferredHomepageItemTypeOrDefault(Settings savedSettings);

    @Transactional(readOnly = true)
    String getPreferredHomepageURL(long userId);

    default boolean hasNotBeenSetYet(String preferredHomepageItemType) {
        return preferredHomepageItemType == null;
    }

    @Transactional
    void savePreferredHomepageItemType(long userId, String homepageItemType);
}
