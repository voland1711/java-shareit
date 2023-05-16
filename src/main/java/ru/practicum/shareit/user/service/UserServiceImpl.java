package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.model.UserMapper.toUser;
import static ru.practicum.shareit.user.model.UserMapper.toUserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        log.info("Поступил запрос на получения списка пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long userId) {
        log.info("Поступил запрос на получение пользователя userId = {}", userId);
        User tempUser = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id: " + userId + " не найден"));
        return toUserDto(tempUser);
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Поступил запрос на создание пользователя");
        User tempUser = toUser(userDto);
        userRepository.save(tempUser);
        return toUserDto(tempUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        log.info("Поступил запрос на обновление пользователя - {}", userId);
        User tempUser = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с id = " + userId + " найден"));
        tempUser.setId(userId);
        if (!StringUtils.isBlank(userDto.getEmail())) {
            tempUser.setEmail(userDto.getEmail());
        }
        if (!StringUtils.isBlank(userDto.getName())) {
            tempUser.setName(userDto.getName());
        }
        return toUserDto(tempUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long userIid) {
        log.info("Поступил запрос на удаления пользователя - {}", userIid);
        userRepository.deleteById(userIid);
    }

}
