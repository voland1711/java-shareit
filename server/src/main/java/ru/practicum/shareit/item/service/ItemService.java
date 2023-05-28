package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(long userId);

    ItemDto getItemById(long userId, long itemId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> searchItems(long userId, String text);

}
