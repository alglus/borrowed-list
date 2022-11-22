package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.models.Settings;
import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.repositories.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;
    private final UserService userService;

    @Value("${alglus.homepage.default-item-type}")
    private String defaultHomepageItemType;

    @Value("${alglus.homepage.base-url}")
    private String homepageBaseURL;

    @Autowired
    public SettingsServiceImpl(SettingsRepository settingsRepository, UserService userService) {
        this.settingsRepository = settingsRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void save(Settings settings) {
        settingsRepository.save(settings);
    }

    @Override
    @Transactional
    public void update(Settings updatedSettings) {
        settingsRepository.save(updatedSettings);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Settings> findByUserId(long userId) {
        return settingsRepository.findById(userId);
    }

    @Override
    public Settings findByUserIdOrThrow(long userId, RuntimeException exception) {
        Optional<Settings> settings = findByUserId(userId);

        if (settings.isEmpty()) {
            throw exception;
        }

        return settings.get();
    }

    @Override
    public Settings findByUserIdOrThrowResponseStatusNotFoundException(long userId) {
        return findByUserIdOrThrow(userId, new ResponseStatusException(NOT_FOUND, "Unable to find this user."));
    }


    @Override
    public Settings findByUserIdOrCreateNewEntry(long userId) {

        Optional<Settings> settings = findByUserId(userId);

        if (settings.isEmpty()) {
            Settings emptySettings = new Settings();
            User user = userService.findByIdOrThrowResponseStatusNotFoundException(userId);
            emptySettings.setUser(user);

            save(emptySettings);

            settings = Optional.of(emptySettings);
        }

        return settings.get();
    }

    @Override
    @Transactional(readOnly = true)
    public String getPreferredLanguageCode(long userId) {
        Settings settings = findByUserIdOrCreateNewEntry(userId);
        return settings.getLanguageCode();
    }

    @Override
    @Transactional
    public void savePreferredLanguageCode(long userId, String languageCode) {
        Settings settings = findByUserIdOrCreateNewEntry(userId);
        settings.setLanguageCode(languageCode);
    }

    @Override
    @Transactional(readOnly = true)
    public String getPreferredHomepageItemType(long userId) {
        Settings settings = findByUserIdOrCreateNewEntry(userId);
        return settings.getHomepageItemType();
    }

    @Override
    public String getPreferredHomepageItemTypeOrDefault(long userId) {
        String savedPreferredHomepageItemType = getPreferredHomepageItemType(userId);

        if (hasNotBeenSetYet(savedPreferredHomepageItemType)) {
            return defaultHomepageItemType;
        } else {
            return savedPreferredHomepageItemType;
        }
    }

    @Override
    public String getPreferredHomepageItemTypeOrDefault(Settings savedSettings) {
        String savedPreferredHomepageItemType = savedSettings.getHomepageItemType();

        if (hasNotBeenSetYet(savedPreferredHomepageItemType)) {
            return defaultHomepageItemType;
        } else {
            return savedPreferredHomepageItemType;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getPreferredHomepageURL(long userId) {
        String preferredHomepageItemType = getPreferredHomepageItemTypeOrDefault(userId);
        return homepageBaseURL + preferredHomepageItemType;
    }

    @Override
    @Transactional
    public void savePreferredHomepageItemType(long userId, String homepageItemType) {
        Settings settings = findByUserIdOrCreateNewEntry(userId);
        settings.setHomepageItemType(homepageItemType);
    }
}
