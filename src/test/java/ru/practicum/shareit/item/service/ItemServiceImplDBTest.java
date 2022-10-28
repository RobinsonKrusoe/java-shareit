package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplDBTest {
    private final EntityManager em;
    private final ItemServiceImpl itemService;

    @AfterEach
    void afterEach() {
        em.createNativeQuery("truncate items restart identity cascade");
        em.createNativeQuery("truncate users restart identity cascade");
    }

    @Test
    void testAddDB() {
        User userOne = new User(0L, "UserOne", "UserOne@mail.tst");
        em.persist(userOne);

        ItemDto itemDto = ItemDto.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .build();

        itemDto = itemService.add(itemDto, 1L);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.getAvailable());
    }
}
