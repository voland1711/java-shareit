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
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription()== null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        if (item.getOwner() == null) {
            item.setOwner(oldItem.getOwner());
        }
        if (item.getRequest() == null) {
            item.setRequest(oldItem.getRequest());
        }
        log.info("Вещь отправления для обновления.");
        return ItemMapper.toItemDto(itemRepository.updateItem(item, itemId));
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        if(text.isEmpty()){
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
