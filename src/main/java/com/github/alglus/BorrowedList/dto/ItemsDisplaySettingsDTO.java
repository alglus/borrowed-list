package com.github.alglus.BorrowedList.dto;

public class ItemsDisplaySettingsDTO {

    private long userId;

    private String itemsPerPage;

    private String sortBy;

    private String sortDirection;

    public ItemsDisplaySettingsDTO(long userId) {
        this.userId = userId;
    }

    public ItemsDisplaySettingsDTO(long userId, String itemsPerPage, String sortBy, String sortDirection) {
        this.userId = userId;
        this.itemsPerPage = itemsPerPage;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(String itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getBySettingName(SettingName settingName) {
        return switch (settingName) {
            case ITEMS_PER_PAGE -> getItemsPerPage();
            case SORT_BY -> getSortBy();
            case SORT_DIRECTION -> getSortDirection();
        };
    }

    public void setBySettingName(SettingName settingName, String value) {
        switch (settingName) {
            case ITEMS_PER_PAGE -> setItemsPerPage(value);
            case SORT_BY -> setSortBy(value);
            case SORT_DIRECTION -> setSortDirection(value);
        }
    }

    public enum SettingName {
        ITEMS_PER_PAGE, SORT_BY, SORT_DIRECTION
    }
}
