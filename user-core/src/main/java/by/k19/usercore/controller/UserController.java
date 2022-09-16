package by.k19.usercore.controller;

import by.k19.usercore.repos.UserRepository;
import by.k19.usercore.service.UserService;
import by.k19.usercore.validation.UserError;
import by.k19.usercore.validation.UserErrorBuilder;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends LoggingController{

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> findAll(HttpServletRequest request) {
        log.info("Запрос всех пользователей: " + getRemoteAddress(request));
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            log.info("Запрос пользователя с id " + id + " : " + getRemoteAddress(request) + " | Успешно");
            return ResponseEntity.ok(optionalUser.get());
        }
        log.info("Запрос пользователя с id " + id + " : " + getRemoteAddress(request) + " | Отказано");
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PATCH})
    public ResponseEntity<?> saveOrUpdate(@Valid @RequestBody User user, Errors errors, HttpServletRequest request) {
        if (errors.hasErrors()) {
            log.info("Запрос создания пользователя: " +getRemoteAddress(request) + " | Отказано");
            return ResponseEntity.badRequest().body(UserErrorBuilder.fromBindingErrors(errors));
        }
        User saved = null;
        if (user.getId() != null) {
            Optional<User> existed = userRepository.findById(user.getId());
            if (existed.isPresent()) {
                User existedUser = existed.get();
                existedUser = userService.updateUserByUser(existedUser, user);
                saved = userRepository.saveAndFlush(existedUser);
            }
        }
        if (saved == null)
            saved = userRepository.saveAndFlush(user);
        log.info("Запрос создания пользователя: " +getRemoteAddress(request) + " | Успешно: " + saved);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id, HttpServletRequest request) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            log.info("Запрос удаления пользователя с id " + id + " : " +getRemoteAddress(request));
            return ResponseEntity.ok().build();
        }
        else return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody User user, HttpServletRequest request) {
        log.info("Запрос удаления пользователя " + user + " : " +getRemoteAddress(request));
        return deleteById(user.getId(), request);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UserError exceptionHandler(Exception exception) {
        return new UserError(exception.getMessage());
    }
}
