package com.github.alglus.BorrowedList.dto;

import javax.validation.constraints.NotNull;

public class SettingsPageDTO {

    @NotNull
    private String languageCode;

    @NotNull
    private String homepageItemType;

    public SettingsPageDTO() {
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getHomepageItemType() {
        return homepageItemType;
    }

    public void setHomepageItemType(String homepageItemType) {
        this.homepageItemType = homepageItemType;
    }
}
