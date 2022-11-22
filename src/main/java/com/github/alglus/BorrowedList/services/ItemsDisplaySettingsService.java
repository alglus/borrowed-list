package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.dto.ItemsDisplaySettingsDTO;
import org.springframework.transaction.annotation.Transactional;

public interface ItemsDisplaySettingsService {
    @Transactional
    void saveItemsDisplaySettingsOrGetDefaults(ItemsDisplaySettingsDTO newSettings);
}
