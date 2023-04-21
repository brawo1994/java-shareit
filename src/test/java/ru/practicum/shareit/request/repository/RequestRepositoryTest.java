package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase
class RequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private RequestRepository requestRepository;

    private final Pageable pageable = new Pagination(0, 10, Sort.unsorted());

    private final User user = User.builder()
            //.id(1L)
            .name("Ivan")
            .email("ivan@ya.ru")
            .build();

    private final Request request = Request.builder()
            //.id(1L)
            .description("Description")
            .owner(user)
            .created(LocalDateTime.now())
            .build();

    @Test
    void contextLoadsTest() {
        assertNotNull(entityManager);
    }

    @Test
    void findAllByPageableTest() {
        entityManager.persist(user);
        entityManager.persist(request);

        List<Request> requestList = requestRepository
                .findAllWhereOwnerNotCurrentUserByPageable(99999, pageable);

        assertEquals(requestList.size(), 1);
        assertEquals(List.of(request), requestList);
    }
}
