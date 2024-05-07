package org.cs.assignment.controller;

import org.cs.assignment.model.User;
import org.cs.assignment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${minAge}")
    private int minimumAge;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        LocalDate requiredMinimumDate = LocalDate.now().minusYears(minimumAge);

        if (!user.getBirthDate().isBefore(requiredMinimumDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User should be 18 years old or older");
        }

        User createdUser = userRepository.saveUser(user);

        return ResponseEntity.created(URI.create("/users/" + createdUser.getId()))
                .body(createdUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateSomeUserFields(@RequestBody User userFieldsToPatch, @PathVariable Long id) {

        LocalDate requiredMinimumDate = LocalDate.now().minusYears(minimumAge);

        if (Objects.nonNull(userFieldsToPatch.getBirthDate()) && !userFieldsToPatch.getBirthDate().isBefore(requiredMinimumDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User should be 18 years old or older");
        }

        User user = userRepository.getUserById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with such id does not exist");
        }

        updateFields(user, userFieldsToPatch);
        userRepository.saveUser(user);

        return ResponseEntity.ok()
                .body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateAllUserFields(@RequestBody User userFieldsToPatch, @PathVariable Long id) {

        LocalDate requiredMinimumDate = LocalDate.now().minusYears(minimumAge);

        if (Objects.nonNull(userFieldsToPatch.getBirthDate()) && !userFieldsToPatch.getBirthDate().isBefore(requiredMinimumDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User should be 18 years old or older");
        }

        User user = userRepository.getUserById(id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with such id does not exist");
        }

        updateFields(user, userFieldsToPatch);
        userRepository.saveUser(user);

        return ResponseEntity.ok()
                .body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userRepository.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getUsersByDateRange(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        if (fromDate == null && toDate == null) {
            return ResponseEntity.ok().body(userRepository.getAllUsers());
        }

        if (fromDate == null || toDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both from and to params should be included when searching by birth date range");
        }

        if (!fromDate.isBefore(toDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "From date parameter must be before To date parameter");
        }

        return ResponseEntity.ok().body(userRepository.getUsersByDateRange(fromDate, toDate));
    }

    private void updateFields(User user, User fieldsToUpdate) {
        Field[] fields = fieldsToUpdate.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                Field userField = user.getClass().getDeclaredField(field.getName());
                field.setAccessible(true);
                Object value = field.get(fieldsToUpdate);
                if (value != null) {
                    userField.setAccessible(true);
                    userField.set(user, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        });
    }
}
