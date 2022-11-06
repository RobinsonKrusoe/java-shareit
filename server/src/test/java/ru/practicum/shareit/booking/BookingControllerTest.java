package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.mockito.Mockito;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("UserOne")
            .email("UserOne@mail.tst")
            .build();

    private final UserDto userDtoTwo = UserDto.builder()
            .id(2L)
            .name("UserTwo")
            .email("UserTwo@mail.tst")
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Аккумуляторная дрель")
            .description("Аккумуляторная дрель + аккумулятор")
            .available(true)
            .owner(userDto)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.parse("2022-12-12T01:01:01"))
            .end(LocalDateTime.parse("2022-12-12T05:00:00"))
            .item(itemDto)
            .booker(userDtoTwo)
            .status("APPROVED")
            .build();

    private final BookingDto bookingDtoTwo = BookingDto.builder()
            .id(2L)
            .start(LocalDateTime.parse("2022-12-11T01:01:01"))
            .end(LocalDateTime.parse("2022-12-11T05:00:00"))
            .item(itemDto)
            .booker(userDtoTwo)
            .status("APPROVED")
            .build();
    private final BookingInDto bookingInDto = BookingInDto.builder()
            .itemId(1L)
            .start(LocalDateTime.parse("2022-12-12T01:01:01"))
            .end(LocalDateTime.parse("2022-12-12T05:00:00"))
            .build();

    @Test
    void postBooking() throws Exception {
        Mockito.when(bookingService.add(bookingInDto, 1L)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void updateStatus() throws Exception {
        Mockito.when(bookingService.updateStatus(1L, 1L, true)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/" + 1)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void getBooking() throws Exception {
        Mockito.when(bookingService.getBooking(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/" + 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    void findUserBookings() throws Exception {
        Mockito.when(bookingService.findUserBookings("ALL", 1L, 0, 10))
                .thenReturn(List.of(bookingDto, bookingDtoTwo));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto, bookingDtoTwo))));
    }

    @Test
    void findOwnerBookings() throws Exception {
        Mockito.when(bookingService.findOwnerBookings("ALL", 1L, 0, 10))
                .thenReturn(List.of(bookingDto, bookingDtoTwo));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto, bookingDtoTwo))));
    }
}
