package com.geopos.repository;

import com.geopos.db.User;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class UserRepositoryIT {

    @Inject
    UserRepository userRepository;

    @Test
    void testSaveAndFindByUserId() {
        long now = System.currentTimeMillis();
        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .createdOn(Instant.ofEpochMilli(now))
                .email("email")
                .firstName("firstName")
                .secondName("secondName")
                .build();

        userRepository.save(user);

        Optional<User> result = userRepository.findByUserId(user.getUserId());
        assertTrue(result.isPresent());
        User resultUser = result.get();
        assertEquals(now, resultUser.getCreatedOn().toEpochMilli());
        assertTrue(resultUser.getId() > 0);
        assertEquals(user.getUserId(), resultUser.getUserId());
        assertEquals(user.getEmail(), resultUser.getEmail());
        assertEquals(user.getFirstName(), resultUser.getFirstName());
        assertEquals(user.getSecondName(), resultUser.getSecondName());
    }

    @Test
    void testSaveAndFindById() {
        long now = System.currentTimeMillis();
        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .createdOn(Instant.ofEpochMilli(now))
                .email("email")
                .firstName("firstName")
                .secondName("secondName")
                .build();

        userRepository.save(user);

        Optional<User> result = userRepository.findById(user.getId());
        assertTrue(result.isPresent());
        User resultUser = result.get();
        assertEquals(now, resultUser.getCreatedOn().toEpochMilli());
        assertTrue(resultUser.getId() > 0);
        assertEquals(user.getUserId(), resultUser.getUserId());
        assertEquals(user.getEmail(), resultUser.getEmail());
        assertEquals(user.getFirstName(), resultUser.getFirstName());
        assertEquals(user.getSecondName(), resultUser.getSecondName());
    }

    @Test
    void testFindIdByUserIdUserIdIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userRepository.findIdByUserId(null);
        });
    }

    @Test
    void testFindByUserIdNotExistingUser() {
        Optional<User> user = userRepository.findByUserId("not-existing-id");
        assertTrue(user.isEmpty());
    }

    @Test
    void testFindByUserIdAndTimeRangeUserIdIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userRepository.findByUserId(null);
        });
    }

    @Test
    void testFindByUserIdAndTimeRangeNotExistingUser() {
        Optional<Long> user = userRepository.findIdByUserId("not-existing-id");
        assertTrue(user.isEmpty());
    }
}