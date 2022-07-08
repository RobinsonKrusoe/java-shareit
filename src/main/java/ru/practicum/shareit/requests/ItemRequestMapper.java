package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest){
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .build();
    }
}
