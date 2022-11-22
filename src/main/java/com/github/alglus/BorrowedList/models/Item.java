package com.github.alglus.BorrowedList.models;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "returned")
    private boolean returned;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{item.validate.category.notnull}")
    private Category category;

    @Column(name = "title")
    @NotEmpty(message = "{item.validate.title.notempty}")
    private String title;

    @Column(name = "whom")
    @NotEmpty(message = "{item.validate.whom.notempty}")
    private String whom;

    @Column(name = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "{item.validate.date.notnull}")
    private LocalDate date;

    @Column(name = "due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Column(name = "description")
    private String description;

    public Item() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWhom() {
        return whom;
    }

    public void setWhom(String who) {
        this.whom = who;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isBorrowed() {
        return type == ItemType.borrowed;
    }

    public boolean isLent() {
        return type == ItemType.lent;
    }

    public long getDaysAgo() {
        return ChronoUnit.DAYS.between(date, LocalDate.now());
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", user=" + user +
                ", returned=" + returned +
                ", type=" + type +
                ", category=" + category +
                ", title='" + title + '\'' +
                ", whom='" + whom + '\'' +
                ", date=" + date +
                ", dueDate=" + dueDate +
                ", description='" + description + '\'' +
                '}';
    }
}
