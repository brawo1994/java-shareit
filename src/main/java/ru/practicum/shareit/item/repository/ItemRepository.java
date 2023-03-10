package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = TRUE " +
            "AND (lower(i.name) LIKE %?1% " +
            "OR lower(i.description) LIKE %?1%)")
    List<Item> findItemsByText(String text);
}
