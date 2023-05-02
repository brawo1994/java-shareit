package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.AssertionErrors;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.List;

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
            .name("Ivan")
            .email("ivan@ya.ru")
            .build();

    private final Request request = Request.builder()
            .description("Description")
            .owner(user)
            .created(LocalDateTime.now())
            .build();

    @Test
    void testContextLoads() {
        assertNotNull(entityManager);
    }

    @Test
    void testFindAllByPageable() {
        entityManager.persist(user);
        entityManager.persist(request);

        List<Request> requestList = requestRepository
                .findAllWhereOwnerNotCurrentUserByPageable(99999, pageable);

        AssertionErrors.assertEquals("There should have been 1 Request in the list", 1, requestList.size());
        AssertionErrors.assertEquals("There should have been " + List.of(request), List.of(request), requestList);
    }
}
