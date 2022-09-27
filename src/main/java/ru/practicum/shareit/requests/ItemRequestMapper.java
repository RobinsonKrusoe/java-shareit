package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest){
        if(itemRequest != null) {
            return ItemRequestDto.builder()
                    .id(itemRequest.getId())
                    .description(itemRequest.getDescription())
                    .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                    .created(itemRequest.getCreated())
                    .build();
        } else {
            return null;
        }
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto){
        if (itemRequestDto != null) {
            ItemRequest itemRequest = new ItemRequest();

            itemRequest.setId(itemRequestDto.getId());
            itemRequest.setDescription(itemRequestDto.getDescription());
            if(itemRequestDto.getRequestor() != null){
                itemRequest.setRequestor(UserMapper.toUser(itemRequestDto.getRequestor()));
            }
            itemRequest.setCreated(itemRequestDto.getCreated());

            return itemRequest;
        } else {
            return null;
        }
    }
}
