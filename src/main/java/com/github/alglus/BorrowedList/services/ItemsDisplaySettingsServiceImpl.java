package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.dto.ItemsDisplaySettingsDTO;
import com.github.alglus.BorrowedList.dto.ItemsDisplaySettingsDTO.SettingName;
import com.github.alglus.BorrowedList.models.Settings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ItemsDisplaySettingsServiceImpl implements ItemsDisplaySettingsService {

    private final SettingsService settingsService;
    private final Map<String, List<String>> allowedItemsDisplayOptions;
    private final Map<String, String> defaultItemsDisplayOption;

    public ItemsDisplaySettingsServiceImpl(SettingsService settingsService, Map<String,
            List<String>> allowedItemsDisplayOptions, Map<String, String> defaultItemsDisplayOption) {
        this.settingsService = settingsService;
        this.allowedItemsDisplayOptions = allowedItemsDisplayOptions;
        this.defaultItemsDisplayOption = defaultItemsDisplayOption;
    }

    @Override
    @Transactional
    public void saveItemsDisplaySettingsOrGetDefaults(ItemsDisplaySettingsDTO newSettings) {

        validateNewSettings(newSettings);

        ItemsDisplaySettingsDTO userItemsDisplaySettings = new ItemsDisplaySettingsDTO(newSettings.getUserId());

        Settings allUserSettings = getAllUserSettingsAndFillItemsDisplaySettings(userItemsDisplaySettings);

        overwriteUserSettingsWithNewSettingsAndSetUndefinedSettings(newSettings, userItemsDisplaySettings);

        saveItemsDisplaySettings(allUserSettings, userItemsDisplaySettings);
    }

    private void validateNewSettings(ItemsDisplaySettingsDTO newSettings) {

        for (SettingName settingName : SettingName.values()) {

            List<String> allowedValues = allowedItemsDisplayOptions.get(settingName.name());

            String newValue = newSettings.getBySettingName(settingName);

            if (!allowedValues.contains(newValue)) {

                // The passed value is not allowed, so set this setting to null,
                // which will force the code later on to load the saved settings, or get the default value.
                newSettings.setBySettingName(settingName, null);
            }
        }
    }

    private Settings getAllUserSettingsAndFillItemsDisplaySettings(ItemsDisplaySettingsDTO userItemsDisplaySettings) {

        Settings allUserSettings = settingsService.findByUserIdOrCreateNewEntry(userItemsDisplaySettings.getUserId());

        userItemsDisplaySettings.setItemsPerPage(allUserSettings.getItemsPerPage());
        userItemsDisplaySettings.setSortBy(allUserSettings.getSortBy());
        userItemsDisplaySettings.setSortDirection(allUserSettings.getSortDirection());

        return allUserSettings;
    }

    private void overwriteUserSettingsWithNewSettingsAndSetUndefinedSettings(ItemsDisplaySettingsDTO newSettings,
                                                                             ItemsDisplaySettingsDTO userSettings) {

        for (SettingName settingName : SettingName.values()) {

            if (settingHasNotBeenDefined(settingName, newSettings)) {

                setUndefinedSettingToDefaultOrSavedValue(settingName, newSettings, userSettings);

            } else {

                saveNewSettingIntoUserSettings(settingName, newSettings, userSettings);
            }
        }
    }

    private void setUndefinedSettingToDefaultOrSavedValue(SettingName settingName, ItemsDisplaySettingsDTO newSettings,
                                                          ItemsDisplaySettingsDTO savedSettings) {

        if (settingHasNotBeenDefined(settingName, savedSettings)) {

            setToDefaultValue(settingName, newSettings, savedSettings);

        } else {

            setToSavedValue(settingName, newSettings, savedSettings);
        }
    }

    private boolean settingHasNotBeenDefined(SettingName settingName, ItemsDisplaySettingsDTO settings) {

        return settings.getBySettingName(settingName) == null;
    }

    private void setToDefaultValue(SettingName settingName, ItemsDisplaySettingsDTO newSettings, ItemsDisplaySettingsDTO savedSettings) {

        String defaultValue = defaultItemsDisplayOption.get(settingName.name());

        savedSettings.setBySettingName(settingName, defaultValue);
        newSettings.setBySettingName(settingName, defaultValue);
    }

    private void setToSavedValue(SettingName settingName, ItemsDisplaySettingsDTO newSettings, ItemsDisplaySettingsDTO savedSettings) {

        String savedValue = savedSettings.getBySettingName(settingName);

        newSettings.setBySettingName(settingName, savedValue);
    }

    private void saveNewSettingIntoUserSettings(SettingName settingName, ItemsDisplaySettingsDTO newSettings,
                                                ItemsDisplaySettingsDTO userSettings) {

        String newValue = newSettings.getBySettingName(settingName);

        userSettings.setBySettingName(settingName, newValue);
    }

    private void saveItemsDisplaySettings(Settings allSettings, ItemsDisplaySettingsDTO itemsDisplaySettings) {

        allSettings.setItemsPerPage(itemsDisplaySettings.getItemsPerPage());
        allSettings.setSortBy(itemsDisplaySettings.getSortBy());
        allSettings.setSortDirection(itemsDisplaySettings.getSortDirection());

        settingsService.update(allSettings);
    }
}
