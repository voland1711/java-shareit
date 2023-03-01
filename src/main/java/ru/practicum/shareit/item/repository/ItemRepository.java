package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllItems(User user);

    Item getItemById(long itemId);

    Item createItem(User user, Item item);

    Item updateItem(Item item, long itemId);

    List<Item> searchItems(String text);
}
