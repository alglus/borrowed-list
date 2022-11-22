package com.github.alglus.BorrowedList.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;

public enum Category {
    TOOLS, CLOTHES, MONEY, BOOKS, FURNITURE, OTHER;

    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getTitle() {
        return messageSource.getMessage(
                "category." + this.name(),
                null,
                LocaleContextHolder.getLocale()
        );
    }

    /**
     * This class is used to inject the MessageSource bean into each Category enum object.
     * The message source is necessary, in order to retrieve the category title in the currently selected language.
     */
    @Component
    public static class MessageSourceInjector {

        private final MessageSource messageSource;

        @Autowired
        public MessageSourceInjector(MessageSource messageSource) {
            this.messageSource = messageSource;
        }

        @PostConstruct
        public void injectMessageSource() {
            for (Category category : EnumSet.allOf(Category.class)) {
                category.setMessageSource(messageSource);
            }
        }
    }
}
