package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Поступил запрос на получения списка пользователей");
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userIid) {
        return UserMapper.toUserDto(userRepository.getUserById(userIid));
    }

    @Override
    public UserDto createUser(User user) {
        log.info("Поступил запрос на создание пользователя");
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(User user, Long userIid) {
        log.info("Поступил запрос на обновление пользователя - {}", userIid);
        return UserMapper.toUserDto(userRepository.updateUser(user, userIid));
    }

    @Override
    public void deleteUser(Long userIid) {
        log.info("Поступил запрос на удаления пользователя - {}", userIid);
        userRepository.deleteUser(userIid);
    }

}
