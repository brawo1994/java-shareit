package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.AssertionErrors;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Pagination;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final UserService userService;
    private final ItemService itemService;

    @Test
    void testSearchItemsByText() {

        final Pageable pageable = new Pagination(0, 10, Sort.unsorted());

        UserDto userDto = UserDto.builder()
                .name("Ivan")
                .email("ivan3@ya.ru")
                .build();
        UserDto resultUserDto = userService.createUser(userDto);

        ItemDto itemDto = ItemDto.builder()
                .name("Item Name")
                .description("Item for Petr")
                .available(true)
                .build();
        ItemDto resultItemDto = itemService.createItem(resultUserDto.getId(), itemDto);

        List<ItemDto> itemDtoList = itemService.searchItemsByText("Petr", pageable);

        AssertionErrors.assertEquals("There should have been 1 Item in the list", 1, itemDtoList.size());
        AssertionErrors.assertEquals("There should have been " + resultItemDto,
                resultItemDto, itemDtoList.get(0));
    }
}
