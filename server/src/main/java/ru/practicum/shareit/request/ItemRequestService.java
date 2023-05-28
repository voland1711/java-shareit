package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto getByIdItemRequest(Long userId, Long requestId);

    ItemRequestResponseDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getAllItemRequest(Long userId, Integer from, Integer size);

    List<ItemRequestResponseDto> getAllByRequester(Long userId, Integer from, Integer size);
}
