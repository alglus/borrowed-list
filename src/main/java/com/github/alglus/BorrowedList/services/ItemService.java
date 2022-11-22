package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.models.Item;
import com.github.alglus.BorrowedList.models.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    @Transactional(readOnly = true)
    Optional<Item> findById(long id);

    Item findByIdOrThrow(long id, RuntimeException exception);

    Item findByIdOrThrowResponseStatusNotFoundException(long id);

    @Transactional
    void save(Item item, ItemType itemType);

    @Transactional
    void update(Item updatedItem, long id);

    @Transactional
    void delete(long id);

    @Transactional(readOnly = true)
    Page<Item> findAllByUserIdAndTypeAndReturnedOrderedWithPagination(long userId, ItemType itemType, boolean returned, String sortBy,
                                                                      Sort.Direction direction, int pageNumber, int pagesPerPage);

    @Transactional
    void markReturned(Item item, boolean returned);

    @Transactional(readOnly = true)
    List<String> findDistinctSortedWhomNames();
}
