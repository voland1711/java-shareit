package ru.practicum.shareit.item.model;

import lombok.NonNull;
import ru.practicum.shareit.item.ItemDto;

public class ItemMapper {
    public static ItemDto toItemDto(@NonNull Item item) {
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId((item.getRequest() == null ? null : item.getRequest().getId()))
                .build();
    }

    public static Item toItem(@NonNull ItemDto itemDto) {
        return Item
                .builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }


}
