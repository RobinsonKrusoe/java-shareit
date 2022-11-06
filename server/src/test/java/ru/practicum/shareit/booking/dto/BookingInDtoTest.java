package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class BookingInDtoTest {

    @Autowired
    private JacksonTester<BookingInDto> json;

    @Test
    void testBookingInDto() throws Exception {
        LocalDateTime startBooking = LocalDateTime.now().plusHours(2);
        LocalDateTime endBooking = LocalDateTime.now().plusHours(24);

        BookingInDto bookingInDto = BookingInDto.builder()
                .itemId(1L)
                .start(startBooking)
                .end(endBooking)
                .build();

        JsonContent<BookingInDto> res = json.write(bookingInDto);

        assertThat(res).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.start")
                .isEqualTo(startBooking.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(res).extractingJsonPathStringValue("$.end")
                .isEqualTo(endBooking.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));


    }
}