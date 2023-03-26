package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }


}
