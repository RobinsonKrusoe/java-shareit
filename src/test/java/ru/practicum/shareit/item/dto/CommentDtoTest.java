package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("UserOne")
                .email("UserOne@mail.tst")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .owner(userDto)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Отлично")
                .item(itemDto)
                .authorName("UserOne")
                .build();

        JsonContent<CommentDto> res = json.write(commentDto);

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.text").isEqualTo("Отлично");
        assertThat(res).extractingJsonPathStringValue("$.authorName").isEqualTo("UserOne");
        assertThat(res).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.item.name").isEqualTo("Аккумуляторная дрель");
        assertThat(res).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("Аккумуляторная дрель + аккумулятор");
    }
}