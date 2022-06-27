package com.geopos.services;

import com.geopos.db.User;
import com.geopos.entity.UserDto;
import com.geopos.repository.UserRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MicronautTest
class UserServiceTest {

    @Inject
    UserService userService;

    @Inject
    UserRepository mockedRepository;

    @MockBean(UserRepository.class)
    UserRepository userRepository() {
        return mock(UserRepository.class);
    }

    @Test
    void testSaveUser() {
        UserDto user = UserDto.builder()
                .id(1L)
                .userId("userId")
                .createdOn(new Date())
                .email("email")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        userService.saveUser(user);
        verify(mockedRepository, times(1)).save(any());
    }

    @Test
    void testGetUserIdByExternalId() {
        when(mockedRepository.findIdByUserId("userId")).thenReturn(Optional.of(1L));
        Optional<Long> userId = userService.getUserIdByExternalId("userId");

        assertTrue(userId.isPresent());
        assertEquals(1L, userId.get());
        verify(mockedRepository, times(1)).findIdByUserId("userId");
    }

    @Test
    void getUser() {
        User user = User.builder()
                .id(1L)
                .userId("userId")
                .createdOn(Instant.now())
                .email("email")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        when(mockedRepository.findByUserId("userId")).thenReturn(Optional.of(user));

        UserDto result = userService.getUser("userId");
        assertNotNull(result);
        verify(mockedRepository, times(1)).findByUserId("userId");
    }
}