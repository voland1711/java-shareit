package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(long userId);

    ItemDto getItemById(long userId, long itemId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, Item item, long itemId);

    List<ItemDto> searchItems(long userId, String text);

}
