package ru.practicum.shareit.item.model;

import lombok.NonNull;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public static ItemDto toItemDto(@NonNull Item item) {
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(@NonNull ItemDto itemDto) {
        return Item
                .builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }


}
