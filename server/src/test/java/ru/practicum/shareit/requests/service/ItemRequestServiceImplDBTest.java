package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplDBTest {
    private final EntityManager em;
    private final ItemRequestServiceImpl service;

    @AfterEach
    void afterEach() {
        em.createNativeQuery("truncate item_requests restart identity cascade;");
        em.createNativeQuery("truncate items restart identity cascade");
        em.createNativeQuery("truncate users restart identity cascade");
    }

    @Test
    void testAddDB() {
        User userOne = new User(0L, "UserOne", "UserOne@mail.tst");
        em.persist(userOne);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться аккумуляторной дрелью")
                .build();

        itemRequestDto = service.add(itemRequestDto, 1L);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id",
                                                            ItemRequest.class);
        ItemRequest itemRequest = query
                .setParameter("id", itemRequestDto.getId())
                .getSingleResult();

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
    }
}
