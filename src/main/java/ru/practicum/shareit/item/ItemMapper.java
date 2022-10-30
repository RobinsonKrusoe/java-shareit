package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item != null) {
            return ItemDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.isAvailable())
                    .owner(UserMapper.toUserDto(item.getOwner()))
                    .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                    .build();
        } else {
            return null;
        }
    }

    public static Item toItem(ItemDto itemDto) {
        if (itemDto != null) {
            Item item = new Item();
            if (itemDto.getId() != null) {
                item.setId(itemDto.getId());
            }
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setOwner(UserMapper.toUser(itemDto.getOwner()));
            item.setAvailable(itemDto.getAvailable());
            return item;
        } else {
            return null;
        }
    }
}
