package com.github.alglus.BorrowedList.services;

import com.github.alglus.BorrowedList.models.Item;
import com.github.alglus.BorrowedList.models.ItemType;
import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.repositories.ItemRepository;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Item> findById(long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Item findByIdOrThrow(long id, RuntimeException exception) {
        Optional<Item> item = findById(id);

        if (item.isEmpty()) {
            throw exception;
        }

        return item.get();
    }

    @Override
    public Item findByIdOrThrowResponseStatusNotFoundException(long id) {
        return findByIdOrThrow(id, new ResponseStatusException(NOT_FOUND, "Unable to find this item."));
    }

    @Override
    @Transactional
    public void save(Item item, ItemType itemType) {

        long userId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        User user = userService.findByIdOrThrowResponseStatusNotFoundException(userId);

        item.setUser(user);
        item.setType(itemType);
        item.setReturned(false);

        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void update(Item updatedItem, long id) {
        Item originalItem = findByIdOrThrowResponseStatusNotFoundException(id);

        updatedItem.setId(id);
        updatedItem.setUser(originalItem.getUser());
        updatedItem.setType(originalItem.getType());

        itemRepository.save(updatedItem);
    }

    @Override
    @Transactional
    public void delete(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> findAllByUserIdAndTypeAndReturnedOrderedWithPagination(long userId, ItemType itemType, boolean returned, String sortBy,
                                                                             Sort.Direction direction, int pageNumber, int pagesPerPage) {
        User user = userService.findByIdOrThrowResponseStatusNotFoundException(userId);

        return itemRepository.findItemsByUserAndTypeAndReturned(
                user, itemType, returned,
                PageRequest.of(
                        pageNumber, pagesPerPage,
                        Sort.by(direction, sortBy).and(Sort.by(Sort.Order.asc("id")))));
    }

    @Override
    @Transactional
    public void markReturned(Item item, boolean returned) {
        item.setReturned(returned);
        itemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findDistinctSortedWhomNames() {
        long userId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        return itemRepository.findDistinctWhomNamesByUserSortedByWhomAsc(userId);
    }
}
