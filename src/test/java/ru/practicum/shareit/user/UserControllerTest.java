package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;

    private final UserDto userDtoIn = UserDto.builder()
            .name("UserOne")
            .email("UserOne@mail.tst")
            .build();

    private final UserDto userDtoOut = UserDto.builder()
            .id(1L)
            .name("UserOne")
            .email("UserOne@mail.tst")
            .build();

    private final UserDto userDtoOutTwo = UserDto.builder()
            .id(2L)
            .name("UserTwo")
            .email("UserTwo@mail.tst")
            .build();

    @Test
    void postUser() throws Exception {
        Mockito.when(userService.add(userDtoIn)).thenReturn(userDtoOut);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOut.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDtoOut.getEmail()), String.class));
    }

    @Test
    void getUser() throws Exception {
        Mockito.when(userService.getDto(anyLong())).thenReturn(userDtoOut);
        mockMvc.perform(get("/users/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOut.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDtoOut.getEmail()), String.class));
    }

    @Test
    void getAllUsers() throws Exception {
        Mockito.when(userService.getAll()).thenReturn(List.of(userDtoOut, userDtoOutTwo));
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDtoOut, userDtoOutTwo))));
    }

    @Test
    void patchUser() throws Exception {
        Mockito.when(userService.patch(userDtoOut)).thenReturn(userDtoOut);
        mockMvc.perform(patch("/users/" + userDtoOut.getId())
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoOut.getEmail())));
    }

    @Test
    void delUser() throws Exception {
        mockMvc.perform(delete("/users/" + anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
