package com.github.alglus.BorrowedList.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "settings")
public class Settings {

    @Id
    @Column(name = "user_id")
    private long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "homepage_item_type")
    private String homepageItemType;

    @Column(name = "items_per_page")
    private String itemsPerPage;

    @Column(name = "sort_by")
    private String sortBy;

    @Column(name = "sort_direction")
    private String sortDirection;

    public Settings() {
    }

    public Settings(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long id) {
        this.userId = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @Override
    public String toString() {
        return "Settings{" +
                "userId=" + userId +
                ", user=" + user +
                ", languageCode='" + languageCode + '\'' +
                ", homepageItemType='" + homepageItemType + '\'' +
                ", itemsPerPage='" + itemsPerPage + '\'' +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
