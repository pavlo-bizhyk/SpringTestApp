package org.cs.assignment.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.cs.assignment.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Value("${email.regexp}")
    private String emailRegex;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User saveUser(User user) {
        if (!user.getEmail().matches(emailRegex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        }
        if (user.getBirthDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birth date should be earlier than current date");
        }
        entityManager.persist(user);
        return user;
    }

    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    public void deleteUserById(Long id) {
        User user = entityManager.find(User.class, id);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with such id does not exist");
        }
        entityManager.remove(user);
    }

    public List<User> getAllUsers() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    public List<User> getUsersByDateRange(LocalDate fromDate, LocalDate toDate) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.birthDate BETWEEN :fromDate AND :toDate", User.class)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();
    }

}
