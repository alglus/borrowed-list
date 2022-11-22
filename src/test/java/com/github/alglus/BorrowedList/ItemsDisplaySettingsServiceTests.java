package com.github.alglus.BorrowedList;

import com.github.alglus.BorrowedList.config.SpringConfig;
import com.github.alglus.BorrowedList.dto.ItemsDisplaySettingsDTO;
import com.github.alglus.BorrowedList.models.Role;
import com.github.alglus.BorrowedList.models.Settings;
import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.security.LangSettingLogoutSuccessHandler;
import com.github.alglus.BorrowedList.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@ContextConfiguration(classes = {ItemsDisplaySettingsServiceImpl.class, SettingsServiceImpl.class, UserServiceImpl.class,
        SpringConfig.class, CustomUserDetailsService.class})
@EnableAutoConfiguration
public class ItemsDisplaySettingsServiceTests {
    private final ItemsDisplaySettingsService itemsDisplaySettingsService;
    private final SettingsService settingsService;
    private final UserService userService;

    private final Map<String, List<String>> allowedItemsDisplayOptions;
    private final Map<String, String> defaultItemsDisplayOption;

    @MockBean
    private LangSettingLogoutSuccessHandler langSettingLogoutSuccessHandler;

    @Autowired
    public ItemsDisplaySettingsServiceTests(ItemsDisplaySettingsServiceImpl itemsDisplaySettingsService,
                                            SettingsService settingsService, UserService userService,
                                            Map<String, List<String>> allowedItemsDisplayOptions,
                                            Map<String, String> defaultItemsDisplayOption) {
        this.itemsDisplaySettingsService = itemsDisplaySettingsService;
        this.settingsService = settingsService;
        this.userService = userService;
        this.allowedItemsDisplayOptions = allowedItemsDisplayOptions;
        this.defaultItemsDisplayOption = defaultItemsDisplayOption;
    }

    @BeforeEach
    void createAndSaveUserToDatabase() {
        User user = new User("name@example.com", "password", true, Role.USER);
        userService.save(user);
    }

    @Test
    void when_user_has_no_saved_settings_and_invalid_settings_are_passed_defaults_are_set() {
        long userId = userService.findByEmail("name@example.com").get().getId(); // The Optional is not empty: see @BeforeEach

        // No saved settings.
        Optional<Settings> settingsBefore = settingsService.findByUserId(userId);
        assertThat(settingsBefore).isNotPresent();


        // Invalid settings are passed.
        ItemsDisplaySettingsDTO invalidSettings = new ItemsDisplaySettingsDTO(userId,
                "-42", "invalidCategory", "someDirection");

        itemsDisplaySettingsService.saveItemsDisplaySettingsOrGetDefaults(invalidSettings);


        // Defaults were set.
        assertThat(invalidSettings.getItemsPerPage()).isEqualTo(defaultItemsDisplayOption.get("ITEMS_PER_PAGE"));
        assertThat(invalidSettings.getSortBy()).isEqualTo(defaultItemsDisplayOption.get("SORT_BY"));
        assertThat(invalidSettings.getSortDirection()).isEqualTo(defaultItemsDisplayOption.get("SORT_DIRECTION"));

        Settings savedSettings = settingsService.findByUserIdOrThrowResponseStatusNotFoundException(userId);

        assertThat(savedSettings.getItemsPerPage()).isEqualTo(defaultItemsDisplayOption.get("ITEMS_PER_PAGE"));
        assertThat(savedSettings.getSortBy()).isEqualTo(defaultItemsDisplayOption.get("SORT_BY"));
        assertThat(savedSettings.getSortDirection()).isEqualTo(defaultItemsDisplayOption.get("SORT_DIRECTION"));
    }

    @Test
    void when_user_has_no_saved_settings_and_new_settings_are_undefined_they_are_filled_with_default_values() {
        long userId = userService.findByEmail("name@example.com").get().getId(); // The Optional is not empty: see @BeforeEach

        // No saved settings.
        Optional<Settings> settingsBefore = settingsService.findByUserId(userId);
        assertThat(settingsBefore).isNotPresent();


        // Undefined new settings are passed.
        ItemsDisplaySettingsDTO newUndefinedSettings = new ItemsDisplaySettingsDTO(userId, null, null, null);
        itemsDisplaySettingsService.saveItemsDisplaySettingsOrGetDefaults(newUndefinedSettings);


        // Settings were filled with defaults.
        assertThat(newUndefinedSettings.getItemsPerPage()).isEqualTo(defaultItemsDisplayOption.get("ITEMS_PER_PAGE"));
        assertThat(newUndefinedSettings.getSortBy()).isEqualTo(defaultItemsDisplayOption.get("SORT_BY"));
        assertThat(newUndefinedSettings.getSortDirection()).isEqualTo(defaultItemsDisplayOption.get("SORT_DIRECTION"));

        Settings settingsAfter = settingsService.findByUserIdOrThrowResponseStatusNotFoundException(userId);

        assertThat(settingsAfter.getItemsPerPage()).isEqualTo(defaultItemsDisplayOption.get("ITEMS_PER_PAGE"));
        assertThat(settingsAfter.getSortBy()).isEqualTo(defaultItemsDisplayOption.get("SORT_BY"));
        assertThat(settingsAfter.getSortDirection()).isEqualTo(defaultItemsDisplayOption.get("SORT_DIRECTION"));
    }

    @Test
    void when_user_has_no_saved_settings_and_valid_settings_are_passed_they_are_saved_as_the_user_settings() {
        long userId = userService.findByEmail("name@example.com").get().getId(); // The Optional is not empty: see @BeforeEach

        // No saved settings.
        Optional<Settings> settingsBefore = settingsService.findByUserId(userId);
        assertThat(settingsBefore).isNotPresent();


        // Valid new settings are passed.
        String newItemsPerPage = "20";
        String newSortBy = "dueDate";
        String newSortDirection = "desc";
        ItemsDisplaySettingsDTO newValidSettings = new ItemsDisplaySettingsDTO(userId,
                newItemsPerPage, newSortBy, newSortDirection);
        itemsDisplaySettingsService.saveItemsDisplaySettingsOrGetDefaults(newValidSettings);


        // Settings were filled with the saved values.
        assertThat(newValidSettings.getItemsPerPage()).isEqualTo(newItemsPerPage);
        assertThat(newValidSettings.getSortBy()).isEqualTo(newSortBy);
        assertThat(newValidSettings.getSortDirection()).isEqualTo(newSortDirection);

        Settings settingsAfter = settingsService.findByUserIdOrThrowResponseStatusNotFoundException(userId);

        assertThat(settingsAfter.getItemsPerPage()).isEqualTo(newItemsPerPage);
        assertThat(settingsAfter.getSortBy()).isEqualTo(newSortBy);
        assertThat(settingsAfter.getSortDirection()).isEqualTo(newSortDirection);
    }

    @Test
    void when_user_has_saved_settings_and_new_settings_are_undefined_they_are_filled_with_the_saved_values() {
        User user = userService.findByEmail("name@example.com").get(); // The Optional is not empty: see @BeforeEach

        // User has saved settings.
        Settings savedSettings = settingsService.findByUserIdOrCreateNewEntry(user.getId());

        savedSettings.setItemsPerPage("5");
        savedSettings.setSortBy("title");
        savedSettings.setSortDirection("asc");

        settingsService.update(savedSettings);


        // Undefined new settings are passed.
        ItemsDisplaySettingsDTO newUndefinedSettings = new ItemsDisplaySettingsDTO(user.getId(), null, null, null);
        itemsDisplaySettingsService.saveItemsDisplaySettingsOrGetDefaults(newUndefinedSettings);


        // Settings were filled with the saved values.
        assertThat(newUndefinedSettings.getItemsPerPage()).isEqualTo(savedSettings.getItemsPerPage());
        assertThat(newUndefinedSettings.getSortBy()).isEqualTo(savedSettings.getSortBy());
        assertThat(newUndefinedSettings.getSortDirection()).isEqualTo(savedSettings.getSortDirection());

        Settings settingsAfter = settingsService.findByUserIdOrThrowResponseStatusNotFoundException(user.getId());

        assertThat(settingsAfter.getItemsPerPage()).isEqualTo(savedSettings.getItemsPerPage());
        assertThat(settingsAfter.getSortBy()).isEqualTo(savedSettings.getSortBy());
        assertThat(settingsAfter.getSortDirection()).isEqualTo(savedSettings.getSortDirection());
    }

    @Test
    void when_user_has_saved_settings_and_invalid_settings_are_passed_they_are_filled_with_saved_values() {
        User user = userService.findByEmail("name@example.com").get(); // The Optional is not empty: see @BeforeEach

        // User has saved settings.
        Settings savedSettings = settingsService.findByUserIdOrCreateNewEntry(user.getId());

        savedSettings.setItemsPerPage("5");
        savedSettings.setSortBy("title");
        savedSettings.setSortDirection("asc");

        settingsService.update(savedSettings);


        // Undefined new settings are passed.
        ItemsDisplaySettingsDTO newInvalidSettings = new ItemsDisplaySettingsDTO(user.getId(),
                "-42", "invalidCategory", "someDirection");
        itemsDisplaySettingsService.saveItemsDisplaySettingsOrGetDefaults(newInvalidSettings);


        // Settings were filled with the saved values.
        assertThat(newInvalidSettings.getItemsPerPage()).isEqualTo(savedSettings.getItemsPerPage());
        assertThat(newInvalidSettings.getSortBy()).isEqualTo(savedSettings.getSortBy());
        assertThat(newInvalidSettings.getSortDirection()).isEqualTo(savedSettings.getSortDirection());

        Settings settingsAfter = settingsService.findByUserIdOrThrowResponseStatusNotFoundException(user.getId());

        assertThat(settingsAfter.getItemsPerPage()).isEqualTo(savedSettings.getItemsPerPage());
        assertThat(settingsAfter.getSortBy()).isEqualTo(savedSettings.getSortBy());
        assertThat(settingsAfter.getSortDirection()).isEqualTo(savedSettings.getSortDirection());
    }

    @Test
    void when_user_has_saved_settings_and_valid_new_settings_are_passed_they_overwrite_the_saved_ones() {
        User user = userService.findByEmail("name@example.com").get(); // The Optional is not empty: see @BeforeEach

        // User has saved settings.
        Settings savedSettings = settingsService.findByUserIdOrCreateNewEntry(user.getId());

        savedSettings.setItemsPerPage("5");
        savedSettings.setSortBy("title");
        savedSettings.setSortDirection("asc");

        settingsService.update(savedSettings);


        // Valid new settings are passed.
        String newItemsPerPage = "20";
        String newSortBy = "dueDate";
        String newSortDirection = "desc";
        ItemsDisplaySettingsDTO newValidSettings = new ItemsDisplaySettingsDTO(user.getId(),
                newItemsPerPage, newSortBy, newSortDirection);
        itemsDisplaySettingsService.saveItemsDisplaySettingsOrGetDefaults(newValidSettings);


        // Settings were filled with the saved values.
        assertThat(newValidSettings.getItemsPerPage()).isEqualTo(newItemsPerPage);
        assertThat(newValidSettings.getSortBy()).isEqualTo(newSortBy);
        assertThat(newValidSettings.getSortDirection()).isEqualTo(newSortDirection);

        Settings settingsAfter = settingsService.findByUserIdOrThrowResponseStatusNotFoundException(user.getId());

        assertThat(settingsAfter.getItemsPerPage()).isEqualTo(newItemsPerPage);
        assertThat(settingsAfter.getSortBy()).isEqualTo(newSortBy);
        assertThat(settingsAfter.getSortDirection()).isEqualTo(newSortDirection);
    }
}


