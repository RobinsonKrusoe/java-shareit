package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime startBooking = LocalDateTime.now().plusHours(2);
        LocalDateTime endBooking = LocalDateTime.now().plusHours(24);

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

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .item(itemDto)
                .start(startBooking)
                .end(endBooking)
                .booker(userDtoTwo)
                .status("WAITING")
                .build();

        JsonContent<BookingDto> res = json.write(bookingDto);

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.start")
                .isEqualTo(startBooking.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(res).extractingJsonPathStringValue("$.end")
                .isEqualTo(endBooking.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(res).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.item.name").isEqualTo("Аккумуляторная дрель");
        assertThat(res).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("Аккумуляторная дрель + аккумулятор");
        assertThat(res).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(res).extractingJsonPathStringValue("$.booker.name").isEqualTo("UserTwo");
        assertThat(res).extractingJsonPathStringValue("$.booker.email").isEqualTo("UserTwo@mail.tst");
        assertThat(res).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}