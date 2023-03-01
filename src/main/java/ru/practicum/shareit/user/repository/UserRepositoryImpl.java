package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private long nextUserId = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersUsedEmail = new HashSet<>();
    private final Set<String> listUsersRegistred = new HashSet<>();

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
        usersUsedEmail.add(user.getEmail());
        listUsersRegistred.add(user.getName());
        log.info("Пользователь - {}, успешно создан", userId);
        return user;
    }

    @Override
    public User updateUser(User user, Long userId) {
        if (StringUtils.isBlank(user.getEmail()) && StringUtils.isEmpty(user.getEmail())) {
            user.setEmail(getUserById(userId).getEmail());
        } else {
            usersUsedEmail.remove(getUserById(userId).getEmail());
            if (usersUsedEmail.contains(user.getEmail())) {
                throw new ValidationException("Адрес электронной почты уже зарегистрирован");
            }
        }

        if (StringUtils.isBlank(user.getName()) && StringUtils.isEmpty(user.getName())) {
            user.setName(getUserById(userId).getName());
        } else {
            listUsersRegistred.remove(getUserById(userId).getName());
        }

        user.setId(userId);
        users.put(userId, user);
        usersUsedEmail.add(user.getEmail());
        listUsersRegistred.add(user.getName());
        log.info("Пользователя - {}, успешно обновлен", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        usersUsedEmail.remove(getUserById(userId).getEmail());
        listUsersRegistred.remove(getUserById(userId).getName());
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

        if (usersUsedEmail.contains(user.getEmail())) {
            throw new ValidationException("Адрес электронной почты уже зарегистрирован");
        }

        if (listUsersRegistred.contains(user.getName())) {
            throw new ValidationException("Пользователь уже зарегистрирован");
        }
    }

    private void existUser(long userId) {
        if (!users.containsKey(userId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
    }
}
