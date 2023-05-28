package ru.practicum.shareit.request.model;

import lombok.NonNull;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

public class ItemRequestMapper {
    public static ItemRequestResponseDto toItemRequestResponseDto(@NonNull ItemRequest itemRequest) {
        return ItemRequestResponseDto
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(@NonNull ItemRequestDto itemRequestDto) {
        return ItemRequest
                .builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

}
