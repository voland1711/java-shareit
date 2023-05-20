package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.model.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.model.ItemRequestMapper.toItemRequestResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final Sort requestsSort = Sort.by(Sort.Direction.DESC, "created");

    @Transactional(readOnly = true)
    @Override
    public ItemRequestResponseDto getByIdItemRequest(Long userId, Long requestId) {
        log.info("Работает: ItemRequestServiceImp.getByIdItemRequest");
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id = " + userId + "  не найден"));
        ItemRequestResponseDto itemRequestResponseDto = toItemRequestResponseDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос с id = " + requestId + " не найден")));

        List<ItemDto> itemDtoList = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
        itemRequestResponseDto.setItems(itemDtoList);
        return itemRequestResponseDto;
    }

    @Transactional
    @Override
    public ItemRequestResponseDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Работает: ItemRequestServiceImp.createItemRequest");
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id = " + userId + "  не найден"));
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setRequester(requester);
        ItemRequestResponseDto itemRequestResponseDto = toItemRequestResponseDto(itemRequestRepository.save(itemRequest));
        log.info("Закончил работу: ItemRequestServiceImp.createItemRequest");
        return itemRequestResponseDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestResponseDto> getAllItemRequest(Long userId, Integer from, Integer size) {
        log.info("Работает: ItemRequestServiceImp.getAllItemRequest");
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id = " + userId + "  не найден"));

        Pageable pageable = PageRequest.of(from / size, size, requestsSort);

        List<ItemRequestResponseDto> itemRequestResponseDtos = itemRequestRepository
                .findAllByRequesterIdNot(userId, pageable).stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .sorted(Comparator.comparing(ItemRequestResponseDto::getCreated))
                .collect(Collectors.toList());

        return getItemsFoRequestResponseDto(itemRequestResponseDtos);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestResponseDto> getAllByRequester(Long userId, Integer from, Integer size) {
        log.info("Работает: ItemRequestServiceImp.getAllByRequester");
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id = " + userId + "  не найден"));

        Pageable pageable = PageRequest.of(from / size, size, requestsSort);

        List<ItemRequestResponseDto> itemRequestResponseDtos = itemRequestRepository
                .findAllByRequesterId(userId, pageable).stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .collect(Collectors.toList());

        return getItemsFoRequestResponseDto(itemRequestResponseDtos);
    }

    private List<ItemRequestResponseDto> getItemsFoRequestResponseDto(List<ItemRequestResponseDto> itemRequestResponseDtos) {
        Map<Long, List<ItemDto>> itemsMap = itemRepository.findAllByRequestIdIn(
                        itemRequestResponseDtos.stream()
                                .map(ItemRequestResponseDto::getId)
                                .collect(Collectors.toList())
                )
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequestResponseDtos.stream().peek(itemRequestResponseDto -> {
            List<ItemDto> itemDtos = itemsMap.getOrDefault(itemRequestResponseDto.getId(), Collections.emptyList());
            itemRequestResponseDto.setItems(itemDtos);
        }).collect(Collectors.toList());
    }



}



