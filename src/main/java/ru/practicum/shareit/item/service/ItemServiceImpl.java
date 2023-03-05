package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public List<ItemDto> getAllItems(long userId) {
        User tempUser = userRepository.getUserById(userId);
        return itemRepository.getAllItems(tempUser).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public ItemDto createItem(long userId, Item item) {
        validationItem(item);
        User tempUser = userRepository.getUserById(userId);
        log.info("Вещь - {}, создана", item.getName());
        return ItemMapper.toItemDto(itemRepository.createItem(tempUser, item));
    }

    @Override
    public ItemDto updateItem(long userId, Item item, long itemId) {

        Item oldItem = ItemMapper.toItem(getItemById(userId, itemId));
        if (oldItem.getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Обновить данные вещи может только ее владелец");
        }
        if (userRepository.getUserById(userId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        item.setId(itemId);
        Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);
        Optional.ofNullable(item.getOwner()).ifPresent(oldItem::setOwner);
        Optional.ofNullable(item.getRequest()).ifPresent(oldItem::setRequest);
        log.info("Вещь отправлена для обновления.");
        return ItemMapper.toItemDto(itemRepository.updateItem(oldItem, itemId));
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validationItem(Item item) {
        if (item.getAvailable() == null) {
            throw new ItemNotFoundException("Отсутсвует параметр available");
        }
        if (StringUtils.isEmpty(item.getName())) {
            throw new ValidationException("Имя вещи пустое/содержит пробелы");
        }
        if (StringUtils.isEmpty(item.getDescription())) {
            throw new ValidationException("Описание вещи пустое/содержит пробелы");
        }
    }

}
