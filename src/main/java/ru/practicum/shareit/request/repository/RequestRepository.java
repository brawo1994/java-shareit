package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByOwnerId(long userId);

    @Query("select request from Request request " +
            "where request.owner.id <> ?1")
    List<Request> findAllWhereOwnerNotCurrentUserByPageable(long userId, Pageable pageable);
}