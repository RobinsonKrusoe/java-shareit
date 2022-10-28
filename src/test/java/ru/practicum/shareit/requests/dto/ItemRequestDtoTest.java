package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.now();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("UserOne")
                .email("UserOne@mail.tst")
                .build();

        UserDto userDtoTwo = UserDto.builder()
                .id(2L)
                .name("UserTwo")
                .email("UserTwo@mail.tst")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .owner(userDto)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться аккумуляторной дрелью")
                .requestor(userDtoTwo)
                .created(created)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestDto> res = json.write(itemRequestDto);

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.description")
                .isEqualTo("Хотел бы воспользоваться аккумуляторной дрелью");
        assertThat(res).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(2);
        assertThat(res).extractingJsonPathStringValue("$.requestor.name").isEqualTo("UserTwo");
        assertThat(res).extractingJsonPathStringValue("$.requestor.email").isEqualTo("UserTwo@mail.tst");

    }
}