package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.AssertionErrors;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    private final Pageable pageable = new Pagination(0, 10, Sort.unsorted());

    private final User user = User.builder()
            .name("Ivan")
            .email("ivan@ya.ru")
            .build();

    private final Request request = Request.builder()
            .description("Description")
            .owner(user)
            .created(LocalDateTime.now())
            .build();

    private final Item item = Item.builder()
            .name("Item name")
            .description("Item description")
            .available(true)
            .request(request)
            .owner(user)
            .build();

    @Test
    void testContextLoads() {
        assertNotNull(entityManager);
    }

    @Test
    void testSearch() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        List<Item> itemList = itemRepository.findItemsByText("item", pageable);

        AssertionErrors.assertEquals("There should have been 1 Item in the list", 1, itemList.size());
    }

    @Test
    void testSearchNotFound() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        List<Item> itemList = itemRepository.findItemsByText("itemName", pageable);

        AssertionErrors.assertEquals("There should have been 0 Item in the list", 0, itemList.size());
    }

    @Test
    void testFindAllByRequestIds() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        List<Item> itemList = itemRepository
                .findAllByRequestIds(List.of(request.getId()));

        AssertionErrors.assertEquals("There should have been 1 Item in the list", 1, itemList.size());
    }

    @Test
    void testFindAllByRequestIdsNotFound() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        List<Item> itemList = itemRepository
                .findAllByRequestIds(List.of(100L, 101L, 102L));

        AssertionErrors.assertEquals("There should have been 0 Item in the list", 0, itemList.size());
    }
}
