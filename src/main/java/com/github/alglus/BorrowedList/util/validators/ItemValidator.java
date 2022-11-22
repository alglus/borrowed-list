package com.github.alglus.BorrowedList.util.validators;

import com.github.alglus.BorrowedList.models.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class ItemValidator implements Validator {

    private final MessageSource messageSource;

    @Autowired
    public ItemValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        checkDateIsNotInTheFuture(item, errors);
        checkDueDateIsAfterDate(item, errors);
    }

    private void checkDateIsNotInTheFuture(Item item, Errors errors) {
        LocalDate date = item.getDate();

        if (date != null && date.isAfter(LocalDate.now())) {
            errors.rejectValue("date", "", messageSource.getMessage(
                    "item.validate.date.not-in-future", null, LocaleContextHolder.getLocale()));
        }
    }

    private void checkDueDateIsAfterDate(Item item, Errors errors) {
        LocalDate date = item.getDate();
        LocalDate dueDate = item.getDueDate();

        if (dueDate != null && date != null && dueDate.isBefore(date)) {
            errors.rejectValue("dueDate", "",
                    messageSource.getMessage(
                            "item.validate.duedate.not-before-date." + item.getType().toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ));
        }
    }
}
