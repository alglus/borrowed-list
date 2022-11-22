package com.github.alglus.BorrowedList.repositories;

import com.github.alglus.BorrowedList.models.Item;
import com.github.alglus.BorrowedList.models.ItemType;
import com.github.alglus.BorrowedList.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findItemsByUserAndTypeAndReturned(User user, ItemType itemType, boolean returned, PageRequest pageRequest);

    @Query("SELECT DISTINCT i.whom FROM Item i WHERE i.user.id = ?1 ORDER BY i.whom ASC")
    List<String> findDistinctWhomNamesByUserSortedByWhomAsc(long userId);
}
