package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void contextLoadsTest() {
        assertNotNull(entityManager);
    }

    @Test
    void searchTest() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        List<Item> itemList = itemRepository.findItemsByText("item", pageable);

        assertEquals(itemList.size(), 1);
    }

    @Test
    void searchNotFoundTest() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        List<Item> itemList = itemRepository.findItemsByText("itemmmm", pageable);

        assertEquals(itemList.size(), 0);
    }

    @Test
    void findAllByRequestIdsTest() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        List<Item> itemList = itemRepository
                .findAllByRequestIds(List.of(request.getId()));

        assertEquals(itemList.size(), 1);
    }

    @Test
    void findAllByRequestIdsNotFoundTest() {
        entityManager.persist(user);
        entityManager.persist(request);
        entityManager.persist(item);
        List<Item> itemList = itemRepository
                .findAllByRequestIds(List.of(100L, 101L, 102L));

        assertEquals(itemList.size(), 0);
    }
}
