package by.k19.usercore.service;

import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private static final List<String> nonUpdatableFields = Arrays.asList("id", "created", "modified");

    public User updateUserByUser(User updated, User value) {
        for (Field field : User.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (!nonUpdatableFields.contains(field.getName()) && field.get(value) != null) {
                    field.set(updated, field.get(value));
                }
            } catch (IllegalAccessException e) {
                log.info(e.getMessage() + " | " + field.getName());
            }
        }
        return updated;
    }
}
