package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private long nextItemId = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getAllItems(User user) {
        log.info("Поступил запрос на получение всех вещей у пользователя - {}", user.getName());
        return new ArrayList(items.values().stream()
                .filter(item -> item.getOwner().equals(user))
                .collect(Collectors.toList()));
    }

    @Override
    public Item getItemById(long itemId) {
        existItem(itemId);
        log.info("Поступил запрос на получение веще с id = {}", itemId);
        return items.get(itemId);
    }

    @Override
    public Item createItem(User user, Item item) {
        item.setId(getNextId());
        item.setOwner(user);
        items.put(item.getId(), item);
        log.info("Вещь создана");
        return item;
    }

    @Override
    public Item updateItem(Item item, long itemId) {
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return new ArrayList(items.values().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text))
                .filter(item -> item.getAvailable())
                .collect(Collectors.toList()));
    }

    private long getNextId() {
        return ++nextItemId;
    }

    private void existItem(long userId) {
        if (!items.containsKey(userId)) {
            throw new ObjectNotFoundException("Вещь отсутствует в коллекции");
        }
    }

}
