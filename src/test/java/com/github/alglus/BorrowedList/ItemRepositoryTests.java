package com.github.alglus.BorrowedList;

import com.github.alglus.BorrowedList.models.Role;
import com.github.alglus.BorrowedList.models.User;
import com.github.alglus.BorrowedList.repositories.ItemRepository;
import com.github.alglus.BorrowedList.services.UserService;
import com.github.alglus.BorrowedList.services.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@ContextConfiguration(classes = {ItemRepository.class, UserServiceImpl.class})
@EnableAutoConfiguration
public class ItemRepositoryTests {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemRepositoryTests(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Test
    void findWhomNames_returns_empty_list_if_no_items_have_ever_been_created() {
        User user = new User("user@example.com", "password", true, Role.USER);
        userService.save(user);

        List<String> allUserWhomNames = itemRepository.findDistinctWhomNamesByUserSortedByWhomAsc(user.getId());
        assertThat(allUserWhomNames).asList().isEmpty();
    }

    @Test
    @Sql("classpath:sql/item-repository-test-data.sql")
    void findWhomNames_returns_all_names_in_correct_order() {
        // The sql script inserts five items with the following 'whom' values:
        //   user_id=1: {'name3', 'name2', 'name2', 'name1'}
        //   user_id=2: {'name4'}
        //
        // This serves to test three aspects of the query:
        // - two duplicate names: test the 'DISTINCT' query command
        // - names are inserted in reverse order: test the 'ORDER BY whom ASC' command
        // - there is an items from another user ID, which may not be included in the returned result

        long userId = 1;

        List<String> expectedList = List.of("name1", "name2", "name3");
        List<String> returnedList = itemRepository.findDistinctWhomNamesByUserSortedByWhomAsc(userId);

        assertThat(returnedList).isEqualTo(expectedList);
    }

}
