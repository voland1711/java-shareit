package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private long nextUserId = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        existUser(userId);
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        validationUser(user);
        long userId = getNextId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Пользователь - {}, успешно создан", userId);
        return user;
    }

    @Override
    public User updateUser(User user, Long userId) {
        if (StringUtils.isBlank(user.getEmail()) && StringUtils.isEmpty(user.getEmail())) {
            user.setEmail(getUserById(userId).getEmail());
        }
        if (usedEmail(user, userId)) {
            throw new ValidationException("Адрес электронной почты уже зарегистрирован");
        }
        if (StringUtils.isBlank(user.getName()) && StringUtils.isEmpty(user.getName())) {
            user.setName(getUserById(userId).getName());
        }

        user.setId(userId);
        users.put(userId, user);
        log.info("Пользователя - {}, успешно обновлен", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
        log.info("Пользователь - {} удален из коллекции.", userId);
    }

    private long getNextId() {
        return ++nextUserId;
    }

    private void validationUser(User user) {
        if (StringUtils.isBlank(user.getName()) && StringUtils.isEmpty(user.getName())) {
            throw new ValidationException("Логин не должен быть пустым/содержать пробелы");
        }

        if (StringUtils.isBlank(user.getEmail()) && StringUtils.isEmpty(user.getEmail())) {
            throw new EmailNotFoundException("Отсутсвует адрес электронной почты");
        }

        if (usedEmail(user, user.getId())) {
            throw new ValidationException("Адрес электронной почты уже зарегистрирован");
        }

    }

    private void existUser(long userId) {
        if (!users.containsKey(userId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }

    private boolean usedEmail(User user, long userId) {
        return users.values().stream()
                .filter(tempUser -> tempUser.getId() != userId)
                .filter(tempUser -> tempUser.getEmail().contains(user.getEmail()))
                .collect(Collectors.toList()).size() > 0;
    }

}
